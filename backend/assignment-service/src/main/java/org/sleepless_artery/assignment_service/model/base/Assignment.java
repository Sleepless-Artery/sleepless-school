package org.sleepless_artery.assignment_service.model.base;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * Base entity representing an assignment.
 *
 * <p>Contains common fields shared by all assignment types and
 * serves as the root of the assignment inheritance hierarchy.</p>
 *
 * <p>Uses {@link InheritanceType#JOINED} strategy, where each
 * assignment subtype is stored in a separate table linked
 * to the base {@code assignment} table.</p>
 */
@Entity
@Table(name = "assignment")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(
        name = "assignment_type",
        discriminatorType = DiscriminatorType.STRING
)
@Getter @Setter
@NoArgsConstructor
public abstract class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "max_score", nullable = false)
    private Double maxScore;

    @Column(name = "deadline")
    private LocalDateTime deadline;


    public abstract AssignmentType getAssignmentType();
}