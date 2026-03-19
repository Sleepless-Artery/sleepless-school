package org.sleepless_artery.course_service.dto;

import java.time.LocalDate;


/**
 * DTO for displaying course data.
 */
public record CourseResponseDto(
    Long id,
    String title,
    Long authorId,
    LocalDate creationDate,
    LocalDate lastUpdateDate,
    String description
) {}
