package org.sleepless_artery.assignment_service.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Response DTO representing a file-based assignment.
 */
@Getter @Setter
@NoArgsConstructor
@JsonTypeName("FILE")
public class FileAssignmentResponseDto extends AssignmentResponseDto {

    private String displayFilename;

    private String fileKey;
}
