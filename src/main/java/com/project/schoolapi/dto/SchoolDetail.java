package com.project.schoolapi.dto;

import com.project.schoolapi.model.School;
import com.project.schoolapi.model.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SchoolDetail {
    private String id;

    private String name;

    private int capacity;

    private List<Student> students;

    public static SchoolDetail fromModels(School school, List<Student> students) {
        return new SchoolDetail(school.getId(), school.getName(), school.getCapacity(), students);
    }
}