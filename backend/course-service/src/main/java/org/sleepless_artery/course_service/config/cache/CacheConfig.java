package org.sleepless_artery.course_service.config.cache;

import org.sleepless_artery.course_service.dto.CourseResponseDto;
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
import java.util.Map;


/**
 * Redis cache configuration.
 * <p>
 * Configures caches for course dto with TTL and serialization.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private static final String CACHE_NAME = "course:id";

    /**
     * CacheManager bean for Redis.
     *
     * @param connectionFactory redis connection
     * @param objectMapper jackson object mapper
     * @return cache manager instance
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {

        var courseSerializer = new JacksonJsonRedisSerializer<>(objectMapper, CourseResponseDto.class);

        var courseCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(courseSerializer)
                );

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put(CACHE_NAME, courseCacheConfig);

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}
