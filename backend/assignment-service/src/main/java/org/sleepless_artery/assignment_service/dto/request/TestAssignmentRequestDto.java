package org.sleepless_artery.assignment_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;


/**
 * Request DTO for test-based assignments.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestAssignmentRequestDto extends AssignmentRequestDto {

    @NotBlank(message = "Condition cannot be blank")
    private String condition;

    @NotNull(message = "Options list must be defined")
    @Size(min = 2, message = "Options list must contain at least 2 elements")
    private List<String> options;

    @NotNull(message = "Correct options indices list must be defined")
    private List<Integer> correctOptionsIndices;
}