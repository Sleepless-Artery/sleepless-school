package org.sleepless_artery.submission_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.submission_service.dto.request.SolutionReviewDto;
import org.sleepless_artery.submission_service.dto.response.SubmissionResponseDto;
import org.sleepless_artery.submission_service.service.review.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "Submission Review",
        description = "Endpoints for grading and reviewing submissions"
)
@Validated
@RestController
@RequestMapping("submissions/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @Operation(
            summary = "Grade submission",
            description = "Assigns a score and optional review comment to a submission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Submission successfully graded",
                    content = @Content(schema = @Schema(implementation = SubmissionResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid review data or score exceeds maximum score"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Submission not found"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Assignment service unavailable"
            )
    })
    @PostMapping("/{id}")
    public ResponseEntity<SubmissionResponseDto> gradeSubmission(
            @PathVariable("id") final Long id,
            @Validated @RequestBody final SolutionReviewDto reviewDto
    ) {
        return ResponseEntity.ok(reviewService.reviewSubmissionById(id, reviewDto));
    }


    @Operation(
            summary = "Remove submission review",
            description = "Removes a previously assigned score and review comment."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Review successfully removed",
                    content = @Content(schema = @Schema(implementation = SubmissionResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Submission not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<SubmissionResponseDto> removeGradeFromSubmission(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(reviewService.removeReviewFromSubmissionById(id));
    }
}
