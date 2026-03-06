package com.project.schoolapi.service;

import com.project.schoolapi.dto.PagedResponse;
import com.project.schoolapi.dto.Student;
import com.project.schoolapi.exception.DuplicateNameException;
import com.project.schoolapi.exception.NotFoundException;
import com.project.schoolapi.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Student createStudent(Student request) {
        if (studentRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateNameException("Student name already exists");
        }

        com.project.schoolapi.model.Student student = com.project.schoolapi.model.Student.builder()
                .name(request.getName())
                .build();

        return Student.fromStudentModel(studentRepository.save(student));
    }

    public PagedResponse<Student> searchStudents(UUID schoolId, String name, Pageable pageable) {
        return PagedResponse.fromPage(studentRepository.findBySchoolIdAndNameIgnoreCaseContaining(schoolId.toString(), name, pageable)
                .map(Student::fromStudentModel));
    }

    public Student getStudent(UUID id) {
        return studentRepository.findById(id.toString())
                .map(Student::fromStudentModel)
                .orElseThrow(() -> new NotFoundException("Student not found"));
    }

    public Student updateStudent(UUID id, Student request) {
        com.project.schoolapi.model.Student student = studentRepository.findById(id.toString())
                .orElseThrow(() -> new NotFoundException("Student not found"));

        if (!student.getName().equalsIgnoreCase(request.getName())
                && studentRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateNameException("Student name already exists");
        }

        student.setName(request.getName());

        return Student.fromStudentModel(studentRepository.save(student));
    }

    public void deleteStudent(UUID id) {
        studentRepository.deleteById(id.toString());
    }
}