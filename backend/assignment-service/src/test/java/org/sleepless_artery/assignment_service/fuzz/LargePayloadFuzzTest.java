package org.sleepless_artery.assignment_service.fuzz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.StringLength;
import org.sleepless_artery.assignment_service.dto.request.FileAssignmentRequestDto;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class LargePayloadFuzzTest {

    private final ObjectMapper mapper;

    public LargePayloadFuzzTest() {
        mapper = new ObjectMapper()
                .registerModule(
                        new JavaTimeModule()
                );
    }


    @Property(tries = 1000)
    void hugeStringsMustNotCrashJackson(
            @ForAll
            @StringLength(
                    min = 10000,
                    max = 100000
            )
            String value
    ) {
        FileAssignmentRequestDto dto = new FileAssignmentRequestDto();
        dto.setTitle(value);

        assertDoesNotThrow(
                () -> mapper.writeValueAsString(dto)
        );
    }
}
