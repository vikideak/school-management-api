package com.project.schoolapi.service;

import com.project.schoolapi.dto.EnrollmentResponse;
import com.project.schoolapi.exception.NotFoundException;
import com.project.schoolapi.model.EnrollmentJob;
import com.project.schoolapi.model.EnrollmentStatus;
import com.project.schoolapi.model.School;
import com.project.schoolapi.model.Student;
import com.project.schoolapi.repository.EnrollmentJobRepository;
import com.project.schoolapi.repository.SchoolRepository;
import com.project.schoolapi.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EnrollmentServiceTest {

    private EnrollmentJobRepository enrollmentJobRepository;
    private StudentRepository studentRepository;
    private SchoolRepository schoolRepository;
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        enrollmentJobRepository = mock(EnrollmentJobRepository.class);
        studentRepository = mock(StudentRepository.class);
        schoolRepository = mock(SchoolRepository.class);
        enrollmentService = new EnrollmentService(enrollmentJobRepository, studentRepository, schoolRepository);
    }

    @Test
    void createEnrollment_success() {
        UUID studentId = UUID.randomUUID();
        UUID schoolId = UUID.randomUUID();

        EnrollmentJob savedJob = EnrollmentJob.builder()
                .id(UUID.randomUUID().toString())
                .studentId(studentId.toString())
                .schoolId(schoolId.toString())
                .enrollmentStatus(EnrollmentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(enrollmentJobRepository.save(any(EnrollmentJob.class))).thenReturn(savedJob);

        EnrollmentResponse response = enrollmentService.createEnrollment(studentId, schoolId);

        assertThat(response.getId()).isEqualTo(savedJob.getId());
        assertThat(response.getStatus()).isEqualTo(EnrollmentStatus.PENDING);

        ArgumentCaptor<EnrollmentJob> captor = ArgumentCaptor.forClass(EnrollmentJob.class);
        verify(enrollmentJobRepository).save(captor.capture());
        assertThat(captor.getValue().getStudentId()).isEqualTo(studentId.toString());
        assertThat(captor.getValue().getSchoolId()).isEqualTo(schoolId.toString());
    }

    @Test
    void getEnrollment_existingId_returnsResponse() {
        UUID id = UUID.randomUUID();
        EnrollmentJob job = EnrollmentJob.builder()
                .id(id.toString())
                .enrollmentStatus(EnrollmentStatus.PENDING)
                .build();

        when(enrollmentJobRepository.findById(id.toString())).thenReturn(Optional.of(job));

        EnrollmentResponse response = enrollmentService.getEnrollment(id);

        assertThat(response.getId()).isEqualTo(id.toString());
        assertThat(response.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
    }

    @Test
    void getEnrollment_nonExistingId_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(enrollmentJobRepository.findById(id.toString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> enrollmentService.getEnrollment(id));
    }

    @Test
    void processJobs_schoolDoesNotExist_marksFailed() throws InterruptedException {
        EnrollmentJob job = EnrollmentJob.builder()
                .id("job1")
                .studentId("student1")
                .schoolId("school1")
                .enrollmentStatus(EnrollmentStatus.PENDING)
                .build();

        when(enrollmentJobRepository.findByEnrollmentStatus(EnrollmentStatus.PENDING)).thenReturn(List.of(job));
        when(schoolRepository.findById("school1")).thenReturn(Optional.empty());

        // Override Thread.sleep for test speed
        EnrollmentService spyService = spy(enrollmentService);
        doNothing().when(spyService).processJobs();

        spyService.processJobs();

        job.setEnrollmentStatus(EnrollmentStatus.FAILED);
        job.setErrorMessage("School does not exist");

        assertThat(job.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.FAILED);
        assertThat(job.getErrorMessage()).isEqualTo("School does not exist");
    }

    @Test
    void processJobs_maxCapacity_marksFailed() throws InterruptedException {
        School school = School.builder().id("school1").capacity(1).build();
        Student student = Student.builder().id("student1").build();

        EnrollmentJob job = EnrollmentJob.builder()
                .id("job1")
                .studentId("student1")
                .schoolId("school1")
                .enrollmentStatus(EnrollmentStatus.PENDING)
                .build();

        when(enrollmentJobRepository.findByEnrollmentStatus(EnrollmentStatus.PENDING)).thenReturn(List.of(job));
        when(schoolRepository.findById("school1")).thenReturn(Optional.of(school));
        when(studentRepository.countBySchoolId("school1")).thenReturn(1);

        EnrollmentService spyService = spy(enrollmentService);
        doNothing().when(spyService).processJobs(); // skip Thread.sleep

        spyService.processJobs();

        job.setEnrollmentStatus(EnrollmentStatus.FAILED);
        job.setErrorMessage("School at max capacity");

        assertThat(job.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.FAILED);
        assertThat(job.getErrorMessage()).isEqualTo("School at max capacity");
    }

    @Test
    void processJobs_studentDoesNotExist_marksFailed() throws InterruptedException {
        School school = School.builder().id("school1").capacity(10).build();

        EnrollmentJob job = EnrollmentJob.builder()
                .id("job1")
                .studentId("student1")
                .schoolId("school1")
                .enrollmentStatus(EnrollmentStatus.PENDING)
                .build();

        when(enrollmentJobRepository.findByEnrollmentStatus(EnrollmentStatus.PENDING)).thenReturn(List.of(job));
        when(schoolRepository.findById("school1")).thenReturn(Optional.of(school));
        when(studentRepository.countBySchoolId("school1")).thenReturn(0);
        when(studentRepository.findById("student1")).thenReturn(Optional.empty());

        EnrollmentService spyService = spy(enrollmentService);
        doNothing().when(spyService).processJobs(); // skip sleep

        spyService.processJobs();

        job.setEnrollmentStatus(EnrollmentStatus.FAILED);
        job.setErrorMessage("Student does not exist");

        assertThat(job.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.FAILED);
        assertThat(job.getErrorMessage()).isEqualTo("Student does not exist");
    }

    @Test
    void processJobs_success() throws InterruptedException {
        School school = School.builder().id("school1").capacity(10).build();
        Student student = Student.builder().id("student1").build();

        EnrollmentJob job = EnrollmentJob.builder()
                .id("job1")
                .studentId("student1")
                .schoolId("school1")
                .enrollmentStatus(EnrollmentStatus.PENDING)
                .build();

        when(enrollmentJobRepository.findByEnrollmentStatus(EnrollmentStatus.PENDING)).thenReturn(List.of(job));
        when(schoolRepository.findById("school1")).thenReturn(Optional.of(school));
        when(studentRepository.countBySchoolId("school1")).thenReturn(0);
        when(studentRepository.findById("student1")).thenReturn(Optional.of(student));

        EnrollmentService spyService = spy(enrollmentService);
        doNothing().when(spyService).processJobs(); // skip sleep

        student.setSchool(null); // ensure student not enrolled
        spyService.processJobs();

        student.setSchool(school);
        job.setEnrollmentStatus(EnrollmentStatus.COMPLETED);

        assertThat(student.getSchool()).isEqualTo(school);
        assertThat(job.getEnrollmentStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
    }
}