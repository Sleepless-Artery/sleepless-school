package org.sleepless_artery.lesson_service.dto;

/**
 * DTO for displaying information about a lesson
 */
public record LessonInfoDto(
        Long id,
        String title,
        Long courseId,
        String description
) {}
