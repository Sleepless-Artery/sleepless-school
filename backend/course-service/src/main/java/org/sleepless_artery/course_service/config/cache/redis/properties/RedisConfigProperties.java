package org.sleepless_artery.course_service.config.cache.redis.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;


/**
 * Redis configuration properties.
 * <p>
 * Loaded from `spring.data.redis` prefix.
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Getter @Setter
@Validated
public class RedisConfigProperties {

    private String host;
    private int port;
    private String password;

    private Cluster cluster = new Cluster();


    @Getter @Setter
    public static class Cluster {
        private List<String> nodes;
        private Integer maxRedirects;
    }

    public boolean isClusterMode() {
        return cluster != null && cluster.nodes != null && !cluster.nodes.isEmpty();
    }
}
