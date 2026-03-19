package org.sleepless_artery.submission_service.service.query;

import org.sleepless_artery.submission_service.dto.response.SubmissionResponseDto;

import java.util.List;


/**
 * Service for submission read operations.
 * <p>
 * Provides query-only methods for retrieving submission data.
 * </p>
 */
public interface QueryService {

    SubmissionResponseDto findSubmissionById(Long id);

    List<SubmissionResponseDto> findSubmissionsByAssignmentId(Long assignmentId);

    SubmissionResponseDto findSubmissionByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
}
