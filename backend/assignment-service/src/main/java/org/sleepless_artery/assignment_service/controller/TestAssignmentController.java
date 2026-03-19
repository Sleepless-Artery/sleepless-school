package org.sleepless_artery.assignment_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.assignment_service.dto.request.TestAssignmentRequestDto;
import org.sleepless_artery.assignment_service.dto.response.AssignmentResponseDto;
import org.sleepless_artery.assignment_service.dto.response.TestAssignmentResponseDto;
import org.sleepless_artery.assignment_service.service.test.TestAssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "Test Assignments",
        description = "Operations for managing test assignments"
)
@Validated
@RestController
@RequestMapping("assignments/test")
@RequiredArgsConstructor
public class TestAssignmentController {

    private final TestAssignmentService assignmentService;


    @Operation(
            summary = "Create test assignment",
            description = "Creates a new test assignment associated with a lesson."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Assignment successfully created",
                    content = @Content(schema = @Schema(implementation = AssignmentResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lesson not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Assignment with the same title already exists"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Lesson service unavailable"
            )
    })
    @PostMapping
    public ResponseEntity<AssignmentResponseDto> createTestAssignment(
            @Valid @NotNull @RequestBody TestAssignmentRequestDto assignmentRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assignmentService.createAssignment(assignmentRequestDto));
    }


    @Operation(
            summary = "Update test assignment",
            description = "Updates metadata and content of an existing test assignment."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Assignment successfully updated",
                    content = @Content(schema = @Schema(implementation = TestAssignmentResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Assignment not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Assignment with the same title already exists"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<TestAssignmentResponseDto> updateTestAssignment(
            @Parameter(description = "Assignment ID", example = "10")
            @PathVariable("id") Long id,
            @Valid @NotNull @RequestBody TestAssignmentRequestDto assignmentRequestDto
    ) {
        return ResponseEntity.ok(assignmentService.updateAssignment(id, assignmentRequestDto));
    }


    @Operation(
            summary = "Delete test assignment",
            description = "Deletes a test assignment by its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Assignment successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Assignment not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestAssignment(
            @Parameter(description = "Assignment ID", example = "10")
            @PathVariable("id") final Long id) {

        assignmentService.deleteAssignmentById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}