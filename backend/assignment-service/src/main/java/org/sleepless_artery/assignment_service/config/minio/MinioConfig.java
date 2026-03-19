package org.sleepless_artery.assignment_service.config.minio;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.assignment_service.config.minio.properties.MinioConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * MinIO client configuration.
 *
 * <p>Configures a {@link MinioClient} bean using
 * {@link MinioConfigProperties}.</p>
 */
@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioConfigProperties configProperties;

    /**
     * Creates a configured MinIO client instance.
     *
     * @return MinIO client
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(configProperties.getUrl())
                .credentials(configProperties.getAccessKey(), configProperties.getSecretKey())
                .build();
    }
}