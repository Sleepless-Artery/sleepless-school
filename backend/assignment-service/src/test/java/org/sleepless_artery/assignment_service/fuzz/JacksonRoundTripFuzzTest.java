package org.sleepless_artery.assignment_service.fuzz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.sleepless_artery.assignment_service.dto.request.FileAssignmentRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class JacksonRoundTripFuzzTest {

    private final ObjectMapper mapper;

    public JacksonRoundTripFuzzTest() {
        mapper = new ObjectMapper()
                .registerModule(
                        new JavaTimeModule()
                );
    }


    @Property(tries = 5000)
    void jacksonRoundTrip(
            @ForAll String title,
            @ForAll String description
    ) throws Exception {

        FileAssignmentRequestDto dto = new FileAssignmentRequestDto();

        dto.setTitle(title);
        dto.setDescription(description);
        dto.setLessonId(1L);
        dto.setDisplayFilename("file.txt");

        String json = mapper.writeValueAsString(dto);

        FileAssignmentRequestDto restored =
                mapper.readValue(
                        json,
                        FileAssignmentRequestDto.class
                );

        assertEquals(
                dto.getTitle(),
                restored.getTitle()
        );

        assertEquals(
                dto.getDescription(),
                restored.getDescription()
        );
    }
}
