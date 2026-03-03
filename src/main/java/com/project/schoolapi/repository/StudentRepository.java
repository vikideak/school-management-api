package com.project.schoolapi.repository;

import com.project.schoolapi.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, String> {
    int countBySchoolId(String schoolId);

    Page<Student> findBySchoolIdAndNameIgnoreCaseContaining(String schoolId, String name, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);
}
