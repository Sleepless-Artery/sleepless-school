package org.sleepless_artery.submission_service.repository.core;

import org.sleepless_artery.submission_service.model.base.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s FROM Submission s WHERE s.assignmentId = :assignmentId")
    List<Submission> findAllByAssignmentId(@Param("assignmentId") Long assignmentId);

    @Query("SELECT s FROM Submission s WHERE s.assignmentId = :assignmentId and s.studentId = :studentId")
    Optional<Submission> findByAssignmentIdAndStudentId(
            @Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId
    );

    @Query("SELECT s.id FROM  Submission s WHERE s.assignmentId = :assignmentId")
    List<Long> findIdsByAssignmentId(@Param("assignmentId") Long assignmentId);

    @Query("SELECT s.id FROM  Submission s WHERE s.studentId = :studentId")
    List<Long> findIdsByStudentId(@Param("studentId") Long studentId);

    @Modifying
    @Query("DELETE FROM Submission b WHERE b.assignmentId = :assignmentId")
    void deleteAllByAssignmentId(@Param("assignmentId") Long assignmentId);

    @Modifying
    @Query("DELETE FROM Submission b WHERE b.studentId = :studentId")
    void deleteAllByStudentId(@Param("studentId") Long studentId);
}
