package com.project.schoolapi.service;

import com.project.schoolapi.dto.PagedResponse;
import com.project.schoolapi.dto.SchoolDetailResponse;
import com.project.schoolapi.dto.SchoolRequest;
import com.project.schoolapi.dto.SchoolResponse;
import com.project.schoolapi.exception.DuplicateNameException;
import com.project.schoolapi.exception.NotFoundException;
import com.project.schoolapi.model.School;
import com.project.schoolapi.model.Student;
import com.project.schoolapi.repository.SchoolRepository;
import com.project.schoolapi.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;

    public SchoolResponse createSchool(SchoolRequest request) {
        if (schoolRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateNameException("School name already exists");
        }

        School school = School.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .build();

        return SchoolResponse.fromSchoolModel(schoolRepository.save(school));
    }

    public PagedResponse<SchoolResponse> searchSchools(String name, Pageable pageable) {
        return PagedResponse.fromPage(schoolRepository.findByNameIgnoreCaseContaining(name, pageable)
                .map(SchoolResponse::fromSchoolModel));
    }

    public SchoolDetailResponse getSchool(UUID id) {
        School school = schoolRepository.findById(id.toString())
                .orElseThrow(() -> new NotFoundException("School not found"));

        List<Student> students = studentRepository.findBySchoolId(id.toString());

        return SchoolDetailResponse.fromModels(school, students);
    }

    public void deleteSchool(UUID id) {
        schoolRepository.deleteById(id.toString());
    }
}