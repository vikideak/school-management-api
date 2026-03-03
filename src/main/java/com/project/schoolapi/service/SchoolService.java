package com.project.schoolapi.service;

import com.project.schoolapi.dto.SchoolRequest;
import com.project.schoolapi.dto.SchoolResponse;
import com.project.schoolapi.exception.DuplicateNameException;
import com.project.schoolapi.exception.NotFoundException;
import com.project.schoolapi.model.School;
import com.project.schoolapi.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;

    public SchoolResponse create(SchoolRequest request) {
        if (schoolRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateNameException("School name already exists");
        }

        School school = School.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .build();

        return SchoolResponse.fromSchoolModel(schoolRepository.save(school));
    }

    public Page<SchoolResponse> search(String name, Pageable pageable) {
        return schoolRepository.findByNameIgnoreCaseContaining(name, pageable)
                .map(SchoolResponse::fromSchoolModel);
    }

    public SchoolResponse get(UUID id) {
        return schoolRepository.findById(id.toString())
                .map(SchoolResponse::fromSchoolModel)
                .orElseThrow(() -> new NotFoundException("School not found"));
    }

    public void delete(UUID id) {
        if (!schoolRepository.existsById(id.toString()))
            throw new NotFoundException("School not found");

        schoolRepository.deleteById(id.toString());
    }
}