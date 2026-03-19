package org.sleepless_artery.assignment_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * Base response DTO for assignment representation.
 *
 * <p>Supports polymorphic JSON serialization depending on
 * assignment type.</p>
 */
@Getter @Setter
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "assignmentType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FileAssignmentResponseDto.class, name = "FILE"),
        @JsonSubTypes.Type(value = TestAssignmentResponseDto.class, name = "TEST")
})
public abstract class AssignmentResponseDto {

    private Long id;
    private String title;
    private Long lessonId;
    private String description;
    private Double maxScore;
    private LocalDateTime deadline;

    @JsonProperty("assignmentType")
    public String getAssignmentType() {
        var typeName = this.getClass().getAnnotation(JsonTypeName.class);
        return typeName != null ? typeName.value() : this.getClass().getSimpleName();
    }
}
