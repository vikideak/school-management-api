package com.project.schoolapi.controller;

import com.project.schoolapi.dto.PagedResponse;
import com.project.schoolapi.dto.SchoolDetailResponse;
import com.project.schoolapi.dto.SchoolRequest;
import com.project.schoolapi.dto.SchoolResponse;
import com.project.schoolapi.service.SchoolService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @PostMapping
    public ResponseEntity<SchoolResponse> createSchool(
            @Valid @RequestBody SchoolRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolService.createSchool(request));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<SchoolResponse>> searchSchools(
            @RequestParam(defaultValue = "0") @Min(0) int pageNumber,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "name") String sortBy
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.DEFAULT_DIRECTION, sortBy));
        return ResponseEntity.ok(schoolService.searchSchools(name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolDetailResponse> getSchool(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok((schoolService.getSchool(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchool(
            @PathVariable UUID id
    ) {
        schoolService.deleteSchool(id);
        return ResponseEntity.noContent().build();
    }
}