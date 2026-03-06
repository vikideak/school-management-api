package com.project.schoolapi.service;

import com.project.schoolapi.dto.PagedResponse;
import com.project.schoolapi.dto.School;
import com.project.schoolapi.dto.SchoolDetail;
import com.project.schoolapi.exception.DuplicateNameException;
import com.project.schoolapi.exception.MaxCapacityReachedException;
import com.project.schoolapi.exception.NotFoundException;
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

    public School createSchool(School request) {
        if (schoolRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateNameException("School name already exists");
        }

        com.project.schoolapi.model.School school = com.project.schoolapi.model.School.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .build();

        return School.fromSchoolModel(schoolRepository.save(school));
    }

    public PagedResponse<School> searchSchools(String name, Pageable pageable) {
        return PagedResponse.fromPage(schoolRepository.findByNameIgnoreCaseContaining(name, pageable)
                .map(School::fromSchoolModel));
    }

    public SchoolDetail getSchool(UUID id) {
        com.project.schoolapi.model.School school = schoolRepository.findById(id.toString())
                .orElseThrow(() -> new NotFoundException("School not found"));

        List<Student> students = studentRepository.findBySchoolId(id.toString());

        return SchoolDetail.fromModels(school, students);
    }

    public School updateSchool(UUID id, School request) {
        com.project.schoolapi.model.School school = schoolRepository.findById(id.toString())
                .orElseThrow(() -> new NotFoundException("School not found"));

        if (!school.getName().equalsIgnoreCase(request.getName())
                && schoolRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateNameException("School name already exists");
        }

        if (request.getCapacity() < school.getCapacity()) {
            int studentsInSchool = studentRepository.countBySchoolId(id.toString());
            if (request.getCapacity() < studentsInSchool) {
                throw new MaxCapacityReachedException("School capacity too low");
            }
        }

        school.setName(request.getName());
        school.setCapacity(request.getCapacity());

        return School.fromSchoolModel(schoolRepository.save(school));
    }

    public void deleteSchool(UUID id) {
        schoolRepository.deleteById(id.toString());
    }
}