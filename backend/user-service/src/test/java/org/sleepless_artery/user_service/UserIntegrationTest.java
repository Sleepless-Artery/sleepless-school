package org.sleepless_artery.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.mockito.Mockito;

@SpringBootTest(classes = UserIntegrationTest.TestConfig.class, properties = {"spring.main.allow-bean-definition-overriding=true","kafka.enabled=false","spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1","jakarta.persistence.jdbc.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1","spring.datasource.driver-class-name=org.h2.Driver","spring.datasource.username=sa","spring.datasource.password=","spring.jpa.hibernate.ddl-auto=update","spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect","spring.flyway.enabled=false"})
class UserIntegrationTest {

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
