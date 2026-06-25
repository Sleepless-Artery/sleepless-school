package org.sleepless_artery.assignment_service.fuzz;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.sleepless_artery.assignment_service.mapper.AssignmentDtoMapper;
import org.sleepless_artery.assignment_service.mapper.FileAssignmentMapperImpl;
import org.sleepless_artery.assignment_service.mapper.TestAssignmentMapperImpl;
import org.sleepless_artery.assignment_service.model.file.FileAssignment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class MapperFuzzTest {

    private final AssignmentDtoMapper mapper;

    public MapperFuzzTest() {
        mapper = new AssignmentDtoMapper(
                new FileAssignmentMapperImpl(),
                new TestAssignmentMapperImpl()
        );
    }


    @Property(tries = 5000)
    void mapperNeverThrows(
            @ForAll String title,
            @ForAll String description
    ) {
        FileAssignment entity = new FileAssignment();

        entity.setTitle(title);
        entity.setDescription(description);

        assertDoesNotThrow(
                () -> mapper.toDto(entity)
        );
    }
}
