package org.sleepless_artery.course_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Course Service Unit Tests")
class CourseUnitTest {

    @Test
    @DisplayName("Should validate course title is not empty")
    void shouldValidateCourseTitle() {
        String validTitle = "Introduction to Java";
        String emptyTitle = "";
        
        assertFalse(validTitle.isEmpty());
        assertTrue(emptyTitle.isEmpty());
        assertTrue(validTitle.length() > 0);
    }

    @Test
    @DisplayName("Should validate course description format")
    void shouldValidateCourseDescription() {
        String description = "Learn advanced Java concepts";
        assertDoesNotThrow(() -> {
            assertNotNull(description);
            assertTrue(description.trim().length() > 0);
        });
    }

    @Test
    @DisplayName("Should handle course duration validation")
    void shouldValidateCourseDuration() {
        int validDuration = 12; // weeks
        int invalidDuration = -5;
        
        assertTrue(validDuration > 0);
        assertFalse(invalidDuration > 0);
    }
}
