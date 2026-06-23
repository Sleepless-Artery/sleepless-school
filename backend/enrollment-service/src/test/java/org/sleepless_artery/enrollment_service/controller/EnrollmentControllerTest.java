package org.sleepless_artery.enrollment_service.controller;

import org.junit.jupiter.api.Test;
import org.sleepless_artery.enrollment_service.model.Enrollment;
import org.sleepless_artery.enrollment_service.service.core.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnrollmentService enrollmentService;


    @Test
    void shouldCheckEnrollment() throws Exception {

        when(enrollmentService.existsByStudentIdAndCourseId(1L, 2L))
                .thenReturn(true);

        mockMvc.perform(
                        get("/enrollments")
                                .param("studentId", "1")
                                .param("courseId", "2")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldGetStudentEnrollments() throws Exception {

        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);
        enrollment.setStudentId(1L);
        enrollment.setCourseId(2L);

        when(enrollmentService.getEnrollmentsByStudentId(1L))
                .thenReturn(List.of(enrollment));

        mockMvc.perform(get("/enrollments/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(1))
                .andExpect(jsonPath("$[0].courseId").value(2));
    }

    @Test
    void shouldGetCourseEnrollments() throws Exception {

        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);
        enrollment.setStudentId(1L);
        enrollment.setCourseId(2L);

        when(enrollmentService.getEnrollmentsByCourseId(2L))
                .thenReturn(List.of(enrollment));

        mockMvc.perform(get("/enrollments/course/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(1))
                .andExpect(jsonPath("$[0].courseId").value(2));
    }

    @Test
    void shouldCreateEnrollment() throws Exception {

        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);
        enrollment.setStudentId(1L);
        enrollment.setCourseId(2L);

        when(enrollmentService.createEnrollment(1L, 2L))
                .thenReturn(enrollment);

        mockMvc.perform(
                        post("/enrollments")
                                .param("studentId", "1")
                                .param("courseId", "2")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.courseId").value(2));
    }

    @Test
    void shouldDeleteEnrollment() throws Exception {

        mockMvc.perform(
                        delete("/enrollments")
                                .param("studentId", "1")
                                .param("courseId", "2")
                )
                .andExpect(status().isNoContent());

        verify(enrollmentService)
                .deleteEnrollment(1L, 2L);
    }

    @Test
    void shouldDeleteStudentEnrollments() throws Exception {

        mockMvc.perform(delete("/enrollments/student/1"))
                .andExpect(status().isNoContent());

        verify(enrollmentService)
                .deleteEnrollmentsByStudentId(1L);
    }

    @Test
    void shouldDeleteCourseEnrollments() throws Exception {

        mockMvc.perform(delete("/enrollments/course/2"))
                .andExpect(status().isNoContent());

        verify(enrollmentService)
                .deleteEnrollmentsByCourseId(2L);
    }
}