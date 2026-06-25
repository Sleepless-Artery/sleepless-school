package org.sleepless_artery.assignment_service.fuzz;

import net.jqwik.api.*;
import org.sleepless_artery.assignment_service.dto.request.FileAssignmentRequestDto;
import org.sleepless_artery.assignment_service.service.util.CommonAssignmentUpdater;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class NullabilityFuzzTest {

    private final CommonAssignmentUpdater updater;

    public NullabilityFuzzTest() {
        updater = new CommonAssignmentUpdater();
    }


    @Property(tries = 5000)
    void updaterHandlesNulls(
            @ForAll("nullableStrings")
            String title
    ) {
        CommonAssignmentUpdaterFuzzTest.DummyAssignment assignment =
                new CommonAssignmentUpdaterFuzzTest.DummyAssignment();

        FileAssignmentRequestDto dto = new FileAssignmentRequestDto();
        dto.setTitle(title);

        assertDoesNotThrow(
                () -> updater.updateAssignment(
                        assignment,
                        dto
                )
        );
    }


    @Provide
    Arbitrary<String> nullableStrings() {
        return Arbitraries.oneOf(
                Arbitraries.just(null),
                Arbitraries.strings()
        );
    }
}
