package com.project.schoolapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class SchoolRequest {
    @NotBlank
    String name;

    @Min(50)
    @Max(2000)
    int capacity;
}
