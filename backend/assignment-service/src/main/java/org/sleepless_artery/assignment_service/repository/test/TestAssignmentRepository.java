package org.sleepless_artery.assignment_service.repository.test;

import org.sleepless_artery.assignment_service.model.test.TestAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TestAssignmentRepository extends JpaRepository<TestAssignment, Long> {

    @Query("SELECT a FROM TestAssignment a WHERE a.lessonId = :lessonId AND LOWER(a.title) = LOWER(:title)")
    TestAssignment findByLessonIdAndTitleIgnoreCase(@Param("lessonId") Long lessonId, @Param("title") String title);
}
