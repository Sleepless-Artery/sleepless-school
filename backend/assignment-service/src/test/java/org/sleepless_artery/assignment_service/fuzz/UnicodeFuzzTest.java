package org.sleepless_artery.assignment_service.fuzz;

import net.jqwik.api.*;
import org.sleepless_artery.assignment_service.dto.request.FileAssignmentRequestDto;
import org.sleepless_artery.assignment_service.service.util.CommonAssignmentUpdater;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class UnicodeFuzzTest {

    private final CommonAssignmentUpdater updater;

    public UnicodeFuzzTest() {
        updater = new CommonAssignmentUpdater();
    }


    @Provide
    Arbitrary<String> unicodeStrings() {

        return Arbitraries.strings()
                .all()
                .ofMaxLength(500);
    }


    @Property(tries = 5000)
    void updaterHandlesUnicode(
            @ForAll("unicodeStrings")
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
}
