package com.project.schoolapi.controller;

import com.project.schoolapi.dto.EnrollmentRequest;
import com.project.schoolapi.dto.EnrollmentResponse;
import com.project.schoolapi.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(
            @Valid @RequestBody EnrollmentRequest request
    ) {
        EnrollmentResponse job = enrollmentService.createEnrollment(request.getStudentId(), request.getSchoolId());
        return ResponseEntity.accepted().body(job);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentStatus(
            @PathVariable UUID id
    ) {
        EnrollmentResponse job = enrollmentService.getEnrollment(id);
        return ResponseEntity.ok(job);
    }
}