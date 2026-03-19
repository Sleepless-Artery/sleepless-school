package org.sleepless_artery.submission_service.service.solution.file;

import org.sleepless_artery.submission_service.dto.request.FileSubmissionRequestDto;
import org.sleepless_artery.submission_service.dto.response.FileSubmissionResponseDto;
import org.sleepless_artery.submission_service.service.solution.core.SolutionService;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


/**
 * Service for managing file-based submissions.
 */
public interface FileSolutionService extends SolutionService {

    FileSubmissionResponseDto findSubmissionById(Long id);

    FileSubmissionResponseDto uploadSolution(FileSubmissionRequestDto requestDto, MultipartFile file);

    FileSubmissionResponseDto updateSolution(Long id, FileSubmissionRequestDto requestDto, MultipartFile file);

    InputStream downloadSubmissionFile(Long id);
}
