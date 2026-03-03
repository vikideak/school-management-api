package com.project.schoolapi.controller;

import com.project.schoolapi.model.EnrollmentJob;
import com.project.schoolapi.repository.EnrollmentJobRepository;
import com.project.schoolapi.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentJobRepository enrollmentJobRepository;

    @PostMapping
    public ResponseEntity<EnrollmentJob> enroll(
            @Valid @RequestParam UUID studentId,
            @Valid @RequestParam UUID schoolId) {

        EnrollmentJob job = enrollmentService.createEnrollment(studentId, schoolId);
        return ResponseEntity.accepted().body(job);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentJob> getStatus(
            @Valid @PathVariable String id
    ) {
        return enrollmentJobRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}