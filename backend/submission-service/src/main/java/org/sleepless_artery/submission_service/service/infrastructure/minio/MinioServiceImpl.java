package org.sleepless_artery.submission_service.service.infrastructure.minio;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sleepless_artery.submission_service.exception.FileOperationException;
import org.sleepless_artery.submission_service.logging.annotations.BusinessEvent;
import org.sleepless_artery.submission_service.logging.event.LogEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Collection;


/**
 * Service for MinIO object storage operations.
 * <p>
 * Handles file operations with circuit breaker and retry patterns for resilience.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucketName:submission-files}")
    private String bucketName;

    private static final String MINIO_SERVICE = "minio-service";


    /**
     * Uploads file to MinIO.
     *
     * @param file      the file to upload
     * @param fileKey   the storage key/path
     *
     * @throws FileOperationException if upload fails
     */
    @Override
    @BusinessEvent(LogEvent.FILE_UPLOAD)
    @CircuitBreaker(name = MINIO_SERVICE, fallbackMethod = "fallbackUpload")
    @Retry(name = MINIO_SERVICE)
    public void upload(MultipartFile file, String fileKey) {
        if (fileKey == null || fileKey.isBlank()) {
            return;
        }

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new FileOperationException("Failed to upload file: " + fileKey);
        }
    }


    /**
     * Removes file from MinIO.
     *
     * @param fileKey the storage key/path
     *
     * @throws FileOperationException if deletion fails
     */
    @Override
    @BusinessEvent(LogEvent.FILE_REMOVE)
    @CircuitBreaker(name = MINIO_SERVICE, fallbackMethod = "fallbackRemove")
    @Retry(name = MINIO_SERVICE)
    public void remove(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) {
            return;
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .build()
            );
        } catch (Exception e) {
            throw new FileOperationException("Failed to remove file: " + fileKey);
        }
    }


    /**
     * Removes multiple files in parallel.
     *
     * @param fileKeys collection of storage keys/paths
     */
    @Override
    public void removeAll(Collection<String> fileKeys) {
        fileKeys.parallelStream().filter(k -> k != null && !k.isBlank()).forEach(this::remove);
    }


    /**
     * Downloads file from MinIO.
     *
     * @param fileKey the storage key/path
     * @return input stream of file content
     *
     * @throws FileOperationException if download fails
     */
    @Override
    @BusinessEvent(LogEvent.FILE_DOWNLOAD)
    @CircuitBreaker(name = MINIO_SERVICE, fallbackMethod = "fallbackDownload")
    @Retry(name = MINIO_SERVICE)
    public InputStream download(String fileKey) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .build()
            );
        } catch (Exception e) {
            throw new FileOperationException("Failed to fetch file: " + fileKey);
        }
    }


    private void fallbackUpload(MultipartFile file, String fileKey, Throwable throwable) {}

    private void fallbackRemove(String fileKey, Throwable throwable) {}

    private InputStream fallbackDownload(String fileKey, Throwable throwable) {
        return null;
    }
}
