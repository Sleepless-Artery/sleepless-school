package org.sleepless_artery.assignment_service.model.file;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sleepless_artery.assignment_service.model.base.Assignment;
import org.sleepless_artery.assignment_service.model.base.AssignmentType;


/**
 * Entity representing a file-based assignment.
 */
@Entity
@Table(name = "file_assignment")
@PrimaryKeyJoinColumn(name = "assignment_id")
@Getter @Setter
@NoArgsConstructor
public class FileAssignment extends Assignment {

    @Column(name = "file_key", nullable = false, unique = true)
    private String fileKey;

    @Column(name = "display_filename", nullable = false)
    private String displayFilename;

    @Override
    public AssignmentType getAssignmentType() {
        return AssignmentType.FILE;
    }
}