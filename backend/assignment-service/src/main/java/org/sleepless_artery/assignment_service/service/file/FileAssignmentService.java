package org.sleepless_artery.assignment_service.service.file;

import org.sleepless_artery.assignment_service.dto.request.FileAssignmentRequestDto;
import org.sleepless_artery.assignment_service.dto.response.FileAssignmentResponseDto;
import org.sleepless_artery.assignment_service.service.core.AssignmentService;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


/**
 * Service for file-based assignment management.
 * <p>
 * Extends basic assignment operations with file upload, download, and storage capabilities.
 * </p>
 */
public interface FileAssignmentService extends AssignmentService {

    FileAssignmentResponseDto findAssignmentById(Long id);

    InputStream downloadAssignmentFile(Long id);

    FileAssignmentResponseDto createAssignment(
            FileAssignmentRequestDto assignmentRequestDto, MultipartFile file
    ) throws Exception;

    FileAssignmentResponseDto updateAssignment(
            Long id, FileAssignmentRequestDto assignmentRequestDto
    ) throws Exception;

    FileAssignmentResponseDto updateAssignmentFile(
            Long id, String filename, MultipartFile file
    ) throws Exception;
}
