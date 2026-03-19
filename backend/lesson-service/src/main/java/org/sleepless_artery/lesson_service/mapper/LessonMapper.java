package org.sleepless_artery.lesson_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.sleepless_artery.lesson_service.dto.LessonInfoDto;
import org.sleepless_artery.lesson_service.dto.LessonRequestDto;
import org.sleepless_artery.lesson_service.dto.LessonContentDto;
import org.sleepless_artery.lesson_service.model.Lesson;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LessonMapper {

    Lesson toEntity(LessonRequestDto lessonRequestDto);

    LessonContentDto toContentDto(Lesson lesson);

    LessonInfoDto toInfoDto(Lesson lesson);
}
