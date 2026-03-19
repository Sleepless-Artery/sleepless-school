package org.sleepless_artery.assignment_service.service.test;

import org.sleepless_artery.assignment_service.dto.request.TestAssignmentRequestDto;
import org.sleepless_artery.assignment_service.dto.response.TestAssignmentResponseDto;
import org.sleepless_artery.assignment_service.service.core.AssignmentService;


/**
 * Service for test-based assignment management.
 * <p>
 * Extends basic assignment operations with test-specific creation and update capabilities.
 * </p>
 */
public interface TestAssignmentService extends AssignmentService {

    TestAssignmentResponseDto createAssignment(TestAssignmentRequestDto assignmentRequestDto);

    TestAssignmentResponseDto updateAssignment(Long id, TestAssignmentRequestDto assignmentRequestDto);
}
