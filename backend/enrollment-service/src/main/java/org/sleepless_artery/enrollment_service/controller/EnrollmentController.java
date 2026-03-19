package org.sleepless_artery.enrollment_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.enrollment_service.model.Enrollment;
import org.sleepless_artery.enrollment_service.service.core.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(
        name = "Enrollments",
        description = "Enrollment management API"
)
@RestController
@RequestMapping("enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;


    @Operation(
            summary = "Check if student is enrolled in course",
            description = "Returns true if the student is enrolled in the specified course"
    )
    @ApiResponse(responseCode = "200", description = "Enrollment status retrieved")
    @GetMapping
    public ResponseEntity<Boolean> checkEnrollment(
            @Parameter(description = "Student ID", example = "10")
            @RequestParam Long studentId,

            @Parameter(description = "Course ID", example = "5")
            @RequestParam Long courseId
    ) {
        return ResponseEntity.ok(enrollmentService.existsByStudentIdAndCourseId(studentId, courseId));
    }


    @Operation(
            summary = "Get student enrollments",
            description = "Returns all enrollments of a specific student"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Enrollments retrieved")
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Enrollment>> getStudentEnrollments(
            @Parameter(description = "Student ID", example = "10")
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudentId(studentId));
    }


    @Operation(
            summary = "Get course enrollments",
            description = "Returns all students enrolled in a specific course"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Enrollments retrieved")
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Enrollment>> getCourseEnrollments(
            @Parameter(description = "Course ID", example = "5")
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourseId(courseId));
    }


    @Operation(
            summary = "Create enrollment",
            description = "Enrolls a student in a course"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Enrollment created"),
            @ApiResponse(responseCode = "404", description = "User or course not found"),
            @ApiResponse(responseCode = "409", description = "Student already enrolled"),
            @ApiResponse(responseCode = "503", description = "External service unavailable")
    })
    @PostMapping
    public ResponseEntity<Enrollment> createEnrollment(
            @Parameter(description = "Student ID", example = "10")
            @RequestParam Long studentId,

            @Parameter(description = "Course ID", example = "5")
            @RequestParam Long courseId
    ) {
        return ResponseEntity.ok(enrollmentService.createEnrollment(studentId, courseId));
    }


    @Operation(
            summary = "Delete enrollment",
            description = "Removes a student from a course"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Enrollment deleted"),
            @ApiResponse(responseCode = "404", description = "Enrollment not found")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteEnrollment(
            @Parameter(description = "Student ID", example = "10")
            @RequestParam Long studentId,

            @Parameter(description = "Course ID", example = "5")
            @RequestParam Long courseId
    ) {
        enrollmentService.deleteEnrollment(studentId, courseId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Delete enrollments by student",
            description = "Deletes all enrollments of a specific student"
    )
    @ApiResponse(responseCode = "204", description = "Enrollments deleted")
    @DeleteMapping("/student/{studentId}")
    public ResponseEntity<Void> deleteStudentEnrollments(
            @Parameter(description = "Student ID", example = "10")
            @PathVariable Long studentId
    ) {
        enrollmentService.deleteEnrollmentsByStudentId(studentId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Delete enrollments by course",
            description = "Deletes all enrollments for a specific course"
    )
    @ApiResponse(responseCode = "204", description = "Enrollments deleted")
    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<Void> deleteCourseEnrollments(
            @Parameter(description = "Course ID", example = "5")
            @PathVariable Long courseId
    ) {
        enrollmentService.deleteEnrollmentsByCourseId(courseId);
        return ResponseEntity.noContent().build();
    }
}