package org.sleepless_artery.course_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * DTO for creating or updating a course.
 */
@Getter
@AllArgsConstructor
public class CourseRequestDto {

    @NotBlank(message = "Course title cannot be blank")
    private String title;

    @NotNull(message = "Author ID must be defined")
    private final Long authorId;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
}
