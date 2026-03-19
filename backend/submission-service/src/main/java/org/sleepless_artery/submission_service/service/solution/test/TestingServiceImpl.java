package org.sleepless_artery.submission_service.service.solution.test;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.submission_service.config.cache.CacheConfig;
import org.sleepless_artery.submission_service.dto.request.TestSubmissionRequestDto;
import org.sleepless_artery.submission_service.dto.response.TestSubmissionResponseDto;
import org.sleepless_artery.submission_service.exception.SubmissionNotFoundException;
import org.sleepless_artery.submission_service.logging.annotations.BusinessEvent;
import org.sleepless_artery.submission_service.logging.event.LogEvent;
import org.sleepless_artery.submission_service.mapper.TestSubmissionMapper;
import org.sleepless_artery.submission_service.repository.test.TestSubmissionRepository;
import org.sleepless_artery.submission_service.service.grading.TestGradingService;
import org.sleepless_artery.submission_service.service.validation.existence.CommonExistenceValidator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Default implementation of {@link TestingService}.
 */
@Service
@RequiredArgsConstructor
public class TestingServiceImpl implements TestingService {

    private final TestSubmissionRepository submissionRepository;
    private final TestGradingService gradingService;
    private final TestSubmissionMapper submissionMapper;
    private final CommonExistenceValidator existenceValidator;


    /**
     * Completes a test assignment and calculates the final score.
     *
     * @param requestDto test submission request
     * @return evaluated test submission DTO
     */
    @Override
    @BusinessEvent(LogEvent.SUBMISSION_CREATION)
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.ASSIGNMENT_CACHE_NAME, key = "#requestDto.assignmentId")
    public TestSubmissionResponseDto completeTest(TestSubmissionRequestDto requestDto) {
        existenceValidator.validateExistence(requestDto.getAssignmentId(), requestDto.getStudentId());
        var submission = submissionMapper.toEntity(requestDto);
        submission.setScore(gradingService.evaluate(requestDto));
        return submissionMapper.toDto(submissionRepository.save(submission));
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
            @CacheEvict(cacheNames = CacheConfig.SUBMISSION_CACHE_NAME, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.ASSIGNMENT_CACHE_NAME, key = "#result.assignmentId")
    })
    public TestSubmissionResponseDto deleteById(Long id) {
        var submission = submissionRepository.findById(id).orElseThrow(
                () -> new SubmissionNotFoundException("Submission not found with ID: " + id)
        );
        submissionRepository.deleteById(id);
        return submissionMapper.toDto(submission);
    }
}
