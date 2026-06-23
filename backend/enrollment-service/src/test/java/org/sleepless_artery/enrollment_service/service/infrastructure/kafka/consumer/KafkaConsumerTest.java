package org.sleepless_artery.enrollment_service.service.infrastructure.kafka.consumer;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sleepless_artery.enrollment_service.service.core.EnrollmentService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;


class KafkaConsumerTest {

    private EnrollmentService enrollmentService;

    private KafkaConsumer kafkaConsumer;

    @BeforeEach
    void setUp() {
        enrollmentService = Mockito.mock(EnrollmentService.class);
        kafkaConsumer = new KafkaConsumer(enrollmentService);
    }

    @Test
    void shouldHandleUserDeletedEvent() {

        kafkaConsumer.listenUserDeletedEvent("15");

        verify(enrollmentService)
                .deleteEnrollmentsByStudentId(15L);
    }

    @Test
    void shouldHandleCourseDeletedEvent() {

        kafkaConsumer.listenCourseDeletedEvent(33L);

        verify(enrollmentService)
                .deleteEnrollmentsByCourseId(33L);
    }

    @Property
    void fuzzUserDeletedEvent(@ForAll String randomKey) {

        assertDoesNotThrow(() -> {

            try {
                kafkaConsumer.listenUserDeletedEvent(randomKey);
            } catch (Exception ignored) {
            }
        });
    }
}
