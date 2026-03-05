package com.project.schoolapi.service;

import com.project.schoolapi.dto.PagedResponse;
import com.project.schoolapi.dto.SchoolDetailResponse;
import com.project.schoolapi.dto.SchoolRequest;
import com.project.schoolapi.dto.SchoolResponse;
import com.project.schoolapi.exception.DuplicateNameException;
import com.project.schoolapi.exception.NotFoundException;
import com.project.schoolapi.model.School;
import com.project.schoolapi.model.Student;
import com.project.schoolapi.repository.SchoolRepository;
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

class SchoolServiceTest {

    private SchoolRepository schoolRepository;
    private StudentRepository studentRepository;
    private SchoolService schoolService;

    @BeforeEach
    void setUp() {
        schoolRepository = mock(SchoolRepository.class);
        studentRepository = mock(StudentRepository.class);
        schoolService = new SchoolService(schoolRepository, studentRepository);
    }

    @Test
    void createSchool_success() {
        SchoolRequest request = new SchoolRequest("Springfield High", 500);

        when(schoolRepository.existsByNameIgnoreCase("Springfield High")).thenReturn(false);

        School savedSchool = School.builder().id(UUID.randomUUID().toString()).name("Springfield High").capacity(500).build();
        when(schoolRepository.save(any(School.class))).thenReturn(savedSchool);

        SchoolResponse response = schoolService.createSchool(request);

        assertEquals("Springfield High", response.getName());
        assertEquals(500, response.getCapacity());
        assertNotNull(response.getId());

        ArgumentCaptor<School> captor = ArgumentCaptor.forClass(School.class);
        verify(schoolRepository).save(captor.capture());
        assertEquals("Springfield High", captor.getValue().getName());
        assertEquals(500, captor.getValue().getCapacity());
    }

    @Test
    void createSchool_duplicateName_throwsException() {
        SchoolRequest request = new SchoolRequest("Springfield High", 500);

        when(schoolRepository.existsByNameIgnoreCase("Springfield High")).thenReturn(true);

        assertThrows(DuplicateNameException.class, () -> schoolService.createSchool(request));
        verify(schoolRepository, never()).save(any());
    }

    @Test
    void searchSchools_returnsPagedResponse() {
        String nameFilter = "High";
        Pageable pageable = PageRequest.of(0, 10);

        School school = School.builder().id(UUID.randomUUID().toString()).name("Springfield High").capacity(500).build();
        Page<School> schoolPage = new PageImpl<>(List.of(school));

        when(schoolRepository.findByNameIgnoreCaseContaining(nameFilter, pageable)).thenReturn(schoolPage);

        PagedResponse<SchoolResponse> response = schoolService.searchSchools(nameFilter, pageable);

        assertEquals(1, response.getContent().size());
        assertEquals("Springfield High", response.getContent().getFirst().getName());
    }

    @Test
    void getSchool_existingId_returnsDetail() {
        UUID id = UUID.randomUUID();
        School school = School.builder().id(id.toString()).name("Springfield High").capacity(500).build();
        Student student = Student.builder().id(UUID.randomUUID().toString()).name("Bart").build();

        when(schoolRepository.findById(id.toString())).thenReturn(Optional.of(school));
        when(studentRepository.findBySchoolId(id.toString())).thenReturn(List.of(student));

        SchoolDetailResponse response = schoolService.getSchool(id);

        assertEquals("Springfield High", response.getName());
        assertEquals(1, response.getStudents().size());
        assertEquals("Bart", response.getStudents().getFirst().getName());
    }

    @Test
    void getSchool_nonExistingId_throwsNotFound() {
        UUID id = UUID.randomUUID();

        when(schoolRepository.findById(id.toString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> schoolService.getSchool(id));
    }

    @Test
    void updateSchool_success() {
        UUID id = UUID.randomUUID();
        School existingSchool = School.builder().id(id.toString()).name("Old Name").capacity(100).build();

        SchoolRequest request = new SchoolRequest("New Name", 200);

        when(schoolRepository.findById(id.toString())).thenReturn(Optional.of(existingSchool));
        when(schoolRepository.existsByNameIgnoreCase("New Name")).thenReturn(false);
        when(schoolRepository.save(any(School.class))).thenAnswer(i -> i.getArgument(0));

        SchoolResponse response = schoolService.updateSchool(id, request);

        assertEquals("New Name", response.getName());
        assertEquals(200, response.getCapacity());
        verify(schoolRepository).save(existingSchool);
    }

    @Test
    void updateSchool_notFound() {
        UUID id = UUID.randomUUID();
        SchoolRequest request = new SchoolRequest("New Name", 200);

        when(schoolRepository.findById(id.toString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> schoolService.updateSchool(id, request));
    }

    @Test
    void updateSchool_duplicateName_throwsException() {
        UUID id = UUID.randomUUID();
        School existingSchool = School.builder().id(id.toString()).name("Old Name").capacity(100).build();

        SchoolRequest request = new SchoolRequest("Existing Name", 200);

        when(schoolRepository.findById(id.toString())).thenReturn(Optional.of(existingSchool));
        when(schoolRepository.existsByNameIgnoreCase("Existing Name")).thenReturn(true);

        assertThrows(DuplicateNameException.class, () -> schoolService.updateSchool(id, request));
    }

    @Test
    void deleteSchool_callsRepository() {
        UUID id = UUID.randomUUID();

        schoolService.deleteSchool(id);

        verify(schoolRepository).deleteById(id.toString());
    }
}