package com.project.schoolapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.schoolapi.model.EnrollmentJob;
import com.project.schoolapi.model.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @UUID
    private String studentId;

    @UUID
    private String schoolId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private EnrollmentStatus status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String errorMessage;

    public static Enrollment fromEnrollmentJob(EnrollmentJob job) {
        return new Enrollment(
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