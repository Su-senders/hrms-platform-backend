package com.hrms.repository;

import com.hrms.entity.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration pour RegionRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests du repository des régions")
class RegionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RegionRepository regionRepository;

    @Test
    @DisplayName("Devrait sauvegarder et retrouver une région")
    void shouldSaveAndFindRegion() {
        // Given
        Region region = Region.builder()
                .code("CE")
                .name("Centre")
                .chefLieu("Yaoundé")
                .active(true)
                .build();

        // When
        Region saved = regionRepository.save(region);
        entityManager.flush();
        entityManager.clear();

        Optional<Region> found = regionRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("CE");
        assertThat(found.get().getName()).isEqualTo("Centre");
    }

    @Test
    @DisplayName("Devrait trouver une région par code")
    void shouldFindRegionByCode() {
        // Given
        Region region = Region.builder()
                .code("CE")
                .name("Centre")
                .chefLieu("Yaoundé")
                .active(true)
                .build();

        entityManager.persistAndFlush(region);

        // When
        Optional<Region> found = regionRepository.findByCode("CE");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("CE");
    }

    @Test
    @DisplayName("Devrait retourner uniquement les régions actives")
    void shouldFindOnlyActiveRegions() {
        // Given
        Region activeRegion = Region.builder()
                .code("CE")
                .name("Centre")
                .chefLieu("Yaoundé")
                .active(true)
                .build();

        Region inactiveRegion = Region.builder()
                .code("LT")
                .name("Littoral")
                .chefLieu("Douala")
                .active(false)
                .build();

        entityManager.persistAndFlush(activeRegion);
        entityManager.persistAndFlush(inactiveRegion);

        // When
        List<Region> activeRegions = regionRepository.findByActiveTrue();

        // Then
        assertThat(activeRegions).hasSize(1);
        assertThat(activeRegions.get(0).getCode()).isEqualTo("CE");
    }

    @Test
    @DisplayName("Devrait vérifier l'existence d'une région par code")
    void shouldCheckExistenceByCode() {
        // Given
        Region region = Region.builder()
                .code("CE")
                .name("Centre")
                .chefLieu("Yaoundé")
                .active(true)
                .build();

        entityManager.persistAndFlush(region);

        // When
        boolean exists = regionRepository.existsByCode("CE");
        boolean notExists = regionRepository.existsByCode("XX");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Devrait rechercher des régions par nom ou chef-lieu")
    void shouldSearchRegions() {
        // Given
        Region region1 = Region.builder()
                .code("CE")
                .name("Centre")
                .chefLieu("Yaoundé")
                .active(true)
                .build();

        Region region2 = Region.builder()
                .code("LT")
                .name("Littoral")
                .chefLieu("Douala")
                .active(true)
                .build();

        entityManager.persistAndFlush(region1);
        entityManager.persistAndFlush(region2);

        // When
        List<Region> results = regionRepository.searchRegions("Yaoundé");

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCode()).isEqualTo("CE");
    }
}

