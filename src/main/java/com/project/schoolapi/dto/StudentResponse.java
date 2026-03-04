package com.project.schoolapi.dto;

import com.project.schoolapi.model.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudentResponse {
    private String id;

    private String name;

    private String schoolId;

    public static StudentResponse fromStudentModel(Student student) {
        String schoolId = student.getSchool() != null ? student.getSchool().getId() : null;
        return new StudentResponse(student.getId(), student.getName(), schoolId);
    }
}