package org.sleepless_artery.enrollment_service.service;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.sleepless_artery.enrollment_service.exception.EnrollmentAlreadyExistsException;
import org.sleepless_artery.enrollment_service.model.Enrollment;
import org.sleepless_artery.enrollment_service.repository.EnrollmentRepository;
import org.sleepless_artery.enrollment_service.service.core.EnrollmentServiceImpl;
import org.sleepless_artery.enrollment_service.service.external.course.CourseExistenceChecker;
import org.sleepless_artery.enrollment_service.service.external.course.CourseExistenceVerificationResult;
import org.sleepless_artery.enrollment_service.service.external.user.UserExistenceChecker;
import org.sleepless_artery.enrollment_service.service.external.user.UserExistenceVerificationResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class EnrollmentFuzzTest {

    private final EnrollmentRepository enrollmentRepository = Mockito.mock(EnrollmentRepository.class);
    private final CourseExistenceChecker courseChecker = Mockito.mock(CourseExistenceChecker.class);
    private final UserExistenceChecker userChecker = Mockito.mock(UserExistenceChecker.class);

    private final EnrollmentServiceImpl enrollmentService =
            new EnrollmentServiceImpl(
                    enrollmentRepository,
                    courseChecker,
                    userChecker
            );

    @Property(tries = 500)
    void createEnrollmentShouldNotCrashOnRandomIds(
            @ForAll Long studentId,
            @ForAll Long courseId
    ) {

        when(userChecker.verifyUserExistence(any()))
                .thenReturn(UserExistenceVerificationResult.EXISTS);

        when(courseChecker.verifyCourseExistence(any()))
                .thenReturn(CourseExistenceVerificationResult.EXISTS);

        when(enrollmentRepository.existsByStudentIdAndCourseId(any(), any()))
                .thenReturn(false);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);

        when(enrollmentRepository.save(any()))
                .thenReturn(enrollment);

        Assertions.assertDoesNotThrow(() -> {

            Enrollment result =
                    enrollmentService.createEnrollment(studentId, courseId);

            Assertions.assertEquals(studentId, result.getStudentId());
            Assertions.assertEquals(courseId, result.getCourseId());
        });
    }

    @Property(tries = 500)
    void shouldAlwaysThrowWhenEnrollmentAlreadyExists(
            @ForAll Long studentId,
            @ForAll Long courseId
    ) {

        when(userChecker.verifyUserExistence(any()))
                .thenReturn(UserExistenceVerificationResult.EXISTS);

        when(courseChecker.verifyCourseExistence(any()))
                .thenReturn(CourseExistenceVerificationResult.EXISTS);

        when(enrollmentRepository.existsByStudentIdAndCourseId(any(), any()))
                .thenReturn(true);

        Assertions.assertThrows(
                EnrollmentAlreadyExistsException.class,
                () -> enrollmentService.createEnrollment(studentId, courseId)
        );
    }

    @Property(tries = 500)
    void existsCheckShouldReturnRepositoryResult(
            @ForAll Long studentId,
            @ForAll Long courseId,
            @ForAll boolean exists
    ) {

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(exists);

        boolean result =
                enrollmentService.existsByStudentIdAndCourseId(studentId, courseId);

        Assertions.assertEquals(exists, result);
    }
}