package org.sleepless_artery.assignment_service.service.validation;

import org.springframework.web.multipart.MultipartFile;


/**
 * Service for file validation.
 */
public interface FileValidator {
    void validate(MultipartFile file);
}
