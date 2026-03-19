package org.sleepless_artery.submission_service.repository.file;

import org.sleepless_artery.submission_service.model.file.FileSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface FileSubmissionRepository extends JpaRepository<FileSubmission, Long> {

    @Query("SELECT f.fileKey FROM FileSubmission f WHERE f.assignmentId = :assignmentId")
    List<String> findFileKeysByAssignmentId(@Param("assignmentId") Long assignmentId);

    @Query("SELECT f.fileKey FROM FileSubmission f WHERE f.studentId = :studentId")
    List<String> findFileKeysByStudentId(@Param("studentId") Long studentId);

    boolean existsByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
}
