package org.sleepless_artery.assignment_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.assignment_service.dto.request.FileAssignmentRequestDto;
import org.sleepless_artery.assignment_service.dto.response.AssignmentResponseDto;
import org.sleepless_artery.assignment_service.dto.response.FileAssignmentResponseDto;
import org.sleepless_artery.assignment_service.service.file.FileAssignmentService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(
        name = "File Assignments",
        description = "Operations for managing file-based assignments"
)
@Validated
@RestController
@RequestMapping("assignments/file")
@RequiredArgsConstructor
public class FileAssignmentController {

    private final FileAssignmentService assignmentService;


    @Operation(
            summary = "Download assignment file",
            description = "Downloads the file associated with a file assignment from MinIO storage."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "File successfully downloaded",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Assignment not found"
            )
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") final Long id) {
        var stream = assignmentService.downloadAssignmentFile(id);
        var assignment = assignmentService.findAssignmentById(id);

        var fileKey = assignment.getFileKey();
        var displayFilename = assignment.getDisplayFilename();
        var filename = displayFilename + fileKey.substring(fileKey.lastIndexOf("."));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition",
                        "attachment; filename=\"" + filename + "\"")
                .body(new InputStreamResource(stream));
    }


    @Operation(
            summary = "Create file assignment",
            description = "Creates a new file assignment and uploads the file to MinIO storage."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Assignment successfully created",
                    content = @Content(schema = @Schema(implementation = AssignmentResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or file validation failed"
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
                    responseCode = "500",
                    description = "File storage operation failed"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Lesson service unavailable"
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssignmentResponseDto> uploadAssignmentFile(
            @Valid @RequestPart("data") FileAssignmentRequestDto assignmentRequestDto,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assignmentService.createAssignment(assignmentRequestDto, file));
    }


    @Operation(
            summary = "Update file assignment metadata",
            description = "Updates assignment metadata without replacing the file."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Assignment successfully updated",
                    content = @Content(schema = @Schema(implementation = FileAssignmentResponseDto.class))
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
    public ResponseEntity<FileAssignmentResponseDto> updateFileAssignment(
            @PathVariable("id") final Long id,
            @Valid @NotNull @RequestBody FileAssignmentRequestDto assignmentRequestDto
    ) throws Exception {
        return ResponseEntity.ok(assignmentService.updateAssignment(id, assignmentRequestDto));
    }


    @Operation(
            summary = "Replace assignment file",
            description = "Replaces the file associated with the assignment and updates file metadata."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "File successfully updated",
                    content = @Content(schema = @Schema(implementation = FileAssignmentResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Assignment not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "File storage operation failed"
            )
    })
    @PutMapping(value = "/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileAssignmentResponseDto> updateFile(
            @PathVariable("id") final Long id,
            @RequestPart(value = "displayFilename", required = false) String displayFilename,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(assignmentService.updateAssignmentFile(id, displayFilename, file));
    }


    @Operation(
            summary = "Delete file assignment",
            description = "Deletes a file assignment and removes the associated file from storage."
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
    public ResponseEntity<Void> deleteFileAssignment(@PathVariable("id") final Long id) {
        assignmentService.deleteAssignmentById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}