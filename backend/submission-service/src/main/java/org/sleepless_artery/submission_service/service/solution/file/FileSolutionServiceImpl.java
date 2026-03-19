package org.sleepless_artery.submission_service.service.solution.file;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.submission_service.config.cache.CacheConfig;
import org.sleepless_artery.submission_service.dto.request.FileSubmissionRequestDto;
import org.sleepless_artery.submission_service.dto.response.FileSubmissionResponseDto;
import org.sleepless_artery.submission_service.exception.SubmissionAlreadyExistsException;
import org.sleepless_artery.submission_service.exception.SubmissionNotFoundException;
import org.sleepless_artery.submission_service.logging.annotations.BusinessEvent;
import org.sleepless_artery.submission_service.logging.event.LogEvent;
import org.sleepless_artery.submission_service.mapper.FileSubmissionMapper;
import org.sleepless_artery.submission_service.model.file.FileSubmission;
import org.sleepless_artery.submission_service.repository.file.FileSubmissionRepository;
import org.sleepless_artery.submission_service.service.infrastructure.minio.MinioService;
import org.sleepless_artery.submission_service.service.validation.existence.CommonExistenceValidator;
import org.sleepless_artery.submission_service.service.util.FileKeyGenerator;
import org.sleepless_artery.submission_service.service.validation.file.FileValidator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


/**
 * Default implementation of {@link FileSolutionService}.
 * <p>
 * Manages file-based submissions.
 */
@Service
@RequiredArgsConstructor
public class FileSolutionServiceImpl implements FileSolutionService {

    private final FileSubmissionRepository submissionRepository;
    private final FileSubmissionMapper submissionMapper;
    private final FileValidator fileValidator;
    private final FileKeyGenerator fileKeyGenerator;
    private final CommonExistenceValidator existenceValidator;
    private final MinioService minioService;


    /**
     * Retrieves a file submission by its identifier.
     *
     * @param id submission identifier
     * @return file submission DTO
     */
    @Override
    public FileSubmissionResponseDto findSubmissionById(Long id) {
        return submissionMapper.toDto(findById(id));
    }


    /**
     * Uploads a new file submission.
     *
     * @param requestDto submission request data
     * @param file uploaded file
     * @return created submission DTO
     *
     * @throws SubmissionAlreadyExistsException if submission with the provided data already exists
     */
    @Override
    @BusinessEvent(LogEvent.SUBMISSION_CREATION)
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.ASSIGNMENT_CACHE_NAME, key = "#requestDto.assignmentId")
    public FileSubmissionResponseDto uploadSolution(FileSubmissionRequestDto requestDto, MultipartFile file) {

        var assignmentId = requestDto.getAssignmentId();
        var studentId = requestDto.getStudentId();

        existenceValidator.validateExistence(assignmentId, studentId);

        if (submissionRepository.existsByAssignmentIdAndStudentId(assignmentId, studentId)) {
            throw new SubmissionAlreadyExistsException(
                    "Submission with assignment ID: " + assignmentId +
                            " already exists for student with ID: " + studentId
            );
        }

        fileValidator.validate(file);

        var fileKey = fileKeyGenerator.generate(requestDto.getAssignmentId(), studentId, file);
        minioService.upload(file, fileKey);

        var submission = submissionMapper.toEntity(requestDto);
        submission.setFileKey(fileKey);

        return submissionMapper.toDto(submissionRepository.save(submission));
    }


    /**
     * Updates an existing submission with a new file.
     *
     * @param id submission identifier
     * @param requestDto updated submission data
     * @param file new file
     * @return updated submission DTO
     */
    @Override
    @BusinessEvent(LogEvent.SUBMISSION_UPDATE)
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.SUBMISSION_CACHE_NAME, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.ASSIGNMENT_CACHE_NAME, key = "#requestDto.assignmentId")
    })
    public FileSubmissionResponseDto updateSolution(Long id, FileSubmissionRequestDto requestDto, MultipartFile file) {
        var existingSubmission = findById(id);

        fileValidator.validate(file);

        minioService.remove(existingSubmission.getFileKey());
        var fileKey = fileKeyGenerator.generate(requestDto.getAssignmentId(), requestDto.getStudentId(), file);
        minioService.upload(file, fileKey);

        var submission = submissionMapper.toEntity(requestDto);
        submission.setFileKey(fileKey);

        return submissionMapper.toDto(submissionRepository.save(submission));
    }


    /**
     * Downloads the file associated with a submission.
     *
     * @param id submission identifier
     * @return input stream of the stored file
     */
    @Override
    @BusinessEvent(LogEvent.FILE_DOWNLOAD)
    public InputStream downloadSubmissionFile(Long id) {
        return minioService.download(findById(id).getFileKey());
    }


    /**
     * Deletes a submission by its identifier.
     *
     * @param id submission identifier
     * @return deleted submission representation
     *
     * @throws SubmissionNotFoundException if submission does not exist
     */
    @Override
    @BusinessEvent(LogEvent.SUBMISSION_DELETION)
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = "submission:id", key = "#id"),
        @CacheEvict(cacheNames = "submission:assignment", key = "#result.assignmentId")
    })
    public FileSubmissionResponseDto deleteById(Long id) {
        var submission = findById(id);
        var fileKey = submission.getFileKey();
        submissionRepository.deleteById(id);
        minioService.remove(fileKey);

        return submissionMapper.toDto(submission);
    }


    private FileSubmission findById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new SubmissionNotFoundException("Submission not found with ID: " + id));
    }
}
