package com.project.schoolapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class StudentRequest {
    @NotBlank
    private String name;
}