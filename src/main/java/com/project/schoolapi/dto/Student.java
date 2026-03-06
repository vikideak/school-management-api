package com.project.schoolapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String schoolId;

    public static Student fromStudentModel(com.project.schoolapi.model.Student student) {
        String schoolId = student.getSchool() != null ? student.getSchool().getId() : null;
        return new Student(student.getId(), student.getName(), schoolId);
    }
}