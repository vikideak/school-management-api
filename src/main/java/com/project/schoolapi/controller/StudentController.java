package com.project.schoolapi.controller;

import com.project.schoolapi.dto.StudentRequest;
import com.project.schoolapi.dto.StudentResponse;
import com.project.schoolapi.service.StudentService;
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
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponse> create(
            @Valid @RequestBody StudentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<StudentResponse>> search(
            @RequestParam UUID schoolId,
            @RequestParam("") String name,
            Pageable pageable) {
        return ResponseEntity.ok(studentService.search(schoolId, name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> get(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(studentService.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}