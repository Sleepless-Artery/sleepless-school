package org.sleepless_artery.submission_service.model.test;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.sleepless_artery.submission_service.model.base.Submission;
import org.sleepless_artery.submission_service.model.base.SubmissionType;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Entity representing a test-based submission.
 */
@Entity
@Table(name = "test_submission")
@PrimaryKeyJoinColumn(name = "submission_id")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestSubmission extends Submission {

    @ElementCollection
    @CollectionTable(
            name = "test_submission_answers",
            joinColumns = @JoinColumn(name = "submission_id")
    )
    @Column(name = "selected_option_index")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Integer> selectedOptionsIndices;

    @Override
    public SubmissionType getSubmissionType() {
        return SubmissionType.TEST;
    }

    @PrePersist
    private void init() {
        super.setSubmittedAt(LocalDateTime.now());
    }
}
