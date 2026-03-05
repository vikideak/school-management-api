package com.project.schoolapi.controller;

import com.project.schoolapi.dto.EnrollmentRequest;
import com.project.schoolapi.dto.EnrollmentResponse;
import com.project.schoolapi.model.EnrollmentStatus;
import com.project.schoolapi.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EnrollmentControllerTest {

    private EnrollmentService enrollmentService;
    private EnrollmentController enrollmentController;

    @BeforeEach
    void setUp() {
        enrollmentService = mock(EnrollmentService.class);
        enrollmentController = new EnrollmentController(enrollmentService);
    }

    @Test
    void enroll_success() {
        UUID studentId = UUID.randomUUID();
        UUID schoolId = UUID.randomUUID();
        EnrollmentRequest request = new EnrollmentRequest(studentId, schoolId);

        EnrollmentResponse response = EnrollmentResponse.builder()
                .id(UUID.randomUUID().toString())
                .studentId(studentId.toString())
                .schoolId(schoolId.toString())
                .status(EnrollmentStatus.PENDING)
                .build();

        when(enrollmentService.createEnrollment(studentId, schoolId)).thenReturn(response);

        ResponseEntity<EnrollmentResponse> result = enrollmentController.enroll(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(result.getBody()).isEqualTo(response);

        ArgumentCaptor<UUID> studentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> schoolCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(enrollmentService).createEnrollment(studentCaptor.capture(), schoolCaptor.capture());

        assertThat(studentCaptor.getValue()).isEqualTo(studentId);
        assertThat(schoolCaptor.getValue()).isEqualTo(schoolId);
    }

    @Test
    void getEnrollmentStatus_success() {
        UUID enrollmentId = UUID.randomUUID();
        EnrollmentResponse response = EnrollmentResponse.builder()
                .id(enrollmentId.toString())
                .studentId(UUID.randomUUID().toString())
                .schoolId(UUID.randomUUID().toString())
                .status(null)
                .build();

        when(enrollmentService.getEnrollment(enrollmentId)).thenReturn(response);

        ResponseEntity<EnrollmentResponse> result = enrollmentController.getEnrollmentStatus(enrollmentId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);

        verify(enrollmentService).getEnrollment(enrollmentId);
    }

    @Test
    void enroll_serviceThrowsException_propagates() {
        UUID studentId = UUID.randomUUID();
        UUID schoolId = UUID.randomUUID();
        EnrollmentRequest request = new EnrollmentRequest(studentId, schoolId);

        when(enrollmentService.createEnrollment(studentId, schoolId))
                .thenThrow(new RuntimeException("Enrollment failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> enrollmentController.enroll(request));
        assertThat(ex.getMessage()).isEqualTo("Enrollment failed");
    }

    @Test
    void getEnrollmentStatus_serviceThrowsException_propagates() {
        UUID enrollmentId = UUID.randomUUID();

        when(enrollmentService.getEnrollment(enrollmentId))
                .thenThrow(new RuntimeException("Not found"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> enrollmentController.getEnrollmentStatus(enrollmentId));
        assertThat(ex.getMessage()).isEqualTo("Not found");
    }
}