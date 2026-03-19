package org.sleepless_artery.submission_service.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * Response DTO representing a test assignment submission.
 */
@Getter @Setter
@NoArgsConstructor
@JsonTypeName("TEST")
public class TestSubmissionResponseDto extends SubmissionResponseDto {
    private List<Integer> selectedOptionsIndices;
}


