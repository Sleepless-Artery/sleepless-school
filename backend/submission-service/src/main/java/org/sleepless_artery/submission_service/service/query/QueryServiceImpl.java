package org.sleepless_artery.submission_service.service.query;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.submission_service.config.cache.CacheConfig;
import org.sleepless_artery.submission_service.dto.response.SubmissionResponseDto;
import org.sleepless_artery.submission_service.exception.SubmissionNotFoundException;
import org.sleepless_artery.submission_service.mapper.SubmissionDtoMapper;
import org.sleepless_artery.submission_service.repository.core.SubmissionRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service for submission read operations.
 * <p>
 * Handles query operations with caching strategies for optimal read performance.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionDtoMapper submissionDtoMapper;


    /**
     * Retrieves submission by ID with caching.
     *
     * @param id the submission ID
     * @return submission data transfer object
     *
     * @throws SubmissionNotFoundException if submission does not exist
     */
    @Override
    @Cacheable(cacheNames = CacheConfig.SUBMISSION_CACHE_NAME, key = "#id")
    public SubmissionResponseDto findSubmissionById(Long id) {
        return submissionDtoMapper.toDto(
                submissionRepository.findById(id)
                        .orElseThrow(
                                () -> new SubmissionNotFoundException("Submission not found with ID: " + id)
                        )
        );
    }


    /**
     * Retrieves all submissions by assignment ID with caching.
     *
     * @param assignmentId the assignment ID
     * @return list of submission data transfer objects
     */
    @Override
    @Cacheable(cacheNames = CacheConfig.ASSIGNMENT_CACHE_NAME, key = "#assignmentId")
    public List<SubmissionResponseDto> findSubmissionsByAssignmentId(Long assignmentId) {
        return submissionRepository.findAllByAssignmentId(assignmentId).stream()
                .map(submissionDtoMapper::toDto)
                .toList();
    }


    /**
     * Retrieves submission by assignment ID and student ID with caching.
     *
     * @param assignmentId  the assignment ID
     * @param studentId     the student ID
     * @return submission data transfer object
     *
     * @throws SubmissionNotFoundException if submission does not exist
     */
    @Override
    public SubmissionResponseDto findSubmissionByAssignmentIdAndStudentId(Long assignmentId, Long studentId) {
        return submissionDtoMapper.toDto(
                submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseThrow(
                        () -> new SubmissionNotFoundException(
                                "Submission not found with ID: " + assignmentId + " and studentId: " + studentId
                        )
                )
        );
    }
}
