package com.project.schoolapi.repository;

import com.project.schoolapi.model.School;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SchoolRepositoryTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Test
    @DisplayName("Should save and retrieve school")
    void saveAndFind_success() {

        School school = schoolRepository.save(
                School.builder()
                        .name("Test School")
                        .capacity(100)
                        .build()
        );

        assertThat(school.getId()).isNotNull();

        School found = schoolRepository.findById(school.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("Test School");
        assertThat(found.getCapacity()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should find schools by name ignoring case with pagination")
    void findByNameIgnoreCaseContaining_success() {

        schoolRepository.save(School.builder().name("Springfield High").capacity(50).build());
        schoolRepository.save(School.builder().name("springfield Elementary").capacity(30).build());
        schoolRepository.save(School.builder().name("Another School").capacity(20).build());

        PageRequest pageable = PageRequest.of(0, 10);

        Page<School> result =
                schoolRepository.findByNameIgnoreCaseContaining("SPRINGFIELD", pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(School::getName)
                .containsExactlyInAnyOrder("Springfield High", "springfield Elementary");
    }

    @Test
    @DisplayName("Should return empty page when no schools match")
    void findByNameIgnoreCaseContaining_noMatches() {

        schoolRepository.save(School.builder().name("Alpha School").capacity(50).build());

        Page<School> result =
                schoolRepository.findByNameIgnoreCaseContaining("Beta", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Should check existence by name ignoring case")
    void existsByNameIgnoreCase_success() {

        schoolRepository.save(
                School.builder()
                        .name("Unique School")
                        .capacity(100)
                        .build()
        );

        boolean existsUpperCase =
                schoolRepository.existsByNameIgnoreCase("UNIQUE SCHOOL");

        boolean existsLowerCase =
                schoolRepository.existsByNameIgnoreCase("unique school");

        boolean notExists =
                schoolRepository.existsByNameIgnoreCase("Other School");

        assertThat(existsUpperCase).isTrue();
        assertThat(existsLowerCase).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Pagination should limit results correctly")
    void pagination_shouldLimitResults() {

        for (int i = 1; i <= 15; i++) {
            schoolRepository.save(
                    School.builder()
                            .name("School " + i)
                            .capacity(50)
                            .build()
            );
        }

        Page<School> page =
                schoolRepository.findAll(PageRequest.of(0, 5));

        assertThat(page.getSize()).isEqualTo(5);
        assertThat(page.getContent()).hasSize(5);
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getTotalPages()).isEqualTo(3);
    }
}