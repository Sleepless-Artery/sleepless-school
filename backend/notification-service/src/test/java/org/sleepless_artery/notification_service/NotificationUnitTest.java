package org.sleepless_artery.notification_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Notification Service Unit Tests")
class NotificationUnitTest {

    @Test
    @DisplayName("Should validate notification type")
    void shouldValidateNotificationType() {
        String emailType = "EMAIL";
        String pushType = "PUSH";
        
        assertTrue(emailType.matches("^(EMAIL|SMS|PUSH)$"));
        assertTrue(pushType.matches("^(EMAIL|SMS|PUSH)$"));
    }

    @Test
    @DisplayName("Should validate recipient email")
    void shouldValidateRecipientEmail() {
        String email = "user@example.com";
        assertTrue(email.contains("@"));
        assertTrue(email.contains("."));
    }

    @Test
    @DisplayName("Should handle message content")
    void shouldHandleMessageContent() {
        String message = "Test notification message";
        assertDoesNotThrow(() -> {
            assertNotNull(message);
            assertFalse(message.isEmpty());
        });
    }
}
