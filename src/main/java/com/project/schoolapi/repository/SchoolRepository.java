package com.project.schoolapi.repository;

import com.project.schoolapi.model.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, String> {
    Page<School> findByNameIgnoreCaseContaining(String name, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);
}