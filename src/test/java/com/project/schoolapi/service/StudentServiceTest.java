package com.project.schoolapi.service;

import com.project.schoolapi.dto.PagedResponse;
import com.project.schoolapi.dto.StudentRequest;
import com.project.schoolapi.dto.StudentResponse;
import com.project.schoolapi.exception.DuplicateNameException;
import com.project.schoolapi.exception.NotFoundException;
import com.project.schoolapi.model.Student;
import com.project.schoolapi.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudentServiceTest {

    private StudentRepository studentRepository;
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentRepository = mock(StudentRepository.class);
        studentService = new StudentService(studentRepository);
    }

    @Test
    void createStudent_success() {
        StudentRequest request = new StudentRequest("Alice");

        when(studentRepository.existsByNameIgnoreCase("Alice")).thenReturn(false);

        Student savedStudent = Student.builder().id(UUID.randomUUID().toString()).name("Alice").build();
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        StudentResponse response = studentService.createStudent(request);

        assertEquals("Alice", response.getName());
        assertNotNull(response.getId());

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(captor.capture());
        assertEquals("Alice", captor.getValue().getName());
    }

    @Test
    void createStudent_duplicateName_throwsException() {
        StudentRequest request = new StudentRequest("Bob");

        when(studentRepository.existsByNameIgnoreCase("Bob")).thenReturn(true);

        assertThrows(DuplicateNameException.class, () -> studentService.createStudent(request));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void searchStudents_returnsPagedResponse() {
        UUID schoolId = UUID.randomUUID();
        String nameFilter = "Al";
        Pageable pageable = PageRequest.of(0, 10);

        Student student = Student.builder().id(UUID.randomUUID().toString()).name("Alice").build();
        Page<Student> studentPage = new PageImpl<>(List.of(student));

        when(studentRepository.findBySchoolIdAndNameIgnoreCaseContaining(schoolId.toString(), nameFilter, pageable))
                .thenReturn(studentPage);

        PagedResponse<StudentResponse> response = studentService.searchStudents(schoolId, nameFilter, pageable);

        assertEquals(1, response.getContent().size());
        assertEquals("Alice", response.getContent().getFirst().getName());
    }

    @Test
    void getStudent_existingId_returnsStudent() {
        UUID id = UUID.randomUUID();
        Student student = Student.builder().id(id.toString()).name("Charlie").build();

        when(studentRepository.findById(id.toString())).thenReturn(Optional.of(student));

        StudentResponse response = studentService.getStudent(id);

        assertEquals("Charlie", response.getName());
        assertEquals(id.toString(), response.getId());
    }

    @Test
    void getStudent_nonExistingId_throwsNotFound() {
        UUID id = UUID.randomUUID();

        when(studentRepository.findById(id.toString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studentService.getStudent(id));
    }

    @Test
    void updateStudent_success() {
        UUID id = UUID.randomUUID();
        Student existingStudent = Student.builder().id(id.toString()).name("Old Name").build();

        StudentRequest request = new StudentRequest("New Name");

        when(studentRepository.findById(id.toString())).thenReturn(Optional.of(existingStudent));
        when(studentRepository.existsByNameIgnoreCase("New Name")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        StudentResponse response = studentService.updateStudent(id, request);

        assertEquals("New Name", response.getName());
        verify(studentRepository).save(existingStudent);
    }

    @Test
    void updateStudent_notFound() {
        UUID id = UUID.randomUUID();
        StudentRequest request = new StudentRequest("New Name");

        when(studentRepository.findById(id.toString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studentService.updateStudent(id, request));
    }

    @Test
    void updateStudent_duplicateName_throwsException() {
        UUID id = UUID.randomUUID();
        Student existingStudent = Student.builder().id(id.toString()).name("Old Name").build();

        StudentRequest request = new StudentRequest("Existing Name");

        when(studentRepository.findById(id.toString())).thenReturn(Optional.of(existingStudent));
        when(studentRepository.existsByNameIgnoreCase("Existing Name")).thenReturn(true);

        assertThrows(DuplicateNameException.class, () -> studentService.updateStudent(id, request));
    }

    @Test
    void deleteStudent_callsRepository() {
        UUID id = UUID.randomUUID();

        studentService.deleteStudent(id);

        verify(studentRepository).deleteById(id.toString());
    }
}