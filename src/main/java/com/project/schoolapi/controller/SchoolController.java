package com.project.schoolapi.controller;

import com.project.schoolapi.dto.SchoolRequest;
import com.project.schoolapi.dto.SchoolResponse;
import com.project.schoolapi.service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<SchoolResponse> create(
            @Valid @RequestBody SchoolRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<SchoolResponse>> search(
            @RequestParam(required = false) String name,
            Pageable pageable) {
        return ResponseEntity.ok(schoolService.search(name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolResponse> get(
            @Valid @PathVariable UUID id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(schoolService.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Valid @PathVariable UUID id
    ) {
        schoolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}