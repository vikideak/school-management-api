package com.project.schoolapi.controller;

import com.project.schoolapi.dto.Enrollment;
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
    public ResponseEntity<Enrollment> enroll(
            @Valid @RequestBody Enrollment request
    ) {
        Enrollment job = enrollmentService.createEnrollment(request.getStudentId(), request.getSchoolId());
        return ResponseEntity.accepted().body(job);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getEnrollmentStatus(
            @PathVariable UUID id
    ) {
        Enrollment job = enrollmentService.getEnrollment(id);
        return ResponseEntity.ok(job);
    }
}