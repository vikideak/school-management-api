package com.project.schoolapi.dto;

import com.project.schoolapi.model.School;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class SchoolResponse {
    private UUID id;

    private String name;

    private int capacity;

    public static SchoolResponse fromSchoolModel(School school) {
        return new SchoolResponse(UUID.fromString(school.getId()), school.getName(), school.getCapacity());
    }
}