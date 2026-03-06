package com.project.schoolapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class School {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @Min(50)
    @Max(2000)
    private int capacity;

    public static School fromSchoolModel(com.project.schoolapi.model.School school) {
        return new School(school.getId(), school.getName(), school.getCapacity());
    }
}