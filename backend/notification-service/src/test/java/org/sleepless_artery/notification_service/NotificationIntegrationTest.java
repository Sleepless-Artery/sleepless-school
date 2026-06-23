package org.sleepless_artery.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

@SpringBootTest(classes = NotificationIntegrationTest.TestConfig.class, properties = {"spring.main.allow-bean-definition-overriding=true","kafka.enabled=false"})
class NotificationIntegrationTest {

    @TestConfiguration
    static class TestConfig {

    }

    @Test
    void contextLoads() {
    }
}
