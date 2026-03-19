package org.sleepless_artery.assignment_service.repository.core;

import org.sleepless_artery.assignment_service.model.base.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("SELECT a FROM Assignment a WHERE a.lessonId = :lessonId")
    List<Assignment> findAllByLessonId(@Param("lessonId") Long lessonId);

    @Query("SELECT a.id FROM Assignment a WHERE a.lessonId = :lessonId")
    List<Long> findIdsByLessonId(@Param("lessonId") Long lessonId);

    @Modifying
    @Query("DELETE FROM Assignment a WHERE a.lessonId = :lessonId")
    void deleteAllByLessonId(@Param("lessonId") Long lessonId);
}
