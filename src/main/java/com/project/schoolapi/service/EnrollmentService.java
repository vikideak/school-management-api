package com.project.schoolapi.service;

import com.project.schoolapi.model.EnrollmentJob;
import com.project.schoolapi.model.EnrollmentStatus;
import com.project.schoolapi.model.School;
import com.project.schoolapi.model.Student;
import com.project.schoolapi.repository.EnrollmentJobRepository;
import com.project.schoolapi.repository.SchoolRepository;
import com.project.schoolapi.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentJobRepository enrollmentJobRepository;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;

    public EnrollmentJob createEnrollment(UUID studentId, UUID schoolId) {
        EnrollmentJob job = EnrollmentJob.builder()
                .studentId(studentId.toString())
                .schoolId(schoolId.toString())
                .enrollmentStatus(EnrollmentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return enrollmentJobRepository.save(job);
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processJobs() throws InterruptedException {
        List<EnrollmentJob> jobs = enrollmentJobRepository.findByEnrollmentStatus(EnrollmentStatus.PENDING);

        for (EnrollmentJob job : jobs) {
            job.setEnrollmentStatus(EnrollmentStatus.PROCESSING);
            job.setUpdatedAt(LocalDateTime.now());

            Thread.sleep(new Random().nextInt(3000));

            School school = schoolRepository.findById(job.getSchoolId()).orElse(null);
            if (school == null) {
                job.setEnrollmentStatus(EnrollmentStatus.FAILED);
                job.setErrorMessage("School does not exist");
                continue;
            }

            int count = studentRepository.countBySchoolId(school.getId());
            if (count >= school.getCapacity()) {
                job.setEnrollmentStatus(EnrollmentStatus.FAILED);
                job.setErrorMessage("School at max capacity");
                continue;
            }

            Student student = studentRepository.findById(job.getStudentId()).orElse(null);

            if (student == null) {
                job.setEnrollmentStatus(EnrollmentStatus.FAILED);
                job.setErrorMessage("Student does not exist");
                continue;
            }

            student.setSchool(school);
            studentRepository.save(student);

            job.setEnrollmentStatus(EnrollmentStatus.COMPLETED);
            job.setUpdatedAt(LocalDateTime.now());
        }
    }
}