package org.sleepless_artery.assignment_service.model.test;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.sleepless_artery.assignment_service.model.base.Assignment;
import org.sleepless_artery.assignment_service.model.base.AssignmentType;

import java.util.List;

import static org.hibernate.annotations.OnDeleteAction.CASCADE;


/**
 * Entity representing a test-based assignment.
 */
@Entity
@Table(name = "test_assignment")
@PrimaryKeyJoinColumn(name = "assignment_id")
@Getter @Setter
@NoArgsConstructor
public class TestAssignment extends Assignment {

    @Column(name = "test_condition")
    private String condition;

    @ElementCollection
    @CollectionTable(
            name = "test_assignment_options",
            joinColumns = @JoinColumn(name = "assignment_id")
    )
    @Column(name = "options", nullable = false)
    @OnDelete(action = CASCADE)
    private List<String> options;

    @ElementCollection
    @CollectionTable(
            name = "test_assignment_correct_options",
            joinColumns = @JoinColumn(name = "assignment_id")
    )
    @Column(name = "correct_options_indices", nullable = false)
    @OnDelete(action = CASCADE)
    private List<Integer> correctOptionsIndices;

    @Override
    public AssignmentType getAssignmentType() {
        return AssignmentType.TEST;
    }
}