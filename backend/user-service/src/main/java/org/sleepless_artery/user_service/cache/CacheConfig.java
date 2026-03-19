package org.sleepless_artery.user_service.cache;

import org.sleepless_artery.user_service.dto.UserResponseDto;
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
 * Configures caches for user dto with TTL and serialization.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private static final String USER_ID_CACHE = "user:id";
    private static final String USER_EMAIL_CACHE = "user:email";

    /**
     * CacheManager bean for Redis.
     *
     * @param connectionFactory redis connection
     * @param objectMapper jackson object mapper
     * @return cache manager instance
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {

        var userSerializer = new JacksonJsonRedisSerializer<>(objectMapper, UserResponseDto.class);

        var userCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(userSerializer)
                );

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put(USER_ID_CACHE, userCacheConfig);
        configs.put(USER_EMAIL_CACHE, userCacheConfig);

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}
