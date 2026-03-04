package com.project.schoolapi.dto;

import com.project.schoolapi.model.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudentInSchoolResponse {
    private String id;

    private String name;

    public static StudentInSchoolResponse fromStudentModel(Student student) {
        return new StudentInSchoolResponse(student.getId(), student.getName());
    }
}