package org.sleepless_artery.submission_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for submitting a file-based assignment solution.
 * <p>
 * Extends {@link SubmissionRequestDto} and adds metadata related to the uploaded file.
 * The actual file content is typically transferred separately as a multipart request.
 * </p>
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileSubmissionRequestDto extends SubmissionRequestDto {

    @NotBlank(message = "Display filename cannot be blank")
    @Size(max = 100, message = "Display filename cannot exceed 100 characters")
    private String displayFilename;
}
