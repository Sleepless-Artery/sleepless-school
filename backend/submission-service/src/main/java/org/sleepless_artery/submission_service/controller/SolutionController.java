package org.sleepless_artery.submission_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.submission_service.dto.request.FileSubmissionRequestDto;
import org.sleepless_artery.submission_service.dto.request.TestSubmissionRequestDto;
import org.sleepless_artery.submission_service.dto.response.FileSubmissionResponseDto;
import org.sleepless_artery.submission_service.dto.response.TestSubmissionResponseDto;
import org.sleepless_artery.submission_service.service.solution.file.FileSolutionService;
import org.sleepless_artery.submission_service.service.solution.test.TestingService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(
        name = "Submission Solutions",
        description = "Endpoints for submitting and managing assignment solutions"
)
@Validated
@RestController
@RequestMapping("submissions/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final TestingService testingService;
    private final FileSolutionService fileSolutionService;


    @Operation(
            summary = "Submit test solution",
            description = "Submits answers for a test assignment and automatically calculates the resulting score."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Test successfully completed",
                    content = @Content(schema = @Schema(implementation = TestSubmissionResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Assignment or student not found"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "External service unavailable"
            )
    })
    @PostMapping
    public ResponseEntity<TestSubmissionResponseDto> completeTest(
            @Validated @RequestBody final TestSubmissionRequestDto requestDto
    ) {
        return ResponseEntity.ok(testingService.completeTest(requestDto));
    }


    @Operation(
            summary = "Download submission file",
            description = "Downloads the file associated with a file-based submission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "File successfully downloaded",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Submission not found"
            )
    })
    @GetMapping("/file/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") final Long id) {
        var stream = fileSolutionService.downloadSubmissionFile(id);
        var submission = fileSolutionService.findSubmissionById(id);

        var fileKey = submission.getFileKey();
        var displayFilename = submission.getDisplayFilename();
        var filename = displayFilename + fileKey.substring(fileKey.lastIndexOf("."));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition",
                        "attachment; filename=\"" + filename + "\"")
                .body(new InputStreamResource(stream));
    }


    @Operation(
            summary = "Upload solution file",
            description = "Uploads a file submission for an assignment."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Submission successfully created",
                    content = @Content(schema = @Schema(implementation = FileSubmissionResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or file validation failed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Assignment or student not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Submission already exists"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "External service unavailable"
            )
    })
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileSubmissionResponseDto> uploadSolutionFile(
            @Validated @RequestPart("data") FileSubmissionRequestDto requestDto,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fileSolutionService.uploadSolution(requestDto, file));
    }


    @Operation(
            summary = "Update submission file",
            description = "Replaces the file associated with a submission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Submission successfully updated",
                    content = @Content(schema = @Schema(implementation = FileSubmissionResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Submission not found"
            )
    })
    @PutMapping(value = "/file/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileSubmissionResponseDto> updateSolutionFile(
            @PathVariable("id") final Long id,
            @Validated @RequestPart("data") FileSubmissionRequestDto requestDto,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(fileSolutionService.updateSolution(id, requestDto, file));
    }


    @Operation(
            summary = "Delete submission",
            description = "Deletes a submission and removes the associated file from storage."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Submission successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Submission not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolutionFile(@PathVariable("id") final Long id) {
        fileSolutionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}