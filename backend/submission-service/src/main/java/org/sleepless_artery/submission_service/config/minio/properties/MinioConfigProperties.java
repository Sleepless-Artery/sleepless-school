package org.sleepless_artery.submission_service.config.minio.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration properties for MinIO storage.
 *
 * <p>Binds configuration values from {@code minio.*} properties
 * used for connecting to the MinIO object storage service.</p>
 */
@Configuration
@ConfigurationProperties(prefix = "minio")
@Getter @Setter
public class MinioConfigProperties {

    private String url;

    private String accessKey;

    private String secretKey;

    private String bucketName;
}
