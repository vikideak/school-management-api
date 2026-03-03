package com.project.schoolapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SchoolRequest {
    @NotBlank
    public String name;

    @Min(50)
    @Max(2000)
    public int capacity;
}
