package org.sleepless_artery.assignment_service.config.minio.init;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sleepless_artery.assignment_service.config.minio.properties.MinioConfigProperties;
import org.sleepless_artery.assignment_service.logging.annotation.Loggable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * Initializes the MinIO bucket on application startup.
 */
@Component
@RequiredArgsConstructor
public class MinioBucketInitializer implements ApplicationRunner {

    private final MinioClient minioClient;
    private final MinioConfigProperties configProperties;


    /**
     * Executes bucket initialization after application startup.
     *
     * @param args application arguments
     * @throws IllegalStateException if bucket initialization fails
     */
    @Loggable
    @Override
    public void run(@NotNull ApplicationArguments args) {
        var bucketName = configProperties.getBucketName();

        try {
            var exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize MinIO bucket: " + bucketName, e);
        }
    }
}
