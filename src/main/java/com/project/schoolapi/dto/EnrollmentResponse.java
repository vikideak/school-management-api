package com.project.schoolapi.dto;

import com.project.schoolapi.model.EnrollmentJob;
import com.project.schoolapi.model.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EnrollmentResponse {
    private String id;

    private String studentId;

    private String schoolId;

    private EnrollmentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String errorMessage;

    public static EnrollmentResponse fromEnrollmentJob(EnrollmentJob job) {
        return new EnrollmentResponse(
                job.getId(),
                job.getStudentId(),
                job.getSchoolId(),
                job.getEnrollmentStatus(),
                job.getCreatedAt(),
                job.getUpdatedAt(),
                job.getErrorMessage()
        );
    }
}