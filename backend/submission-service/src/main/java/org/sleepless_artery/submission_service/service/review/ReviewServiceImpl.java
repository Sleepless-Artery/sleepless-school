package org.sleepless_artery.submission_service.service.review;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.submission_service.config.cache.CacheConfig;
import org.sleepless_artery.submission_service.dto.request.SolutionReviewDto;
import org.sleepless_artery.submission_service.dto.response.SubmissionResponseDto;
import org.sleepless_artery.submission_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.submission_service.exception.SubmissionNotFoundException;
import org.sleepless_artery.submission_service.logging.annotations.BusinessEvent;
import org.sleepless_artery.submission_service.logging.event.LogEvent;
import org.sleepless_artery.submission_service.mapper.FileSubmissionMapper;
import org.sleepless_artery.submission_service.model.base.SubmissionStatus;
import org.sleepless_artery.submission_service.model.file.FileSubmission;
import org.sleepless_artery.submission_service.repository.file.FileSubmissionRepository;
import org.sleepless_artery.submission_service.service.external.assignment.FetchAssignmentDataService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Default implementation of {@link ReviewService}.
 * <p>
 * Handles manual evaluation of file-based submissions by instructors.
 * Allows assigning scores, adding review comments, and removing reviews.
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final FileSubmissionRepository submissionRepository;
    private final FileSubmissionMapper submissionMapper;
    private final FetchAssignmentDataService fetchAssignmentDataService;


    /**
     * Assigns a score and optional review comment to a submission.
     *
     * @param id identifier of the submission
     * @param reviewDto review data containing score and comment
     * @return updated submission DTO
     *
     * @throws SubmissionNotFoundException if submission does not exist
     * @throws IllegalArgumentException if score exceeds assignment maximum score
     * @throws ExternalServiceUnavailableException if assignment service is unavailable
     */
    @Override
    @BusinessEvent(LogEvent.REVIEW_SUBMISSION_UPDATE)
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.SUBMISSION_CACHE_NAME, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.ASSIGNMENT_CACHE_NAME, key = "#result.assignmentId")
    })
    public SubmissionResponseDto reviewSubmissionById(Long id, SolutionReviewDto reviewDto) {
        var submission = findById(id);

        var maxScore = fetchAssignmentDataService.getMaxScore(submission.getAssignmentId());

        if (maxScore < 0) {
            throw new ExternalServiceUnavailableException("Service unavailable");
        }

        if (reviewDto.getScore() > maxScore) {
            throw new IllegalArgumentException("Score is greater than the maximum score");
        }

        submission.setScore(reviewDto.getScore());
        submission.setReviewComment(reviewDto.getReviewComment());
        submission.setStatus(submission.getScore() > 0 ? SubmissionStatus.RATED : SubmissionStatus.REJECTED);

        return submissionMapper.toDto(submissionRepository.save(submission));
    }


    /**
     * Removes the review from a submission and resets its state.
     *
     * @param id identifier of the submission
     * @return updated submission DTO
     *
     * @throws SubmissionNotFoundException if submission does not exist
     */
    @Override
    @BusinessEvent(LogEvent.REVIEW_SUBMISSION_DELETION)
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.SUBMISSION_CACHE_NAME, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.ASSIGNMENT_CACHE_NAME, key = "#result.assignmentId")
    })
    public SubmissionResponseDto removeReviewFromSubmissionById(Long id) {
        var submission = findById(id);
        submission.setScore(null);
        submission.setReviewComment(null);
        submission.setStatus(SubmissionStatus.SUBMITTED);

        return submissionMapper.toDto(submissionRepository.save(submission));
    }


    private FileSubmission findById(Long id) {
        return submissionRepository.findById(id).orElseThrow(
                () -> new SubmissionNotFoundException("Submission not found with ID: " + id)
        );
    }
}
