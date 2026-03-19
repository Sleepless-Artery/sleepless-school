package org.sleepless_artery.submission_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.submission_service.dto.response.SubmissionResponseDto;
import org.sleepless_artery.submission_service.service.query.QueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(
        name = "Submission Query",
        description = "Endpoints for retrieving submissions"
)
@RestController
@RequestMapping("submissions/query")
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;


    @Operation(
            summary = "Get submission by ID",
            description = "Returns a submission by its identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Submission successfully retrieved",
                    content = @Content(schema = @Schema(implementation = SubmissionResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Submission not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponseDto> findSubmissionById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(queryService.findSubmissionById(id));
    }


    @Operation(
            summary = "Get submissions by assignment",
            description = "Returns all submissions for a specific assignment."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Submissions successfully retrieved"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Assignment not found"
            )
    })
    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<SubmissionResponseDto>> findAllSubmissionsByAssignmentId(
            @PathVariable("assignmentId") final Long assignmentId
    ) {
        return ResponseEntity.ok(queryService.findSubmissionsByAssignmentId(assignmentId));
    }


    @Operation(
            summary = "Get submission by assignment and student",
            description = "Returns a specific student's submission for an assignment."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Submission successfully retrieved",
                    content = @Content(schema = @Schema(implementation = SubmissionResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Submission not found"
            )
    })
    @GetMapping("/assignment/{assignmentId}/student/{studentId}")
    public ResponseEntity<SubmissionResponseDto> findSubmissionByAssignmentIdAndStudentId(
            @PathVariable("assignmentId") final Long assignmentId, @PathVariable("studentId") final Long studentId
    ) {
        return ResponseEntity.ok(queryService.findSubmissionByAssignmentIdAndStudentId(assignmentId, studentId));
    }
}
