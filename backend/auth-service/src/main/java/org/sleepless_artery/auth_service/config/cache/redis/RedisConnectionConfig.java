package org.sleepless_artery.auth_service.config.cache.redis;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.config.cache.redis.properties.RedisConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;


/**
 * Redis connection configuration.
 *
 * <p>Creates {@link RedisConnectionFactory} depending on the configured Redis mode:
 * standalone or cluster.</p>
 *
 * <p>Uses Lettuce client with custom timeout settings.</p>
 */
@Configuration
@RequiredArgsConstructor
public class RedisConnectionConfig {

    private final RedisConfigProperties redisProperties;


    /**
     * Creates Redis connection factory based on application configuration.
     *
     * @return configured {@link RedisConnectionFactory}
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        LettuceClientConfiguration clientConfig =
                LettuceClientConfiguration.builder()
                        .commandTimeout(Duration.ofSeconds(2))
                        .shutdownTimeout(Duration.ofMillis(100))
                        .build();

        if (redisProperties.isClusterMode()) {
            var clusterCfg = new RedisClusterConfiguration();
            redisProperties.getCluster().getNodes()
                    .forEach(node -> {
                        var parts = node.split(":");
                        clusterCfg.clusterNode(
                                parts[0], Integer.parseInt(parts[1])
                        );
                    });

            clusterCfg.setMaxRedirects(redisProperties.getCluster().getMaxRedirects());

            if (redisProperties.getPassword() != null) {
                clusterCfg.setPassword(RedisPassword.of(redisProperties.getPassword()));
            }

            return new LettuceConnectionFactory(clusterCfg, clientConfig);
        }

        RedisStandaloneConfiguration standaloneCfg = new RedisStandaloneConfiguration(
                redisProperties.getHost(), redisProperties.getPort()
        );

        if (redisProperties.getPassword() != null) {
            standaloneCfg.setPassword(RedisPassword.of(redisProperties.getPassword()));
        }

        return new LettuceConnectionFactory(standaloneCfg, clientConfig);
    }
}