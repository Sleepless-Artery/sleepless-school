package org.sleepless_artery.submission_service.model.file;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sleepless_artery.submission_service.model.base.Submission;
import org.sleepless_artery.submission_service.model.base.SubmissionStatus;
import org.sleepless_artery.submission_service.model.base.SubmissionType;

import java.time.LocalDateTime;


/**
 * Entity representing a file-based submission.
 */
@Entity
@Table(name = "file_submission")
@PrimaryKeyJoinColumn(name = "submission_id")
@Getter @Setter
@NoArgsConstructor
public class FileSubmission extends Submission {

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SubmissionStatus status;

    @Column(name = "review_comment", length = 1000)
    private String reviewComment;

    @Column(name = "file_key", nullable = false)
    private String fileKey;

    @Column(name = "display_filename")
    private String displayFilename;


    @Override
    public SubmissionType getSubmissionType() {
        return SubmissionType.FILE;
    }


    @PrePersist
    private void init() {
        super.setSubmittedAt(LocalDateTime.now());
        setStatus(SubmissionStatus.SUBMITTED);
    }
}
