package org.sleepless_artery.assignment_service.fuzz;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import net.jqwik.api.*;
import org.sleepless_artery.assignment_service.dto.request.TestAssignmentRequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TestAssignmentValidationFuzzTest {

    private final Validator validator;

    public TestAssignmentValidationFuzzTest() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }


    @Property(tries = 5000)
    void optionsListSmallerThanTwoMustFail(
            @ForAll String option
    ) {
        TestAssignmentRequestDto dto = new TestAssignmentRequestDto();

        dto.setTitle("title");
        dto.setLessonId(1L);
        dto.setMaxScore(10.0);
        dto.setCondition("condition");
        dto.setOptions(List.of(option));
        dto.setCorrectOptionsIndices(List.of(0));

        var violations = validator.validate(dto);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals("options")
                        )
        );
    }


    @Property(tries = 5000)
    void blankConditionMustFail(
            @ForAll("blankStrings") String blank
    ) {
        TestAssignmentRequestDto dto = new TestAssignmentRequestDto();

        dto.setTitle("title");
        dto.setLessonId(1L);
        dto.setMaxScore(10.0);
        dto.setCondition(blank);
        dto.setOptions(List.of("A", "B"));
        dto.setCorrectOptionsIndices(List.of(0));

        var violations = validator.validate(dto);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals("condition")
                        )
        );
    }


    @Provide
    Arbitrary<String> blankStrings() {
        return Arbitraries.of(
                "",
                " ",
                "  ",
                "\t",
                "\n",
                "\r\n"
        );
    }
}