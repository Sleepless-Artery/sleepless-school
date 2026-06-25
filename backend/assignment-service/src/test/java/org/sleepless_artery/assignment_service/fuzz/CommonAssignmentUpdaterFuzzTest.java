package org.sleepless_artery.assignment_service.fuzz;

import net.jqwik.api.*;
import org.sleepless_artery.assignment_service.dto.request.FileAssignmentRequestDto;
import org.sleepless_artery.assignment_service.model.base.Assignment;
import org.sleepless_artery.assignment_service.model.base.AssignmentType;
import org.sleepless_artery.assignment_service.service.util.CommonAssignmentUpdater;

import static org.junit.jupiter.api.Assertions.*;


class CommonAssignmentUpdaterFuzzTest {

    private final CommonAssignmentUpdater updater;

    public CommonAssignmentUpdaterFuzzTest() {
        updater = new CommonAssignmentUpdater();
    }


    @Property(tries = 10000)
    void updaterMustNeverThrow(
            @ForAll String title,
            @ForAll String description,
            @ForAll Double score
    ) {
        DummyAssignment assignment = new DummyAssignment();
        FileAssignmentRequestDto dto = new FileAssignmentRequestDto();

        dto.setTitle(title);
        dto.setDescription(description);
        dto.setMaxScore(score);

        assertDoesNotThrow(
                () -> updater.updateAssignment(
                        assignment,
                        dto
                )
        );
    }


    @Property(tries = 5000)
    void updaterMustCopyProvidedValues(
            @ForAll String title,
            @ForAll String description
    ) {
        DummyAssignment assignment = new DummyAssignment();
        FileAssignmentRequestDto dto = new FileAssignmentRequestDto();

        dto.setTitle(title);
        dto.setDescription(description);

        updater.updateAssignment(
                assignment,
                dto
        );

        assertEquals(title, assignment.getTitle());
        assertEquals(description, assignment.getDescription());
    }


    static class DummyAssignment extends Assignment {

        @Override
        public AssignmentType getAssignmentType() {
            return null;
        }
    }
}