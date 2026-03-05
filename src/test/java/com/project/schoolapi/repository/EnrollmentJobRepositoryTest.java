package com.project.schoolapi.repository;

import com.project.schoolapi.model.EnrollmentJob;
import com.project.schoolapi.model.EnrollmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EnrollmentJobRepositoryTest {

    @Autowired
    private EnrollmentJobRepository enrollmentJobRepository;

    @Test
    @DisplayName("Should save and retrieve enrollment job")
    void saveAndFindById_success() {

        EnrollmentJob job = EnrollmentJob.builder()
                .studentId("student-1")
                .schoolId("school-1")
                .enrollmentStatus(EnrollmentStatus.PENDING)
                .build();

        EnrollmentJob saved = enrollmentJobRepository.save(job);

        assertThat(saved.getId()).isNotNull();

        EnrollmentJob found = enrollmentJobRepository
                .findById(saved.getId())
                .orElseThrow();

        assertThat(found.getStudentId()).isEqualTo("student-1");
        assertThat(found.getSchoolId()).isEqualTo("school-1");
        assertThat(found.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.PENDING);
    }

    @Test
    @DisplayName("Should find jobs by enrollment status")
    void findByEnrollmentStatus_success() {

        EnrollmentJob pendingJob = enrollmentJobRepository.save(
                EnrollmentJob.builder()
                        .studentId("student-1")
                        .schoolId("school-1")
                        .enrollmentStatus(EnrollmentStatus.PENDING)
                        .build()
        );

        EnrollmentJob completedJob = enrollmentJobRepository.save(
                EnrollmentJob.builder()
                        .studentId("student-2")
                        .schoolId("school-2")
                        .enrollmentStatus(EnrollmentStatus.COMPLETED)
                        .build()
        );

        List<EnrollmentJob> pendingJobs =
                enrollmentJobRepository.findByEnrollmentStatus(EnrollmentStatus.PENDING);

        assertThat(pendingJobs)
                .hasSize(1)
                .first()
                .extracting(EnrollmentJob::getId)
                .isEqualTo(pendingJob.getId());
    }

    @Test
    @DisplayName("Should return empty list when no jobs match status")
    void findByEnrollmentStatus_noMatches() {

        enrollmentJobRepository.save(
                EnrollmentJob.builder()
                        .studentId("student-1")
                        .schoolId("school-1")
                        .enrollmentStatus(EnrollmentStatus.COMPLETED)
                        .build()
        );

        List<EnrollmentJob> failedJobs =
                enrollmentJobRepository.findByEnrollmentStatus(EnrollmentStatus.FAILED);

        assertThat(failedJobs).isEmpty();
    }
}