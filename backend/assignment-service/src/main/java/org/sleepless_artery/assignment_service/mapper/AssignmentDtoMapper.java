package org.sleepless_artery.assignment_service.mapper;

import org.hibernate.Hibernate;
import org.sleepless_artery.assignment_service.dto.response.AssignmentResponseDto;
import org.sleepless_artery.assignment_service.logging.annotation.Loggable;
import org.sleepless_artery.assignment_service.model.base.Assignment;
import org.sleepless_artery.assignment_service.model.file.FileAssignment;
import org.sleepless_artery.assignment_service.model.test.TestAssignment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;


/**
 * Mapper responsible for converting {@link Assignment} entities
 * to their corresponding {@link AssignmentResponseDto} representations.
 *
 * <p>Uses a type-based mapping strategy to delegate conversion
 * to specific mappers depending on the concrete assignment subtype.</p>
 */
@Component
public class AssignmentDtoMapper {

    private final Map<Class<? extends Assignment>, Function<Assignment, AssignmentResponseDto>> mappers;

    public AssignmentDtoMapper(FileAssignmentMapper fileAssignmentMapper,
                               TestAssignmentMapper testAssignmentMapper) {
        this.mappers = Map.of(
                FileAssignment.class, assignment -> fileAssignmentMapper.toDto((FileAssignment) assignment),
                TestAssignment.class, assignment -> testAssignmentMapper.toDto((TestAssignment) assignment)
        );
    }

    /**
     * Converts an {@link Assignment} entity to its corresponding response DTO.
     *
     * @param assignment assignment entity
     * @return assignment response DTO
     * @throws IllegalStateException if no mapper is registered for the assignment type
     */
    @Loggable
    public AssignmentResponseDto toDto(Assignment assignment) {
        var mapper = mappers.get(Hibernate.getClass(assignment));

        if (mapper == null) {
            throw new IllegalStateException("No mapper for " + assignment.getClass());
        }

        return mapper.apply(assignment);
    }
}