package com.project.schoolapi.dto;

import jakarta.validation.constraints.NotBlank;

public class StudentRequest {
    @NotBlank
    String name;
}