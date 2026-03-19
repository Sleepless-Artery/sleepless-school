package org.sleepless_artery.submission_service.service.review;

import org.sleepless_artery.submission_service.dto.request.SolutionReviewDto;
import org.sleepless_artery.submission_service.dto.response.SubmissionResponseDto;


/**
 * Service responsible for reviewing student submissions.
 * <p>
 * Provides operations for assigning scores and feedback to submissions,
 * as well as removing existing reviews.
 */
public interface ReviewService {

    SubmissionResponseDto reviewSubmissionById(Long id, SolutionReviewDto reviewDto);

    SubmissionResponseDto removeReviewFromSubmissionById(Long id);
}
