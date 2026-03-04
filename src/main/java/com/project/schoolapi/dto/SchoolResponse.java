package com.project.schoolapi.dto;

import com.project.schoolapi.model.School;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SchoolResponse {
    private String id;

    private String name;

    private int capacity;

    public static SchoolResponse fromSchoolModel(School school) {
        return new SchoolResponse(school.getId(), school.getName(), school.getCapacity());
    }
}