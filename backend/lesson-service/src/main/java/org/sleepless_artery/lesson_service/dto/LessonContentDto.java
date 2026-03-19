package org.sleepless_artery.lesson_service.dto;

/**
 * DTO for displaying the lesson content
 */
public record LessonContentDto(
        Long id,
        String title,
        String content
) {}
