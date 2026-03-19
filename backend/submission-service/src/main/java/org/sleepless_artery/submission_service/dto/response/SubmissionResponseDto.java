package org.sleepless_artery.submission_service.dto.response;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * Base response DTO representing a submission.
 */
@Getter @Setter
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "submissionType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FileSubmissionResponseDto.class, name = "FILE"),
        @JsonSubTypes.Type(value = TestSubmissionResponseDto.class, name = "TEST")
})
public abstract class SubmissionResponseDto {

    private Long id;
    private Long assignmentId;
    private Long studentId;
    private LocalDateTime submittedAt;
    private Double score;

    @JsonProperty("submissionType")
    public String getSubmissionType() {
        var typeName = this.getClass().getAnnotation(JsonTypeName.class);
        return typeName != null ? typeName.value() : this.getClass().getSimpleName();
    }
}
