package org.sleepless_artery.assignment_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.sleepless_artery.assignment_service.dto.request.FileAssignmentRequestDto;
import org.sleepless_artery.assignment_service.dto.response.FileAssignmentResponseDto;
import org.sleepless_artery.assignment_service.model.file.FileAssignment;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileAssignmentMapper {

    FileAssignmentResponseDto toDto(FileAssignment assignment);

    FileAssignment toEntity(FileAssignmentRequestDto assignmentRequestDto);
}
