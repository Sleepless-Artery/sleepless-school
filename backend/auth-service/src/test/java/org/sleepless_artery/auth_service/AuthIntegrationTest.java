package org.sleepless_artery.auth_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.mockito.Mockito;

@SpringBootTest(classes = AuthIntegrationTest.TestConfig.class, properties = {"spring.main.allow-bean-definition-overriding=true","kafka.enabled=false"})
class AuthIntegrationTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        RedisConnectionFactory redisConnectionFactory() {
            return Mockito.mock(RedisConnectionFactory.class);
        }
    }

    @Test
    void contextLoads() {
    }
}
