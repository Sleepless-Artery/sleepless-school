package org.sleepless_artery.assignment_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.sleepless_artery.assignment_service.dto.request.TestAssignmentRequestDto;
import org.sleepless_artery.assignment_service.dto.response.TestAssignmentResponseDto;
import org.sleepless_artery.assignment_service.model.test.TestAssignment;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TestAssignmentMapper {

    TestAssignmentResponseDto toDto(TestAssignment assignment);

    TestAssignment toEntity(TestAssignmentRequestDto assignmentRequestDto);
}