package com.project.schoolapi.dto;

import com.project.schoolapi.model.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class StudentResponse {
    private UUID id;

    private String name;

    private UUID schoolId;

    public static StudentResponse fromStudentModel(Student student) {
        UUID schoolId = student.getSchool() != null ? UUID.fromString(student.getSchool().getId()) : null;
        return new StudentResponse(UUID.fromString(student.getId()), student.getName(), schoolId);
    }
}