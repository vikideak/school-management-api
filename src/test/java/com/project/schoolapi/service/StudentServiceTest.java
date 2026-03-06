package com.project.schoolapi.service;

import com.project.schoolapi.dto.PagedResponse;
import com.project.schoolapi.dto.Student;
import com.project.schoolapi.exception.DuplicateNameException;
import com.project.schoolapi.exception.NotFoundException;
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
        Student request = new Student();
        request.setName("Alice");

        when(studentRepository.existsByNameIgnoreCase("Alice")).thenReturn(false);

        com.project.schoolapi.model.Student savedStudent = com.project.schoolapi.model.Student.builder().id(UUID.randomUUID().toString()).name("Alice").build();
        when(studentRepository.save(any(com.project.schoolapi.model.Student.class))).thenReturn(savedStudent);

        Student response = studentService.createStudent(request);

        assertEquals("Alice", response.getName());
        assertNotNull(response.getId());

        ArgumentCaptor<com.project.schoolapi.model.Student> captor = ArgumentCaptor.forClass(com.project.schoolapi.model.Student.class);
        verify(studentRepository).save(captor.capture());
        assertEquals("Alice", captor.getValue().getName());
    }

    @Test
    void createStudent_duplicateName_throwsException() {
        Student request = new Student();
        request.setName("Bob");

        when(studentRepository.existsByNameIgnoreCase("Bob")).thenReturn(true);

        assertThrows(DuplicateNameException.class, () -> studentService.createStudent(request));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void searchStudents_returnsPagedResponse() {
        UUID schoolId = UUID.randomUUID();
        String nameFilter = "Al";
        Pageable pageable = PageRequest.of(0, 10);

        com.project.schoolapi.model.Student student = com.project.schoolapi.model.Student.builder().id(UUID.randomUUID().toString()).name("Alice").build();
        Page<com.project.schoolapi.model.Student> studentPage = new PageImpl<>(List.of(student));

        when(studentRepository.findBySchoolIdAndNameIgnoreCaseContaining(schoolId.toString(), nameFilter, pageable))
                .thenReturn(studentPage);

        PagedResponse<Student> response = studentService.searchStudents(schoolId, nameFilter, pageable);

        assertEquals(1, response.getContent().size());
        assertEquals("Alice", response.getContent().getFirst().getName());
    }

    @Test
    void getStudent_existingId_returnsStudent() {
        UUID id = UUID.randomUUID();
        com.project.schoolapi.model.Student student = com.project.schoolapi.model.Student.builder().id(id.toString()).name("Charlie").build();

        when(studentRepository.findById(id.toString())).thenReturn(Optional.of(student));

        Student response = studentService.getStudent(id);

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
        com.project.schoolapi.model.Student existingStudent = com.project.schoolapi.model.Student.builder().id(id.toString()).name("Old Name").build();

        Student request = new Student();
        request.setName("New Name");

        when(studentRepository.findById(id.toString())).thenReturn(Optional.of(existingStudent));
        when(studentRepository.existsByNameIgnoreCase("New Name")).thenReturn(false);
        when(studentRepository.save(any(com.project.schoolapi.model.Student.class))).thenAnswer(i -> i.getArgument(0));

        Student response = studentService.updateStudent(id, request);

        assertEquals("New Name", response.getName());
        verify(studentRepository).save(existingStudent);
    }

    @Test
    void updateStudent_notFound() {
        UUID id = UUID.randomUUID();
        Student request = new Student();
        request.setName("New Name");

        when(studentRepository.findById(id.toString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studentService.updateStudent(id, request));
    }

    @Test
    void updateStudent_duplicateName_throwsException() {
        UUID id = UUID.randomUUID();
        com.project.schoolapi.model.Student existingStudent = com.project.schoolapi.model.Student.builder().id(id.toString()).name("Old Name").build();

        Student request = new Student();
        request.setName("Existing Name");

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