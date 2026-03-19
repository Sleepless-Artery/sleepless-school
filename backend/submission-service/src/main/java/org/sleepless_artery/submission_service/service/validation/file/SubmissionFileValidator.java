package org.sleepless_artery.submission_service.service.validation.file;

import org.sleepless_artery.submission_service.exception.UnsupportedFileTypeException;
import org.sleepless_artery.submission_service.logging.annotations.BusinessEvent;
import org.sleepless_artery.submission_service.logging.event.LogEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;


/**
 * Service for file validation.
 */
@Service
public class SubmissionFileValidator implements FileValidator {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "doc", "docx");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;


    /**
     * Validates file against all security constraints.
     *
     * @param file the file to validate
     */
    @Override
    @BusinessEvent(LogEvent.FILE_VALIDATION)
    public void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new UnsupportedFileTypeException("File is empty or null");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new UnsupportedFileTypeException("File size exceeds maximum allowed size");
        }

        validateContentType(file);
        validateFilename(file);
    }


    /**
     * Validates file content type against allowed types.
     *
     * @param file the file to validate
     * @throws UnsupportedFileTypeException if content type is invalid
     */
    private void validateContentType(MultipartFile file) {
        var contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new UnsupportedFileTypeException("Invalid content type: " + contentType);
        }
    }


    /**
     * Validates file extension against allowed extensions.
     *
     * @param file the file to validate
     * @throws UnsupportedFileTypeException if extension is invalid
     */
    private void validateFilename(MultipartFile file) {
        var originalFilename = file.getOriginalFilename();

        if (originalFilename == null) {
            throw new UnsupportedFileTypeException("File has no name");
        }

        var extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new UnsupportedFileTypeException("Invalid file extension: " + extension);
        }
    }


    /**
     * Extracts file extension from filename.
     *
     * @param filename the filename
     * @return file extension or empty string if none
     */
    private String getFileExtension(String filename) {
        var lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex + 1);
    }
}
