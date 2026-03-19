package org.sleepless_artery.submission_service.service.grading;

import org.sleepless_artery.submission_service.dto.request.TestSubmissionRequestDto;


/**
 * Service responsible for automatic evaluation of test submissions.
 */
public interface TestGradingService {
    double evaluate(TestSubmissionRequestDto requestDto);
}
