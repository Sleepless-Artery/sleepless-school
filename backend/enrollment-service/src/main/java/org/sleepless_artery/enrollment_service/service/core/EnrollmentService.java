package org.sleepless_artery.enrollment_service.service.core;

import org.sleepless_artery.enrollment_service.model.Enrollment;

import java.util.List;


/**
 * Enrollment management service.
 * <p>
 * Provides operations for retrieving, creating and deleting enrollments.
 */
public interface EnrollmentService {

    List<Enrollment> getEnrollmentsByStudentId(Long studentId);

    List<Enrollment> getEnrollmentsByCourseId(Long courseId);

    Enrollment createEnrollment(Long studentId, Long courseId);

    void deleteEnrollment(Long studentId, Long courseId);

    void deleteEnrollmentsByStudentId(Long studentId);

    void deleteEnrollmentsByCourseId(Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}
