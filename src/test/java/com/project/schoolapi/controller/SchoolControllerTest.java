package com.project.schoolapi.controller;

import com.project.schoolapi.dto.PagedResponse;
import com.project.schoolapi.dto.School;
import com.project.schoolapi.dto.SchoolDetail;
import com.project.schoolapi.service.SchoolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
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

class SchoolControllerTest {

    private SchoolService schoolService;
    private SchoolController schoolController;

    @BeforeEach
    void setUp() {
        schoolService = mock(SchoolService.class);
        schoolController = new SchoolController(schoolService);
    }

    @Test
    void createSchool_success() {
        School request = new School();
        request.setName("Test School");
        request.setCapacity(100);
        School response = new School("id123", "Test School", 100);

        when(schoolService.createSchool(request)).thenReturn(response);

        ResponseEntity<School> result = schoolController.createSchool(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);

        verify(schoolService).createSchool(request);
    }

    @Test
    void searchSchools_success() {
        School schoolResponse = new School("id123", "Test School", 100);
        PagedResponse<School> pagedResponse = PagedResponse.fromPage(
                new PageImpl<>(List.of(schoolResponse))
        );

        when(schoolService.searchSchools(anyString(), any(Pageable.class))).thenReturn(pagedResponse);

        ResponseEntity<PagedResponse<School>> result = schoolController.searchSchools(0, 10, "Test", "name");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(pagedResponse);

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(schoolService).searchSchools(nameCaptor.capture(), pageableCaptor.capture());

        assertThat(nameCaptor.getValue()).isEqualTo("Test");
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(0);
    }

    @Test
    void getSchool_success() {
        UUID schoolId = UUID.randomUUID();
        SchoolDetail response = new SchoolDetail("id123", "Test School", 100, Collections.emptyList());

        when(schoolService.getSchool(schoolId)).thenReturn(response);

        ResponseEntity<SchoolDetail> result = schoolController.getSchool(schoolId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);

        verify(schoolService).getSchool(schoolId);
    }

    @Test
    void deleteSchool_success() {
        UUID schoolId = UUID.randomUUID();

        ResponseEntity<Void> result = schoolController.deleteSchool(schoolId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();

        verify(schoolService).deleteSchool(schoolId);
    }

    @Test
    void createSchool_serviceThrowsException_propagates() {
        School request = new School();
        request.setName("Test School");
        request.setCapacity(100);
        when(schoolService.createSchool(request)).thenThrow(new RuntimeException("Error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> schoolController.createSchool(request));
        assertThat(ex.getMessage()).isEqualTo("Error");
    }

    @Test
    void searchSchools_serviceThrowsException_propagates() {
        when(schoolService.searchSchools(anyString(), any(Pageable.class)))
                .thenThrow(new RuntimeException("Error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                schoolController.searchSchools(0, 10, "Test", "name"));
        assertThat(ex.getMessage()).isEqualTo("Error");
    }

    @Test
    void getSchool_serviceThrowsException_propagates() {
        UUID schoolId = UUID.randomUUID();
        when(schoolService.getSchool(schoolId)).thenThrow(new RuntimeException("Error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> schoolController.getSchool(schoolId));
        assertThat(ex.getMessage()).isEqualTo("Error");
    }

    @Test
    void deleteSchool_serviceThrowsException_propagates() {
        UUID schoolId = UUID.randomUUID();
        doThrow(new RuntimeException("Error")).when(schoolService).deleteSchool(schoolId);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> schoolController.deleteSchool(schoolId));
        assertThat(ex.getMessage()).isEqualTo("Error");
    }
}