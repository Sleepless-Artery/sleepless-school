package org.sleepless_artery.course_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.sleepless_artery.course_service.dto.CourseRequestDto;
import org.sleepless_artery.course_service.dto.CourseResponseDto;
import org.sleepless_artery.course_service.model.Course;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {

    CourseResponseDto toDto(Course course);

    Course toEntity(CourseRequestDto courseRequestDto);
}
