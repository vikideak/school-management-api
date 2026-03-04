package com.project.schoolapi.dto;

import com.project.schoolapi.model.School;
import com.project.schoolapi.model.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SchoolDetailResponse {
    private String id;

    private String name;

    private int capacity;

    private List<StudentInSchoolResponse> students;

    public static SchoolDetailResponse fromModels(School school, List<Student> students) {
        List<StudentInSchoolResponse> studentsInSchool = students.stream()
                .map(StudentInSchoolResponse::fromStudentModel)
                .toList();
        return new SchoolDetailResponse(school.getId(), school.getName(), school.getCapacity(), studentsInSchool);
    }
}