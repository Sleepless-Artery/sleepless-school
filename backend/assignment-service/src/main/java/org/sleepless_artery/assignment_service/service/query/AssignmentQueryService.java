package org.sleepless_artery.assignment_service.service.query;

import org.sleepless_artery.assignment_service.dto.response.AssignmentResponseDto;

import java.util.List;


/**
 * Service for assignment read operations.
 * <p>
 * Provides query-only methods for retrieving assignment data.
 * </p>
 */
public interface AssignmentQueryService {

    AssignmentResponseDto findById(Long id);

    List<AssignmentResponseDto> findAllByLessonId(Long lessonId);

    boolean existsById(Long id);
}
