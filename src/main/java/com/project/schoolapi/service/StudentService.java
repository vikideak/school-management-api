package com.project.schoolapi.service;

import com.project.schoolapi.dto.StudentRequest;
import com.project.schoolapi.dto.StudentResponse;
import com.project.schoolapi.exception.DuplicateNameException;
import com.project.schoolapi.exception.NotFoundException;
import com.project.schoolapi.model.Student;
import com.project.schoolapi.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateNameException("Student name already exists");
        }
        Student student = Student.builder()
                .name(request.getName())
                .build();

        return StudentResponse.fromStudentModel(studentRepository.save(student));
    }

    public Page<StudentResponse> search(UUID schoolId, String name, Pageable pageable) {
        return studentRepository.findBySchoolIdAndNameIgnoreCaseContaining(schoolId.toString(), name, pageable)
                .map(StudentResponse::fromStudentModel);
    }

    public StudentResponse get(UUID id) {
        return studentRepository.findById(id.toString())
                .map(StudentResponse::fromStudentModel)
                .orElseThrow(() -> new NotFoundException("Student not found"));
    }

    public void delete(UUID id) {
        studentRepository.deleteById(id.toString());
    }
}