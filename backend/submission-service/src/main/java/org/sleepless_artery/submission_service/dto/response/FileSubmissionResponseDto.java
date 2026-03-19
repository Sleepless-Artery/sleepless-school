package org.sleepless_artery.submission_service.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sleepless_artery.submission_service.model.base.SubmissionStatus;


/**
 * Response DTO representing a file-based submission.
 * <p>
 * Contains metadata about the uploaded file and the current review status.
 * </p>
 */
@Getter @Setter
@NoArgsConstructor
@JsonTypeName("FILE")
public class FileSubmissionResponseDto extends SubmissionResponseDto {

    private String reviewComment;

    private SubmissionStatus status;

    private String displayFilename;

    private String fileKey;
}
