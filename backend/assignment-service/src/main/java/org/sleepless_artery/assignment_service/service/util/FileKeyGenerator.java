package org.sleepless_artery.assignment_service.service.util;

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
     * Format: {@code assignments/lesson-{lessonId}/{uuid}.{extension}}
     * </p>
     *
     * @param lessonId the lesson ID for path organization
     * @param file the uploaded file
     * @return generated file key path
     */
    public String generate(Long lessonId, MultipartFile file) {
        var originalFilename = file.getOriginalFilename();
        var extension = getSafeExtension(originalFilename);

        return String.format("assignments/lesson-%d/%s.%s",
                lessonId, UUID.randomUUID(), extension);
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
        extension = extension.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        if (extension.length() > 10 || extension.isEmpty()) {
            return "bin";
        }

        return extension;
    }
}
