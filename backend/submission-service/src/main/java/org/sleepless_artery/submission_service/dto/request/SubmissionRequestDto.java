package org.sleepless_artery.submission_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Base request DTO for creating a submission.
 * <p>
 * Contains common fields required for all submission types.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class SubmissionRequestDto {

    @NotNull(message = "Assignment ID must be defined")
    private Long assignmentId;

    @NotNull(message = "Student ID must be defined")
    private Long studentId;
}
