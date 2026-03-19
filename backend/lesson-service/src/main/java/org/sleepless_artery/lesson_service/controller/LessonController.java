package org.sleepless_artery.lesson_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.lesson_service.dto.LessonContentDto;
import org.sleepless_artery.lesson_service.dto.LessonInfoDto;
import org.sleepless_artery.lesson_service.dto.LessonRequestDto;
import org.sleepless_artery.lesson_service.service.core.LessonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "Lessons",
        description = "Lesson management API"
)
@Validated
@RestController
@RequestMapping("lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;


    @Operation(
            summary = "Get lesson by id",
            description = "Returns full lesson content by its identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lesson found"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LessonContentDto> getLesson(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }


    @Operation(
            summary = "Get lessons of course",
            description = "Returns paginated list of lessons belonging to the specified course"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lessons retrieved successfully")
    })
    @GetMapping("/course/{id}")
    public ResponseEntity<Page<LessonInfoDto>> getLessonsOfCourse(
            @Parameter(description = "Course ID") @PathVariable Long id,
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(lessonService.getLessonsByCourseId(id, pageable));
    }


    @Operation(
            summary = "Create lesson",
            description = "Creates a new lesson within the specified course"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lesson successfully created"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "409", description = "Lesson with the same title or sequence number already exists"),
            @ApiResponse(responseCode = "503", description = "Course service unavailable")
    })
    @PostMapping
    public ResponseEntity<LessonContentDto> createLesson(
            @Valid @RequestBody LessonRequestDto lessonRequestDto) {

        return new ResponseEntity<>(
                lessonService.createLesson(lessonRequestDto),
                HttpStatus.CREATED
        );
    }


    @Operation(
            summary = "Update lesson",
            description = "Updates an existing lesson"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lesson updated successfully"),
            @ApiResponse(responseCode = "404", description = "Lesson not found"),
            @ApiResponse(responseCode = "400", description = "Course ID does not match the original one"),
            @ApiResponse(responseCode = "409", description = "Lesson with the same title or sequence number already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LessonContentDto> updateLesson(
            @Parameter(description = "Lesson ID") @PathVariable Long id,
            @Valid @RequestBody LessonRequestDto lessonDto
    ) {
        return ResponseEntity.ok(lessonService.updateLesson(id, lessonDto));
    }


    @Operation(
            summary = "Delete lesson",
            description = "Deletes a lesson by its identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lesson deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Delete lessons by course",
            description = "Deletes all lessons belonging to the specified course"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "All lessons deleted successfully")
    })
    @DeleteMapping("/course/{id}")
    public ResponseEntity<Void> deleteLessonsFromCourse(
            @Parameter(description = "Course ID") @PathVariable Long id) {

        lessonService.deleteLessonsByCourseId(id);
        return ResponseEntity.noContent().build();
    }
}