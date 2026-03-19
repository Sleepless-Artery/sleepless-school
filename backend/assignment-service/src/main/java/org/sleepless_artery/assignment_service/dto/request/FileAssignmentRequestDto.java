package org.sleepless_artery.assignment_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Request DTO for file-based assignments.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileAssignmentRequestDto extends AssignmentRequestDto {

    @NotBlank(message = "Display filename cannot be blank")
    @Size(max = 100, message = "Display filename cannot exceed 100 characters")
    private String displayFilename;
}