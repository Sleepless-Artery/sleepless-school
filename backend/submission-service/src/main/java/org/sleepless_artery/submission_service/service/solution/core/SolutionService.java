package org.sleepless_artery.submission_service.service.solution.core;

import org.sleepless_artery.submission_service.dto.response.SubmissionResponseDto;


/**
 * Base service for submission management.
 * <p>
 * Defines common operations applicable to all submission types.
 */
public interface SolutionService {
    SubmissionResponseDto deleteById(Long id);
}
