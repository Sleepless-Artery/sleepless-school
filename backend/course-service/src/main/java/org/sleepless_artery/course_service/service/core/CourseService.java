package org.sleepless_artery.course_service.service.core;

import org.sleepless_artery.course_service.dto.CourseRequestDto;
import org.sleepless_artery.course_service.dto.CourseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Set;


/**
 * Course management service.
 * <p>
 * Provides operations for retrieving, searching,
 * creating, updating and deleting courses.
 */
public interface CourseService {

    CourseResponseDto findById(Long id);

    Page<CourseResponseDto> findCourses(
            String title, Long authorId, String description,
            LocalDate startingDate, LocalDate endingDate, Pageable pageable
    );

    CourseResponseDto createCourse(CourseRequestDto courseRequestDto);

    CourseResponseDto createCourse(CourseRequestDto courseRequestDto, Long currentUserId, Set<String> currentUserRoles);

    CourseResponseDto updateCourse(Long id, CourseRequestDto courseRequestDto);

    CourseResponseDto updateCourse(Long id, CourseRequestDto courseRequestDto, Long currentUserId, Set<String> currentUserRoles);

    void updateCourseLastUpdateDate(Long id);

    void deleteCourse(Long id);

    void deleteCourse(Long id, Long currentUserId, Set<String> currentUserRoles);

    void deleteCoursesByAuthorId(Long authorId);

    void deleteCoursesByAuthorId(Long authorId, Long currentUserId, Set<String> currentUserRoles);

    boolean existsById(Long id);
}