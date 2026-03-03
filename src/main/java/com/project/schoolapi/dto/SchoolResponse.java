package com.project.schoolapi.dto;

import com.project.schoolapi.model.School;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class SchoolResponse {
    UUID id;

    String name;

    int capacity;

    public static SchoolResponse fromSchoolModel(School school) {
        return new SchoolResponse(school.getId(), school.getName(), school.getCapacity());
    }
}