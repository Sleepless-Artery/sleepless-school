package org.sleepless_artery.assignment_service.service.file;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.assignment_service.config.cache.CacheConfig;
import org.sleepless_artery.assignment_service.dto.request.FileAssignmentRequestDto;
import org.sleepless_artery.assignment_service.dto.response.AssignmentResponseDto;
import org.sleepless_artery.assignment_service.dto.response.FileAssignmentResponseDto;
import org.sleepless_artery.assignment_service.exception.*;
import org.sleepless_artery.assignment_service.logging.event.LogEvent;
import org.sleepless_artery.assignment_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.assignment_service.mapper.FileAssignmentMapper;
import org.sleepless_artery.assignment_service.model.file.FileAssignment;
import org.sleepless_artery.assignment_service.repository.file.FileAssignmentRepository;
import org.sleepless_artery.assignment_service.service.external.lesson.LessonExistenceChecker;
import org.sleepless_artery.assignment_service.service.infrastructure.kafka.producer.KafkaProducer;
import org.sleepless_artery.assignment_service.service.util.CommonAssignmentUpdater;
import org.sleepless_artery.assignment_service.service.infrastructure.minio.MinioService;
import org.sleepless_artery.assignment_service.service.util.FileKeyGenerator;
import org.sleepless_artery.assignment_service.service.validation.FileValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


