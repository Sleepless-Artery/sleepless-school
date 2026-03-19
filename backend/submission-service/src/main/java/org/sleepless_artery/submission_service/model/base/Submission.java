package org.sleepless_artery.submission_service.model.base;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;



/**
 * Base entity representing a submission.
 *
 * <p>Contains common fields shared by all submission types and
 * serves as the root of the submission inheritance hierarchy.</p>
 *
 * <p>Uses {@link InheritanceType#JOINED} strategy, where each
 * assignment subtype is stored in a separate table linked
 * to the base {@code submission} table.</p>
 */
@Entity
@Table(name = "submission")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(
        name = "submission_type",
        discriminatorType = DiscriminatorType.STRING
)
@Getter @Setter
@NoArgsConstructor
public abstract class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "score")
    private Double score;

    public abstract SubmissionType getSubmissionType();
}
