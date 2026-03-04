package com.project.schoolapi.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class EnrollmentRequest {
    private UUID studentId;

    private UUID schoolId;
}
