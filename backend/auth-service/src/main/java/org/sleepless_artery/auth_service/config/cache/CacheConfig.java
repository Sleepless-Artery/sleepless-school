package org.sleepless_artery.auth_service.config.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sleepless_artery.auth_service.credential.model.Credential;
import org.sleepless_artery.auth_service.role.model.Role;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


/**
 * Redis cache configuration.
 * <p>
 * Configures caches for authorization-related entities
 * with TTL and serialization.
 */
@EnableCaching
@Configuration
public class CacheConfig {

    public static final String ROLE_CACHE_NAME = "role";
    public static final String CREDENTIAL_CACHE_NAME = "credential";


    /**
     * CacheManager bean for Redis.
     *
     * @param connectionFactory redis connection
     * @param objectMapper jackson object mapper
     * @return cache manager instance
     */
    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {

        var keySerializer = new StringRedisSerializer();

        var roleSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Role.class);

        var credentialSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Credential.class);

        var roleCacheConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(30))
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(roleSerializer))
                        .disableCachingNullValues();

        var credentialCacheConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(15))
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(credentialSerializer))
                        .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put(ROLE_CACHE_NAME, roleCacheConfig);
        configs.put(CREDENTIAL_CACHE_NAME, credentialCacheConfig);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(15)))
                .withInitialCacheConfigurations(configs)
                .build();
    }
}