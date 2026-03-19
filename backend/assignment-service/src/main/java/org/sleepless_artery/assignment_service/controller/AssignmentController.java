package org.sleepless_artery.assignment_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.assignment_service.dto.response.AssignmentResponseDto;
import org.sleepless_artery.assignment_service.service.query.AssignmentQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(
        name = "Assignments",
        description = "Endpoints for querying assignments"
)
@Validated
@RestController
@RequestMapping("assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentQueryService queryService;

    @Operation(
            summary = "Find assignment by ID",
            description = "Returns assignment details by its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Assignment successfully retrieved",
                    content = @Content(schema = @Schema(implementation = AssignmentResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Assignment not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponseDto> findById(
            @Parameter(description = "Assignment ID", example = "15")
            @PathVariable("id") final Long id) {
        return ResponseEntity.ok(queryService.findById(id));
    }


    @Operation(
            summary = "Get assignments by lesson ID",
            description = "Returns all assignments associated with the specified lesson."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Assignments successfully retrieved"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lesson not found"
            )
    })
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<? extends AssignmentResponseDto>> findAssignmentsByLessonId(
            @Parameter(description = "Lesson ID", example = "3")
            @PathVariable("lessonId") final Long lessonId
    ) {
        return ResponseEntity.ok(queryService.findAllByLessonId(lessonId));
    }
}