package org.sleepless_artery.submission_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.sleepless_artery.submission_service.dto.request.TestSubmissionRequestDto;
import org.sleepless_artery.submission_service.dto.response.TestSubmissionResponseDto;
import org.sleepless_artery.submission_service.model.test.TestSubmission;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TestSubmissionMapper {

    TestSubmission toEntity(TestSubmissionRequestDto requestDto);

    TestSubmissionResponseDto toDto(TestSubmission submission);
}
