package org.sleepless_artery.submission_service.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO representing instructor review information for a submission.
 * <p>
 * Used to evaluate a student's solution.
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolutionReviewDto {

    @PositiveOrZero(message = "Score cannot be negative")
    private Double score;

    @Size(max = 1000, message = "Review comment length cannot exceed 1000 characters")
    private String reviewComment;
}
