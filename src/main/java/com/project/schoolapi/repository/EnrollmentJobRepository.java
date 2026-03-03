package com.project.schoolapi.repository;

import com.project.schoolapi.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentJobRepository extends JpaRepository<EnrollmentJob, String> {
    List<EnrollmentJob> findByEnrollmentStatus(EnrollmentStatus status);
}
