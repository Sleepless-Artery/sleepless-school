package org.sleepless_artery.assignment_service.service.core;

import org.sleepless_artery.assignment_service.dto.response.AssignmentResponseDto;


/**
 * Service for assignment management operations.
 */
public interface AssignmentService {

    AssignmentResponseDto deleteAssignmentById(Long id);
}
