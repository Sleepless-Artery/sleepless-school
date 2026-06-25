package org.sleepless_artery.assignment_service.fuzz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.jqwik.api.*;
import org.sleepless_artery.assignment_service.dto.request.FileAssignmentRequestDto;

import static org.junit.jupiter.api.Assertions.*;


class JacksonSerializationFuzzTest {

    private final ObjectMapper mapper;

    public JacksonSerializationFuzzTest() {
        mapper = new ObjectMapper()
                .registerModule(
                        new JavaTimeModule()
                );
    }


    @Property(tries = 5000)
    void dtoSerializationMustNeverThrow(
            @ForAll String title,
            @ForAll String description,
            @ForAll Double score
    ) {
        FileAssignmentRequestDto dto = new FileAssignmentRequestDto();

        dto.setTitle(title);
        dto.setDescription(description);
        dto.setMaxScore(score);
        dto.setLessonId(1L);
        dto.setDisplayFilename("file.txt");

        assertDoesNotThrow(
                () -> mapper.writeValueAsString(dto)
        );
    }
}