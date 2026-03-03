package com.project.schoolapi.dto;

import com.project.schoolapi.model.Student;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class StudentResponse {
    UUID id;

    String name;

    UUID schoolId;

    public static StudentResponse fromStudentModel(Student student) {
        return new StudentResponse(student.getId(), student.getName(), student.getSchool().getId());
    }
}