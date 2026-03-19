package org.sleepless_artery.course_service.repository;

import org.sleepless_artery.course_service.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    boolean existsByAuthorIdAndTitleIgnoreCase(Long authorId, String title);

    @Query("SELECT c.id FROM Course c WHERE c.authorId = :authorId")
    List<Long> findIdsByAuthorId(@Param("authorId") Long authorId);

    void deleteByAuthorId(Long authorId);
}
