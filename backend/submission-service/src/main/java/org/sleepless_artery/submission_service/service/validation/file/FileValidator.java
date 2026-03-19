package org.sleepless_artery.submission_service.service.validation.file;

import org.springframework.web.multipart.MultipartFile;


/**
 * Service for file validation.
 */
public interface FileValidator {
    void validate(MultipartFile file);
}
