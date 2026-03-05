package com.project.schoolapi.integration;

import com.project.schoolapi.dto.EnrollmentResponse;
import com.project.schoolapi.model.EnrollmentJob;
import com.project.schoolapi.model.EnrollmentStatus;
import com.project.schoolapi.model.School;
import com.project.schoolapi.model.Student;
import com.project.schoolapi.repository.EnrollmentJobRepository;
import com.project.schoolapi.repository.SchoolRepository;
import com.project.schoolapi.repository.StudentRepository;
import com.project.schoolapi.service.EnrollmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EnrollmentFlowIT {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentJobRepository enrollmentJobRepository;

    @Test
    @DisplayName("Full enrollment flow should complete successfully")
    void fullEnrollmentFlow_success() throws InterruptedException {

        // 1️⃣ Create and save school
        School school = schoolRepository.save(
                School.builder()
                        .name("Integration School")
                        .capacity(10)
                        .build()
        );

        // 2️⃣ Create and save student
        Student student = studentRepository.save(
                Student.builder()
                        .name("Integration Student")
                        .build()
        );

        UUID studentId = UUID.fromString(student.getId());
        UUID schoolId = UUID.fromString(school.getId());

        // 3️⃣ Create enrollment job
        EnrollmentResponse response = enrollmentService.createEnrollment(studentId, schoolId);

        assertThat(response.getStatus()).isEqualTo(EnrollmentStatus.PENDING);

        // 4️⃣ Process jobs manually (instead of waiting for scheduler)
        enrollmentService.processJobs();

        // 5️⃣ Verify job status updated
        EnrollmentJob updatedJob = enrollmentJobRepository
                .findById(response.getId())
                .orElseThrow();

        assertThat(updatedJob.getEnrollmentStatus())
                .isEqualTo(EnrollmentStatus.COMPLETED);

        // 6️⃣ Verify student is now linked to school
        Student updatedStudent = studentRepository
                .findById(student.getId())
                .orElseThrow();

        assertThat(updatedStudent.getSchool()).isNotNull();
        assertThat(updatedStudent.getSchool().getId())
                .isEqualTo(school.getId());
    }

    @Test
    @DisplayName("Enrollment should fail if school capacity is reached")
    void enrollmentFails_whenCapacityReached() throws InterruptedException {

        // 1️⃣ School with capacity 1
        School school = schoolRepository.save(
                School.builder()
                        .name("Small School")
                        .capacity(1)
                        .build()
        );

        // 2️⃣ Already enrolled student
        Student existingStudent = studentRepository.save(
                Student.builder()
                        .name("Existing Student")
                        .school(school)
                        .build()
        );

        // 3️⃣ New student
        Student newStudent = studentRepository.save(
                Student.builder()
                        .name("New Student")
                        .build()
        );

        UUID newStudentId = UUID.fromString(newStudent.getId());
        UUID schoolId = UUID.fromString(school.getId());

        // 4️⃣ Create enrollment job
        EnrollmentResponse response =
                enrollmentService.createEnrollment(newStudentId, schoolId);

        // 5️⃣ Process jobs
        enrollmentService.processJobs();

        // 6️⃣ Verify FAILED status
        EnrollmentJob job = enrollmentJobRepository
                .findById(response.getId())
                .orElseThrow();

        assertThat(job.getEnrollmentStatus())
                .isEqualTo(EnrollmentStatus.FAILED);

        assertThat(job.getErrorMessage())
                .isEqualTo("School at max capacity");

        // 7️⃣ Ensure student was NOT enrolled
        Student updatedStudent = studentRepository
                .findById(newStudent.getId())
                .orElseThrow();

        assertThat(updatedStudent.getSchool()).isNull();
    }
}