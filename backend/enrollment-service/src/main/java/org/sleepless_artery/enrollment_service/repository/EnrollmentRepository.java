package org.sleepless_artery.enrollment_service.repository;

import org.sleepless_artery.enrollment_service.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    void deleteAllByStudentId(Long studentId);

    void deleteAllByCourseId(Long courseId);

    void deleteByStudentIdAndCourseId(Long studentId, Long courseId);
}
