package org.sleepless_artery.assignment_service.service.util;

import org.sleepless_artery.assignment_service.dto.request.AssignmentRequestDto;
import org.sleepless_artery.assignment_service.model.base.Assignment;
import org.springframework.stereotype.Component;


/**
 * Component for updating common assignment fields.
 * <p>
 * Provides reusable update logic shared across different assignment types.
 * </p>
 */
@Component
public class CommonAssignmentUpdater {

    /**
     * Updates common fields on an assignment entity.
     *
     * @param assignment the assignment entity to update
     * @param dto the data transfer object with new values
     */
    public void updateAssignment(Assignment assignment, AssignmentRequestDto dto) {
        if (dto.getTitle() != null) {
            assignment.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            assignment.setDescription(dto.getDescription());
        }

        if (dto.getMaxScore() != null) {
            assignment.setMaxScore(dto.getMaxScore());
        }

        if (dto.getDeadline() != null) {
            assignment.setDeadline(dto.getDeadline());
        }
    }
}