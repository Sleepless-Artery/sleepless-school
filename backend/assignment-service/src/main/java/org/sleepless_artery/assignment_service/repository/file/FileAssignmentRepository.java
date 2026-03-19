package org.sleepless_artery.assignment_service.repository.file;

import org.sleepless_artery.assignment_service.model.file.FileAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface FileAssignmentRepository extends JpaRepository<FileAssignment, Long> {

    @Query("SELECT a.fileKey FROM FileAssignment a WHERE a.lessonId = :lessonId")
    List<String> findFileKeysByLessonId(@Param("lessonId") Long lessonId);

    @Query("SELECT a FROM FileAssignment a WHERE a.lessonId = :lessonId AND LOWER(a.title) = LOWER(:title)")
    FileAssignment findByLessonIdAndTitleIgnoreCase(@Param("lessonId") Long lessonId, @Param("title") String title);
}
