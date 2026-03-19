package org.sleepless_artery.course_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.course_service.dto.CourseRequestDto;
import org.sleepless_artery.course_service.dto.CourseResponseDto;
import org.sleepless_artery.course_service.service.core.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@Tag(
        name = "Courses",
        description = "Course management API"
)
@Validated
@RestController
@RequestMapping("courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;


    @Operation(summary = "Get course by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course found"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> findById(
            @Parameter(description = "Course identifier", example = "1")
            @PathVariable @Positive final Long id
    ) {
        return ResponseEntity.ok(courseService.findById(id));
    }


    @Operation(
            summary = "Search courses",
            description = "Returns a paginated list of courses filtered by optional parameters"
    )
    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<CourseResponseDto>> findCourses(

            @Parameter(description = "Author identifier", example = "10")
            @RequestParam(required = false) Long authorId,

            @Parameter(description = "Course title filter", example = "Spring Boot")
            @RequestParam(required = false) String title,

            @Parameter(description = "Course description filter")
            @RequestParam(required = false) String description,

            @Parameter(description = "Filter courses updated after date", example = "2024-01-01")
            @RequestParam(required = false) LocalDate startingDate,

            @Parameter(description = "Filter courses updated before date", example = "2024-12-31")
            @RequestParam(required = false) LocalDate endingDate,

            @Parameter(description = "Pagination information")
            @PageableDefault() Pageable pageable
    ) {
        return ResponseEntity.ok(
                courseService.findCourses(
                        title,
                        authorId,
                        description,
                        startingDate,
                        endingDate,
                        pageable
                )
        );
    }


    @Operation(summary = "Create course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course successfully created"),
            @ApiResponse(responseCode = "409", description = "Course already exists"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(
            @Parameter(description = "Course creation request")
            @Valid @RequestBody final CourseRequestDto courseRequestDto
    ) {
        return ResponseEntity.ok(courseService.createCourse(courseRequestDto));
    }


    @Operation(
            summary = "Update course"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course successfully updated"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "409", description = "Course with same title already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(

            @Parameter(description = "Course identifier", example = "1")
            @PathVariable final Long id,

            @Parameter(description = "Updated course data")
            @Valid @RequestBody final CourseRequestDto courseRequestDto
    ) {
        return ResponseEntity.ok(courseService.updateCourse(id, courseRequestDto));
    }


    @Operation(summary = "Delete course")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Course successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course identifier", example = "1")
            @PathVariable final Long id
    ) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Delete courses by author",
            description = "Deletes all courses created by the specified author"
    )
    @ApiResponse(responseCode = "204", description = "Courses successfully deleted")
    @DeleteMapping("/author/{authorId}")
    public ResponseEntity<Void> deleteCoursesByAuthor(

            @Parameter(description = "Author identifier", example = "10")
            @PathVariable final Long authorId
    ) {
        courseService.deleteCoursesByAuthorId(authorId);
        return ResponseEntity.noContent().build();
    }
}