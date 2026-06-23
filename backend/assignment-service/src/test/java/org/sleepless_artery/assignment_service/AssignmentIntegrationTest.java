package org.sleepless_artery.assignment_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.sleepless_artery.assignment_service.service.infrastructure.minio.MinioService;
import org.mockito.Mockito;

@SpringBootTest(classes = AssignmentIntegrationTest.TestConfig.class, properties = {"spring.main.allow-bean-definition-overriding=true","kafka.enabled=false"})
class AssignmentIntegrationTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        RedisConnectionFactory redisConnectionFactory() {
            return Mockito.mock(RedisConnectionFactory.class);
        }

        @Bean
        MinioService minioServiceImpl() {
                return Mockito.mock(MinioService.class);
            }
    }

    @Test
    void contextLoads() {
    }
}
