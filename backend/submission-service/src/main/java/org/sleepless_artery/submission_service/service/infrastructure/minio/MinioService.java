package org.sleepless_artery.submission_service.service.infrastructure.minio;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Collection;


/**
 * Service for MinIO object storage operations.
 * <p>
 * Provides abstraction for file upload, download, and deletion.
 * </p>
 */
public interface MinioService {

    void upload(MultipartFile file, String fileKey);

    void remove(String fileKey);

    void removeAll(Collection<String> fileKeys);

    InputStream download(String fileKey);
}
