package org.sleepless_artery.submission_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * Request DTO for submitting a solution to a test assignment.
 * <p>
 * Extends {@link SubmissionRequestDto} and
 * contains data about student's answer.
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestSubmissionRequestDto extends SubmissionRequestDto {

    @NotNull(message = "Selected options indices list must be defined")
    private List<Integer> selectedOptionsIndices;
}
