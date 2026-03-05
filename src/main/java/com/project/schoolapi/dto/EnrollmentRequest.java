package com.project.schoolapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class EnrollmentRequest {
    private UUID studentId;

    private UUID schoolId;
}
