package com.hrms.service;

import com.hrms.dto.ArrondissementDTO;
import com.hrms.dto.DepartmentDTO;
import com.hrms.dto.RegionDTO;
import com.hrms.entity.Arrondissement;
import com.hrms.entity.Department;
import com.hrms.entity.Region;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.repository.ArrondissementRepository;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour GeographicService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service géographique")
class GeographicServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ArrondissementRepository arrondissementRepository;

    @Mock
    private PersonnelRepository personnelRepository;

    @InjectMocks
    private GeographicService geographicService;

    private Region region;
    private Department department;
    private Arrondissement arrondissement;

    @BeforeEach
    void setUp() {
        // Créer une région de test
        region = Region.builder()
                .id(1L)
                .code("CE")
                .name("Centre")
                .chefLieu("Yaoundé")
                .active(true)
                .build();

        // Créer un département de test
        department = Department.builder()
                .id(1L)
                .code("CE-MFOU")
                .name("Mfoundi")
                .chefLieu("Yaoundé")
                .region(region)
                .active(true)
                .build();

        // Créer un arrondissement de test
        arrondissement = Arrondissement.builder()
                .id(1L)
                .code("CE-MFOU-YDE1")
                .name("Yaoundé 1er")
                .chefLieu("Yaoundé")
                .department(department)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Devrait retourner toutes les régions actives")
    void shouldGetAllRegions() {
        // Given
        List<Region> regions = Arrays.asList(region);
        when(regionRepository.findAllActiveOrderByName()).thenReturn(regions);
        when(departmentRepository.countByRegionId(anyLong())).thenReturn(5L);
        when(arrondissementRepository.countByRegionId(anyLong())).thenReturn(10L);

        // When
        List<RegionDTO> result = geographicService.getAllRegions();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("CE");
        assertThat(result.get(0).getName()).isEqualTo("Centre");
        verify(regionRepository).findAllActiveOrderByName();
    }

    @Test
    @DisplayName("Devrait retourner une région par ID")
    void shouldGetRegionById() {
        // Given
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(departmentRepository.countByRegionId(1L)).thenReturn(5L);
        when(arrondissementRepository.countByRegionId(1L)).thenReturn(10L);

        // When
        RegionDTO result = geographicService.getRegionById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("CE");
        assertThat(result.getName()).isEqualTo("Centre");
        verify(regionRepository).findById(1L);
    }

    @Test
    @DisplayName("Devrait lever une exception si la région n'existe pas")
    void shouldThrowExceptionWhenRegionNotFound() {
        // Given
        when(regionRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> geographicService.getRegionById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Region");
        verify(regionRepository).findById(999L);
    }

    @Test
    @DisplayName("Devrait retourner une région par code")
    void shouldGetRegionByCode() {
        // Given
        when(regionRepository.findByCode("CE")).thenReturn(Optional.of(region));
        when(departmentRepository.countByRegionId(1L)).thenReturn(5L);
        when(arrondissementRepository.countByRegionId(1L)).thenReturn(10L);

        // When
        RegionDTO result = geographicService.getRegionByCode("CE");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("CE");
        verify(regionRepository).findByCode("CE");
    }

    @Test
    @DisplayName("Devrait rechercher des régions par terme")
    void shouldSearchRegions() {
        // Given
        List<Region> regions = Arrays.asList(region);
        when(regionRepository.searchRegions("Centre")).thenReturn(regions);
        when(departmentRepository.countByRegionId(anyLong())).thenReturn(5L);
        when(arrondissementRepository.countByRegionId(anyLong())).thenReturn(10L);

        // When
        List<RegionDTO> result = geographicService.searchRegions("Centre");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(regionRepository).searchRegions("Centre");
    }

    @Test
    @DisplayName("Devrait retourner les départements d'une région")
    void shouldGetDepartmentsByRegion() {
        // Given
        List<Department> departments = Arrays.asList(department);
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(departmentRepository.findByRegionIdAndActiveTrue(1L)).thenReturn(departments);
        when(arrondissementRepository.countByDepartmentId(anyLong())).thenReturn(3L);

        // When
        List<DepartmentDTO> result = geographicService.getDepartmentsByRegion(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("CE-MFOU");
        verify(regionRepository).findById(1L);
        verify(departmentRepository).findByRegionIdAndActiveTrue(1L);
    }

    @Test
    @DisplayName("Devrait retourner les arrondissements d'un département")
    void shouldGetArrondissementsByDepartment() {
        // Given
        List<Arrondissement> arrondissements = Arrays.asList(arrondissement);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(arrondissementRepository.findByDepartmentIdAndActiveTrue(1L)).thenReturn(arrondissements);

        // When
        List<ArrondissementDTO> result = geographicService.getArrondissementsByDepartment(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("CE-MFOU-YDE1");
        verify(departmentRepository).findById(1L);
        verify(arrondissementRepository).findByDepartmentIdAndActiveTrue(1L);
    }

    @Test
    @DisplayName("Devrait retourner tous les départements actifs")
    void shouldGetAllDepartments() {
        // Given
        List<Department> departments = Arrays.asList(department);
        when(departmentRepository.findByActiveTrue()).thenReturn(departments);
        when(arrondissementRepository.countByDepartmentId(anyLong())).thenReturn(3L);

        // When
        List<DepartmentDTO> result = geographicService.getAllDepartments();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(departmentRepository).findByActiveTrue();
    }

    @Test
    @DisplayName("Devrait retourner un département par ID")
    void shouldGetDepartmentById() {
        // Given
        when(departmentRepository.findByIdWithRegion(1L)).thenReturn(Optional.of(department));
        when(arrondissementRepository.countByDepartmentId(1L)).thenReturn(3L);

        // When
        DepartmentDTO result = geographicService.getDepartmentById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("CE-MFOU");
        verify(departmentRepository).findByIdWithRegion(1L);
    }

    @Test
    @DisplayName("Devrait retourner tous les arrondissements actifs")
    void shouldGetAllArrondissements() {
        // Given
        List<Arrondissement> arrondissements = Arrays.asList(arrondissement);
        when(arrondissementRepository.findByActiveTrue()).thenReturn(arrondissements);

        // When
        List<ArrondissementDTO> result = geographicService.getAllArrondissements();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(arrondissementRepository).findByActiveTrue();
    }

    @Test
    @DisplayName("Devrait retourner un arrondissement par ID")
    void shouldGetArrondissementById() {
        // Given
        when(arrondissementRepository.findByIdWithDepartmentAndRegion(1L))
                .thenReturn(Optional.of(arrondissement));

        // When
        ArrondissementDTO result = geographicService.getArrondissementById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("CE-MFOU-YDE1");
        verify(arrondissementRepository).findByIdWithDepartmentAndRegion(1L);
    }
}

