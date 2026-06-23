package org.sleepless_artery.enrollment_service.service.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sleepless_artery.enrollment_service.exception.*;
import org.sleepless_artery.enrollment_service.model.Enrollment;
import org.sleepless_artery.enrollment_service.repository.EnrollmentRepository;
import org.sleepless_artery.enrollment_service.service.external.course.CourseExistenceChecker;
import org.sleepless_artery.enrollment_service.service.external.course.CourseExistenceVerificationResult;
import org.sleepless_artery.enrollment_service.service.external.user.UserExistenceChecker;
import org.sleepless_artery.enrollment_service.service.external.user.UserExistenceVerificationResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseExistenceChecker courseExistenceChecker;

    @Mock
    private UserExistenceChecker userExistenceChecker;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private final Long studentId = 1L;
    private final Long courseId = 2L;

    @Test
    void shouldReturnStudentEnrollments() {

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);

        when(enrollmentRepository.findByStudentId(studentId))
                .thenReturn(List.of(enrollment));

        List<Enrollment> result =
                enrollmentService.getEnrollmentsByStudentId(studentId);

        assertEquals(1, result.size());
        assertEquals(studentId, result.get(0).getStudentId());

        verify(enrollmentRepository).findByStudentId(studentId);
    }

    @Test
    void shouldReturnCourseEnrollments() {

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);

        when(enrollmentRepository.findByCourseId(courseId))
                .thenReturn(List.of(enrollment));

        List<Enrollment> result =
                enrollmentService.getEnrollmentsByCourseId(courseId);

        assertEquals(1, result.size());
        assertEquals(courseId, result.get(0).getCourseId());

        verify(enrollmentRepository).findByCourseId(courseId);
    }

    @Test
    void shouldReturnTrueWhenEnrollmentExists() {

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(true);

        boolean result =
                enrollmentService.existsByStudentIdAndCourseId(studentId, courseId);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenEnrollmentDoesNotExist() {

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(false);

        boolean result =
                enrollmentService.existsByStudentIdAndCourseId(studentId, courseId);

        assertFalse(result);
    }

    @Test
    void shouldCreateEnrollmentSuccessfully() {

        when(userExistenceChecker.verifyUserExistence(studentId))
                .thenReturn(UserExistenceVerificationResult.EXISTS);

        when(courseExistenceChecker.verifyCourseExistence(courseId))
                .thenReturn(CourseExistenceVerificationResult.EXISTS);

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(false);

        Enrollment savedEnrollment = new Enrollment();
        savedEnrollment.setStudentId(studentId);
        savedEnrollment.setCourseId(courseId);

        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenReturn(savedEnrollment);

        Enrollment result =
                enrollmentService.createEnrollment(studentId, courseId);

        assertNotNull(result);
        assertEquals(studentId, result.getStudentId());
        assertEquals(courseId, result.getCourseId());

        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {

        when(userExistenceChecker.verifyUserExistence(studentId))
                .thenReturn(UserExistenceVerificationResult.NOT_FOUND);

        assertThrows(
                UserDoesNotExistException.class,
                () -> enrollmentService.createEnrollment(studentId, courseId)
        );

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCourseDoesNotExist() {

        when(userExistenceChecker.verifyUserExistence(studentId))
                .thenReturn(UserExistenceVerificationResult.EXISTS);

        when(courseExistenceChecker.verifyCourseExistence(courseId))
                .thenReturn(CourseExistenceVerificationResult.NOT_FOUND);

        assertThrows(
                CourseDoesNotExistException.class,
                () -> enrollmentService.createEnrollment(studentId, courseId)
        );

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUserServiceUnavailable() {

        when(userExistenceChecker.verifyUserExistence(studentId))
                .thenReturn(UserExistenceVerificationResult.SERVICE_UNAVAILABLE);

        assertThrows(
                ExternalServiceUnavailableException.class,
                () -> enrollmentService.createEnrollment(studentId, courseId)
        );
    }

    @Test
    void shouldThrowWhenCourseServiceUnavailable() {

        when(userExistenceChecker.verifyUserExistence(studentId))
                .thenReturn(UserExistenceVerificationResult.EXISTS);

        when(courseExistenceChecker.verifyCourseExistence(courseId))
                .thenReturn(CourseExistenceVerificationResult.SERVICE_UNAVAILABLE);

        assertThrows(
                ExternalServiceUnavailableException.class,
                () -> enrollmentService.createEnrollment(studentId, courseId)
        );
    }

    @Test
    void shouldThrowWhenEnrollmentAlreadyExists() {

        when(userExistenceChecker.verifyUserExistence(studentId))
                .thenReturn(UserExistenceVerificationResult.EXISTS);

        when(courseExistenceChecker.verifyCourseExistence(courseId))
                .thenReturn(CourseExistenceVerificationResult.EXISTS);

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(true);

        assertThrows(
                EnrollmentAlreadyExistsException.class,
                () -> enrollmentService.createEnrollment(studentId, courseId)
        );

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void shouldDeleteEnrollmentSuccessfully() {

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(true);

        enrollmentService.deleteEnrollment(studentId, courseId);

        verify(enrollmentRepository)
                .deleteByStudentIdAndCourseId(studentId, courseId);
    }

    @Test
    void shouldThrowWhenDeletingMissingEnrollment() {

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(false);

        assertThrows(
                EnrollmentNotFoundException.class,
                () -> enrollmentService.deleteEnrollment(studentId, courseId)
        );
    }

    @Test
    void shouldDeleteEnrollmentsByStudentId() {

        enrollmentService.deleteEnrollmentsByStudentId(studentId);

        verify(enrollmentRepository)
                .deleteAllByStudentId(studentId);
    }

    @Test
    void shouldDeleteEnrollmentsByCourseId() {

        enrollmentService.deleteEnrollmentsByCourseId(courseId);

        verify(enrollmentRepository)
                .deleteAllByCourseId(courseId);
    }
}