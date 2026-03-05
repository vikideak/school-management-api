package com.project.schoolapi.repository;

import com.project.schoolapi.model.School;
import com.project.schoolapi.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Test
    @DisplayName("Should find students by schoolId and name containing (case insensitive)")
    void findBySchoolIdAndNameIgnoreCaseContaining_success() {
        School school = schoolRepository.save(
                School.builder().name("Test School").capacity(100).build()
        );

        studentRepository.save(
                Student.builder().name("John").school(school).build()
        );

        studentRepository.save(
                Student.builder().name("Johnny").school(school).build()
        );

        Page<Student> result = studentRepository
                .findBySchoolIdAndNameIgnoreCaseContaining(
                        school.getId(),
                        "john",
                        PageRequest.of(0, 10)
                );

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void countBySchoolId_shouldReturnCorrectCount() {
        School school = schoolRepository.save(
                School.builder().name("Test School").capacity(100).build()
        );

        studentRepository.save(
                Student.builder().name("Alice").school(school).build()
        );

        int count = studentRepository.countBySchoolId(school.getId());

        assertThat(count).isEqualTo(1);
    }

    @Test
    void existsByNameIgnoreCase_shouldWork() {
        studentRepository.save(
                Student.builder().name("Bob").build()
        );

        boolean exists = studentRepository.existsByNameIgnoreCase("bob");

        assertThat(exists).isTrue();
    }
}