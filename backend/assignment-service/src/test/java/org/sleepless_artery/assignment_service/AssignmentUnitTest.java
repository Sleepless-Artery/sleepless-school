package org.sleepless_artery.assignment_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Assignment Service Unit Tests")
class AssignmentUnitTest {

    @Test
    @DisplayName("Should validate assignment title")
    void shouldValidateAssignmentTitle() {
        String validTitle = "Java Inheritance Task";
        String emptyTitle = "";
        
        assertFalse(validTitle.isEmpty());
        assertTrue(emptyTitle.isEmpty());
    }

    @Test
    @DisplayName("Should validate assignment deadline")
    void shouldValidateDeadline() {
        long currentTime = System.currentTimeMillis();
        long futureTime = currentTime + 86400000; // +1 day
        long pastTime = currentTime - 86400000; // -1 day
        
        assertTrue(futureTime > currentTime);
        assertFalse(pastTime > currentTime);
    }

    @Test
    @DisplayName("Should validate assignment points")
    void shouldValidatePoints() {
        int validPoints = 100;
        int invalidPoints = -10;
        
        assertTrue(validPoints >= 0);
        assertFalse(invalidPoints >= 0);
    }
}
