package com.project.schoolapi.controller;

import com.project.schoolapi.dto.PagedResponse;
import com.project.schoolapi.dto.Student;
import com.project.schoolapi.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudentControllerTest {

    private StudentService studentService;
    private StudentController studentController;

    @BeforeEach
    void setUp() {
        studentService = mock(StudentService.class);
        studentController = new StudentController(studentService);
    }

    @Test
    void createStudent_success() {
        Student request = new Student();
        request.setName("John");
        Student response = new Student("id123", "John", null);

        when(studentService.createStudent(request)).thenReturn(response);

        ResponseEntity<Student> result = studentController.createStudent(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);

        verify(studentService).createStudent(request);
    }

    @Test
    void searchStudents_success() {
        UUID schoolId = UUID.randomUUID();

        Student student = new Student("id123", "John", null);
        PagedResponse<Student> pagedResponse =
                PagedResponse.fromPage(new PageImpl<>(List.of(student)));

        when(studentService.searchStudents(any(), anyString(), any(Pageable.class)))
                .thenReturn(pagedResponse);

        ResponseEntity<PagedResponse<Student>> result =
                studentController.searchStudents(schoolId, 0, 10, "Jo", "name");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(pagedResponse);

        ArgumentCaptor<UUID> schoolCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(studentService).searchStudents(
                schoolCaptor.capture(),
                nameCaptor.capture(),
                pageableCaptor.capture()
        );

        assertThat(schoolCaptor.getValue()).isEqualTo(schoolId);
        assertThat(nameCaptor.getValue()).isEqualTo("Jo");
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(0);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
    }

    @Test
    void getStudent_success() {
        UUID studentId = UUID.randomUUID();
        Student response = new Student(studentId.toString(), "John", null);

        when(studentService.getStudent(studentId)).thenReturn(response);

        ResponseEntity<Student> result = studentController.getStudent(studentId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);

        verify(studentService).getStudent(studentId);
    }

    @Test
    void deleteStudent_success() {
        UUID studentId = UUID.randomUUID();

        ResponseEntity<Void> result = studentController.deleteStudent(studentId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();

        verify(studentService).deleteStudent(studentId);
    }

    @Test
    void createStudent_serviceThrowsException_propagates() {
        Student request = new Student();
        request.setName("John");

        when(studentService.createStudent(request))
                .thenThrow(new RuntimeException("Error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> studentController.createStudent(request)
        );

        assertThat(ex.getMessage()).isEqualTo("Error");
    }

    @Test
    void searchStudents_serviceThrowsException_propagates() {
        UUID schoolId = UUID.randomUUID();

        when(studentService.searchStudents(any(), anyString(), any(Pageable.class)))
                .thenThrow(new RuntimeException("Error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> studentController.searchStudents(schoolId, 0, 10, "", "name")
        );

        assertThat(ex.getMessage()).isEqualTo("Error");
    }

    @Test
    void getStudent_serviceThrowsException_propagates() {
        UUID studentId = UUID.randomUUID();

        when(studentService.getStudent(studentId))
                .thenThrow(new RuntimeException("Error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> studentController.getStudent(studentId)
        );

        assertThat(ex.getMessage()).isEqualTo("Error");
    }

    @Test
    void deleteStudent_serviceThrowsException_propagates() {
        UUID studentId = UUID.randomUUID();

        doThrow(new RuntimeException("Error"))
                .when(studentService).deleteStudent(studentId);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> studentController.deleteStudent(studentId)
        );

        assertThat(ex.getMessage()).isEqualTo("Error");
    }
}