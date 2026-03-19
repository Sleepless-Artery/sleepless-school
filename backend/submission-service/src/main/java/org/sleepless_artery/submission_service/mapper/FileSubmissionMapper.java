package org.sleepless_artery.submission_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.sleepless_artery.submission_service.dto.request.FileSubmissionRequestDto;
import org.sleepless_artery.submission_service.dto.response.FileSubmissionResponseDto;
import org.sleepless_artery.submission_service.model.file.FileSubmission;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileSubmissionMapper {

    FileSubmission toEntity(FileSubmissionRequestDto requestDto);

    FileSubmissionResponseDto toDto(FileSubmission submission);
}
