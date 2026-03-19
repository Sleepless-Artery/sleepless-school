package org.sleepless_artery.lesson_service.config.cache;

import org.sleepless_artery.lesson_service.dto.LessonInfoDto;
import org.sleepless_artery.lesson_service.dto.LessonContentDto;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
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
 * Configures caches for lesson DTOs with TTL and serialization.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private static final String LESSON_CACHE_NAME = "lesson:id";
    private static final String LESSON_PAGE_CACHE_NAME = "lesson:course:id";

    /**
     * CacheManager bean for Redis.
     *
     * @param connectionFactory redis connection
     * @param objectMapper jackson object mapper
     * @return cache manager instance
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {

        var lessonSerializer = new JacksonJsonRedisSerializer<>(objectMapper, LessonContentDto.class);

        var pageType = objectMapper.getTypeFactory().constructParametricType(PageImpl.class, LessonInfoDto.class);
        var lessonPageSerializer = new JacksonJsonRedisSerializer<>(objectMapper, pageType);


        var lessonCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(lessonSerializer)
                );

        var lessonPageCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(lessonPageSerializer)
                );

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put(LESSON_CACHE_NAME, lessonCacheConfig);
        configs.put(LESSON_PAGE_CACHE_NAME, lessonPageCacheConfig);

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}