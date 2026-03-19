package org.sleepless_artery.submission_service.service.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


/**
 * Component for generating unique file keys for storage.
 * <p>
 * Creates structured file paths with UUID-based filenames and safe extensions.
 * </p>
 */
@Component
public class FileKeyGenerator {

    /**
     * Generates a unique file key for storage.
     * <p>
     * Format: {@code submissions/assignment-{assignmentId}/student-{studentId}/{uuid}.{extension}}
     * </p>
     *
     * @param assignmentId the assignment ID for path organization
     * @param studentId the student ID for path organization
     * @param file the uploaded file
     * @return generated file key path
     */
    public String generate(Long assignmentId, Long studentId, MultipartFile file) {
        var originalFilename = file.getOriginalFilename();
        var extension = getSafeExtension(originalFilename);

        return String.format("submissions/assignment-%d/student-%d/%s.%s",
                assignmentId, studentId, UUID.randomUUID(), extension);
    }

    /**
     * Extracts and sanitizes file extension.
     *
     * @param filename the original filename
     * @return sanitized file extension
     */
    private String getSafeExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "bin";
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        return extension.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
}

