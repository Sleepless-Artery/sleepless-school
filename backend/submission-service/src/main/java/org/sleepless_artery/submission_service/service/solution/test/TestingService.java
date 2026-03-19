package org.sleepless_artery.submission_service.service.solution.test;

import org.sleepless_artery.submission_service.dto.request.TestSubmissionRequestDto;
import org.sleepless_artery.submission_service.dto.response.TestSubmissionResponseDto;
import org.sleepless_artery.submission_service.service.solution.core.SolutionService;


/**
 * Service responsible for handling test assignment submissions.
 */
public interface TestingService extends SolutionService {

    TestSubmissionResponseDto completeTest(TestSubmissionRequestDto requestDto);
}
