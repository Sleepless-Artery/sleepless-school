package org.sleepless_artery.assignment_service.config.cache;

import org.sleepless_artery.assignment_service.dto.response.AssignmentResponseDto;
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
 * Configures caches for assignment dto with TTL and serialization.
 */
@EnableCaching
@Configuration
public class CacheConfig {

    public static final String ASSIGNMENT_CACHE_NAME = "assignment:id";
    public static final String LESSON_CACHE_NAME = "assignment:lesson";


    /**
     * CacheManager bean for Redis.
     *
     * @param connectionFactory redis connection
     * @param objectMapper jackson object mapper
     * @return cache manager instance
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {

        var assignmentSerializer = new JacksonJsonRedisSerializer<>(objectMapper, AssignmentResponseDto.class);

        var listType = objectMapper.getTypeFactory().constructCollectionType(List.class, AssignmentResponseDto.class);

        var listSerializer = new JacksonJsonRedisSerializer<>(objectMapper, listType);

        var assignmentCacheConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(15))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(assignmentSerializer)
                        );

        var assignmentListCacheConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(listSerializer)
                        );

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put(ASSIGNMENT_CACHE_NAME, assignmentCacheConfig);
        configs.put(LESSON_CACHE_NAME, assignmentListCacheConfig);

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}