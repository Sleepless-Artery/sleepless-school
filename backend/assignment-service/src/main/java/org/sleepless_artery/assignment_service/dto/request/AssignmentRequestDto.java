package org.sleepless_artery.assignment_service.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * Base request DTO for assignment creation or update operations.
 *
 * <p>Contains common fields shared by all assignment types.</p>
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AssignmentRequestDto {

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title length cannot exceed 100 characters")
    private String title;

    @NotNull(message = "Lesson ID must be defined")
    private Long lessonId;

    @Size(max = 500, message = "Description length cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Maximum score must be defined")
    @PositiveOrZero(message = "Maximum score must be non-negative")
    private Double maxScore;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;
}