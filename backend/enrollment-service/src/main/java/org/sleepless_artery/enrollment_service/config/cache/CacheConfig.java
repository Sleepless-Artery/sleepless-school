package org.sleepless_artery.enrollment_service.config.cache;

import org.sleepless_artery.enrollment_service.model.Enrollment;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Redis cache configuration.
 * <p>
 * Configures caches for enrollment queries.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String ENROLLMENT_EXISTS_CACHE = "enrollment:exists";
    public static final String STUDENT_ENROLLMENTS_CACHE = "enrollment:student";
    public static final String COURSE_ENROLLMENTS_CACHE = "enrollment:course";

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

        var booleanSerializer = new JacksonJsonRedisSerializer<>(objectMapper, Boolean.class);

        var listType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Enrollment.class);

        var listSerializer = new JacksonJsonRedisSerializer<>(objectMapper, listType);

        var existsCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(booleanSerializer)
                );

        var listCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(listSerializer)
                );

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put(ENROLLMENT_EXISTS_CACHE, existsCacheConfig);
        configs.put(STUDENT_ENROLLMENTS_CACHE, listCacheConfig);
        configs.put(COURSE_ENROLLMENTS_CACHE, listCacheConfig);

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}