/**
 * Service implementation for file-based assignment management.
 * <p>
 * Handles CRUD operations for file assignments, MinIO storage integration,
 * caching strategies, and Kafka event notifications.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class FileAssignmentServiceImpl implements FileAssignmentService{

    private final FileAssignmentRepository fileAssignmentRepository;
    private final CommonAssignmentUpdater assignmentUpdater;
    private final LessonExistenceChecker lessonExistenceChecker;
    private final FileValidator fileValidator;
    private final FileKeyGenerator fileKeyGenerator;
    private final MinioService minioService;
    private final FileAssignmentMapper assignmentMapper;
    private final KafkaProducer kafkaProducer;

    @Value("${spring.kafka.topic.prefix}")
    private String prefix;

    @Value("${spring.kafka.topic.domain}")
    private String domain;


    /**
     * Downloads assignment file from MinIO storage.
     *
     * @param id the assignment ID
     * @return input stream of the file content
     */
    @Override
    @BusinessEvent(LogEvent.FILE_DOWNLOAD)
    public InputStream downloadAssignmentFile(Long id) {
        return minioService.download(findById(id).getFileKey());
    }


    /**
     * Retrieves file assignment by ID.
     *
     * @param id the assignment ID
     * @return file assignment data transfer object
     */
    @Override
    public FileAssignmentResponseDto findAssignmentById(Long id) {
        return assignmentMapper.toDto(findById(id));
    }


    /**
     * Creates a new file assignment with file upload.
     *
     * @param requestDto the assignment creation data
     * @param file the file to upload
     * @return created file assignment data transfer object
     */
    @Override
    @BusinessEvent(LogEvent.ASSIGNMENT_CREATION)
    @CacheEvict(cacheNames = CacheConfig.LESSON_CACHE_NAME, key = "#result.lessonId")
    @Transactional
    public FileAssignmentResponseDto createAssignment(
            FileAssignmentRequestDto requestDto, MultipartFile file
    ) {
        var lessonId = requestDto.getLessonId();

        switch (lessonExistenceChecker.verifyLessonExistence(lessonId)) {
            case NOT_FOUND -> throw new InvalidLessonIdException("Lesson not found with ID: " + lessonId);
            case SERVICE_UNAVAILABLE -> throw new ExternalServiceUnavailableException("Service unavailable");
        }

        if (fileAssignmentRepository.findByLessonIdAndTitleIgnoreCase(lessonId, requestDto.getTitle()) != null) {
            throw new AssignmentAlreadyExistsException(
                    "File assignment already exists with title: " + requestDto.getTitle() +
                            " for lesson: " + lessonId
            );
        }

        fileValidator.validate(file);

        var fileKey = fileKeyGenerator.generate(requestDto.getLessonId(), file);
        FileAssignment saved;

        try {
            minioService.upload(file, fileKey);

            var assignment = assignmentMapper.toEntity(requestDto);
            assignment.setFileKey(fileKey);

            if (requestDto.getDisplayFilename() != null && !requestDto.getDisplayFilename().isBlank()) {
                assignment.setDisplayFilename(requestDto.getDisplayFilename());
            } else {
                assignment.setDisplayFilename(file.getOriginalFilename());
            }

            saved = fileAssignmentRepository.save(assignment);
        } catch (Exception e) {
            minioService.remove(fileKey);
            throw new FileOperationException(e.getMessage());
        }

        return assignmentMapper.toDto(saved);
    }


    /**
     * Updates assignment metadata.
     *
     * @param id the assignment ID
     * @param requestDto the update data
     * @return updated file assignment data transfer object
     */
    @Override
    @BusinessEvent(LogEvent.ASSIGNMENT_UPDATE)
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.ASSIGNMENT_CACHE_NAME, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.LESSON_CACHE_NAME, key = "#result.lessonId")
    })
    @Transactional
    public FileAssignmentResponseDto updateAssignment(
            Long id, FileAssignmentRequestDto requestDto
    ) {
        var assignment = findById(id);

        var existingAssignment = fileAssignmentRepository.findByLessonIdAndTitleIgnoreCase(
                requestDto.getLessonId(), requestDto.getTitle()
        );
        if (existingAssignment != null && !existingAssignment.getId().equals(assignment.getId())) {
            throw new AssignmentAlreadyExistsException(
                    "File assignment already exists with title: " + requestDto.getTitle() +
                            " for lesson: " + requestDto.getLessonId()
            );
        }

        assignmentUpdater.updateAssignment(assignment, requestDto);

        if (requestDto.getDisplayFilename() != null && !requestDto.getDisplayFilename().isBlank()) {
            assignment.setDisplayFilename(requestDto.getDisplayFilename());
        }

        return assignmentMapper.toDto(fileAssignmentRepository.save(assignment));
    }


    /**
     * Replaces assignment file with new file.
     *
     * @param id the assignment ID
     * @param filename the display filename
     * @param file the new file to upload
     * @return updated file assignment data transfer object
     */
    @Override
    @BusinessEvent(LogEvent.ASSIGNMENT_FILE_UPDATE)
    @Transactional
    public FileAssignmentResponseDto updateAssignmentFile(
            Long id, String filename, MultipartFile file
    ) {
        var assignment = findById(id);
        fileValidator.validate(file);

        minioService.remove(assignment.getFileKey());

        var newFileKey = fileKeyGenerator.generate(assignment.getLessonId(), file);
        minioService.upload(file, newFileKey);

        assignment.setFileKey(newFileKey);

        if (filename != null && !filename.isBlank()) {
            assignment.setDisplayFilename(filename);
        }

        return assignmentMapper.toDto(fileAssignmentRepository.save(assignment));
    }


    /**
     * Deletes assignment by ID with file cleanup.
     *
     * @param id the assignment ID
     * @return deleted assignment data transfer object
     */
    @Override
    @BusinessEvent(LogEvent.ASSIGNMENT_DELETION)
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.ASSIGNMENT_CACHE_NAME, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.LESSON_CACHE_NAME, key = "#result.lessonId")
    })
    @Transactional
    public AssignmentResponseDto deleteAssignmentById(Long id) {
        var assignment = findById(id);
        var fileKey = assignment.getFileKey();

        fileAssignmentRepository.deleteById(id);
        minioService.remove(fileKey);

        kafkaProducer.send(
                String.format("%s.%s.%s", prefix, domain, "deleted"),
                id.toString()
        );

        return assignmentMapper.toDto(assignment);
    }


    private FileAssignment findById(Long id) {
        return fileAssignmentRepository.findById(id)
                .orElseThrow(() -> new AssignmentNotFoundException("File assignment not found with ID: " + id));
    }
}
