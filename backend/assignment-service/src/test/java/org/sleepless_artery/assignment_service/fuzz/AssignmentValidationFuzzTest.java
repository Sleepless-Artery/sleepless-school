package org.sleepless_artery.assignment_service.fuzz;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.StringLength;
import org.sleepless_artery.assignment_service.dto.request.FileAssignmentRequestDto;

import static org.junit.jupiter.api.Assertions.assertTrue;


class AssignmentValidationFuzzTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();


    @Property(tries = 5000)
    void titleLongerThan100CharactersMustFail(
            @ForAll
            @StringLength(min = 101, max = 1000)
            String title
    ) {
        FileAssignmentRequestDto dto = new FileAssignmentRequestDto();

        dto.setTitle(title);
        dto.setLessonId(1L);
        dto.setMaxScore(100.0);
        dto.setDisplayFilename("file.pdf");

        var violations = validator.validate(dto);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals("title")
                        )
        );
    }


    @Property(tries = 5000)
    void displayFilenameLongerThan100CharactersMustFail(
            @ForAll
            @StringLength(min = 101, max = 1000)
            String filename
    ) {
        FileAssignmentRequestDto dto = new FileAssignmentRequestDto();

        dto.setTitle("title");
        dto.setLessonId(1L);
        dto.setMaxScore(100.0);
        dto.setDisplayFilename(filename);

        var violations = validator.validate(dto);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals("displayFilename")
                        )
        );
    }

    @Property(tries = 5000)
    void negativeScoreMustFail(
            @ForAll
            @DoubleRange(min = -1_000_000, max = -0.01)
            double score
    ) {
        FileAssignmentRequestDto dto = new FileAssignmentRequestDto();

        dto.setTitle("title");
        dto.setLessonId(1L);
        dto.setMaxScore(score);
        dto.setDisplayFilename("file.pdf");

        var violations = validator.validate(dto);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals("maxScore")
                        )
        );
    }
}