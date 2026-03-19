package org.sleepless_artery.enrollment_service.service.core;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.enrollment_service.config.cache.CacheConfig;
import org.sleepless_artery.enrollment_service.exception.*;
import org.sleepless_artery.enrollment_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.enrollment_service.logging.event.LogEvent;
import org.sleepless_artery.enrollment_service.service.external.course.CourseExistenceChecker;
import org.sleepless_artery.enrollment_service.service.external.user.UserExistenceChecker;
import org.sleepless_artery.enrollment_service.model.Enrollment;
import org.sleepless_artery.enrollment_service.repository.EnrollmentRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service implementation responsible for managing course enrollments.
 * <p>
 * Provides operations for retrieving, creating, and deleting enrollments.
 * <p>
 * Integrates with external services to verify user and course existence
 * and uses caching to optimize frequently accessed queries.
 */
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    private final CourseExistenceChecker courseExistenceChecker;
    private final UserExistenceChecker userExistenceChecker;


    /**
     * Retrieves all enrollments for a given student.
     *
     * @param studentId student identifier
     * @return list of enrollments associated with the student
     */
    @Override
    @Cacheable(cacheNames = CacheConfig.STUDENT_ENROLLMENTS_CACHE, key = "#studentId")
    public List<Enrollment> getEnrollmentsByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }


    /**
     * Retrieves all enrollments for a given course.
     *
     * @param courseId course identifier
     * @return list of enrollments associated with the course
     */
    @Override
    @Cacheable(cacheNames = CacheConfig.COURSE_ENROLLMENTS_CACHE, key = "#courseId")
    public List<Enrollment> getEnrollmentsByCourseId(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }


    /**
     * Checks whether a student is enrolled in a specific course.
     *
     * @param studentId student identifier
     * @param courseId course identifier
     * @return {@code true} if the enrollment exists, {@code false} otherwise
     */
    @Override
    @Cacheable(cacheNames = CacheConfig.ENROLLMENT_EXISTS_CACHE, key = "{#studentId, #courseId}")
    public boolean existsByStudentIdAndCourseId(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }


    /**
     * Creates a new enrollment for a student in a course.
     *
     * @param studentId student identifier
     * @param courseId course identifier
     * @return created enrollment
     *
     * @throws UserDoesNotExistException if the student does not exist
     * @throws CourseDoesNotExistException if the course does not exist
     * @throws EnrollmentAlreadyExistsException if the student is already enrolled
     * @throws ExternalServiceUnavailableException if user or course service is unavailable
     */
    @Override
    @BusinessEvent(LogEvent.ENROLLMENT_CREATION)
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.ENROLLMENT_EXISTS_CACHE, key = "{#studentId, #courseId}"),
            @CacheEvict(cacheNames = CacheConfig.STUDENT_ENROLLMENTS_CACHE, key = "#studentId"),
            @CacheEvict(cacheNames = CacheConfig.COURSE_ENROLLMENTS_CACHE, key = "#courseId")
    })
    public Enrollment createEnrollment(Long studentId, Long courseId) {

        switch (userExistenceChecker.verifyUserExistence(studentId)) {
            case NOT_FOUND -> throw new UserDoesNotExistException("User not found with ID: " + studentId);
            case SERVICE_UNAVAILABLE -> throw new ExternalServiceUnavailableException("User service is unavailable");
        }

        switch (courseExistenceChecker.verifyCourseExistence(courseId)) {
            case NOT_FOUND -> throw new CourseDoesNotExistException("Course not found with ID: " + courseId);
            case SERVICE_UNAVAILABLE -> throw new ExternalServiceUnavailableException("Course service is unavailable");
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new EnrollmentAlreadyExistsException("User already enrolled to the course");
        }

        var enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);

        return enrollmentRepository.save(enrollment);
    }


    /**
     * Deletes a student's enrollment from a course.
     *
     * @param studentId student identifier
     * @param courseId course identifier
     *
     * @throws EnrollmentNotFoundException if the enrollment does not exist
     */
    @Override
    @BusinessEvent(LogEvent.ENROLLMENT_DELETION)
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.ENROLLMENT_EXISTS_CACHE, key = "{#studentId, #courseId}"),
            @CacheEvict(cacheNames = CacheConfig.STUDENT_ENROLLMENTS_CACHE, key = "#studentId"),
            @CacheEvict(cacheNames = CacheConfig.COURSE_ENROLLMENTS_CACHE, key = "#courseId")
    })
    public void deleteEnrollment(Long studentId, Long courseId) {
        if (!enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new EnrollmentNotFoundException("User is not enrolled to the course");
        }
        enrollmentRepository.deleteByStudentIdAndCourseId(studentId, courseId);
    }


    /**
     * Deletes all enrollments associated with a specific student.
     *
     * @param studentId student identifier
     */
    @Override
    @BusinessEvent(LogEvent.ENROLLMENT_DELETION_BY_STUDENT_ID)
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.STUDENT_ENROLLMENTS_CACHE, key = "#studentId"),
            @CacheEvict(cacheNames = CacheConfig.ENROLLMENT_EXISTS_CACHE, allEntries = true)
    })
    public void deleteEnrollmentsByStudentId(Long studentId) {
        enrollmentRepository.deleteAllByStudentId(studentId);
    }


    /**
     * Deletes all enrollments associated with a specific course.
     *
     * @param courseId course identifier
     */
    @Override
    @BusinessEvent(LogEvent.ENROLLMENT_DELETION_BY_COURSE_ID)
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.COURSE_ENROLLMENTS_CACHE, key = "#courseId"),
            @CacheEvict(cacheNames = CacheConfig.ENROLLMENT_EXISTS_CACHE, allEntries = true)
    })
    public void deleteEnrollmentsByCourseId(Long courseId) {
        enrollmentRepository.deleteAllByCourseId(courseId);
    }
}