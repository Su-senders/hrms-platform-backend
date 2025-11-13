package com.hrms.service;

import com.hrms.entity.Arrondissement;
import com.hrms.entity.Department;
import com.hrms.entity.Region;
import com.hrms.exception.BusinessException;
import com.hrms.repository.ArrondissementRepository;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour GeographicValidationService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service de validation géographique")
class GeographicValidationServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ArrondissementRepository arrondissementRepository;

    @InjectMocks
    private GeographicValidationService validationService;

    private Region region;
    private Department department;
    private Arrondissement arrondissement;

    @BeforeEach
    void setUp() {
        region = Region.builder()
                .id(1L)
                .code("CE")
                .name("Centre")
                .build();

        department = Department.builder()
                .id(1L)
                .code("CE-MFOU")
                .name("Mfoundi")
                .region(region)
                .build();

        arrondissement = Arrondissement.builder()
                .id(1L)
                .code("CE-MFOU-YDE1")
                .name("Yaoundé 1er")
                .department(department)
                .build();
    }

    @Test
    @DisplayName("Devrait valider une cohérence géographique correcte")
    void shouldValidateCorrectGeographicCoherence() {
        // Given
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(arrondissementRepository.findById(1L)).thenReturn(Optional.of(arrondissement));

        // When / Then
        validationService.validateGeographicCoherence(1L, 1L, 1L);

        // Vérifications
        verify(regionRepository).findById(1L);
        verify(departmentRepository).findById(1L);
        verify(arrondissementRepository).findById(1L);
    }

    @Test
    @DisplayName("Devrait lever une exception si la région est manquante")
    void shouldThrowExceptionWhenRegionIsMissing() {
        // When / Then
        assertThatThrownBy(() -> validationService.validateGeographicCoherence(null, 1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("région est obligatoire");
    }

    @Test
    @DisplayName("Devrait lever une exception si la région n'existe pas")
    void shouldThrowExceptionWhenRegionNotFound() {
        // Given
        when(regionRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> validationService.validateGeographicCoherence(999L, null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Région non trouvée");
    }

    @Test
    @DisplayName("Devrait lever une exception si le département n'appartient pas à la région")
    void shouldThrowExceptionWhenDepartmentNotInRegion() {
        // Given
        Region otherRegion = Region.builder().id(2L).name("Littoral").build();
        Department wrongDepartment = Department.builder()
                .id(2L)
                .name("Wouri")
                .region(otherRegion)
                .build();

        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(wrongDepartment));

        // When / Then
        assertThatThrownBy(() -> validationService.validateGeographicCoherence(1L, 2L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("n'appartient pas à la région");
    }

    @Test
    @DisplayName("Devrait lever une exception si l'arrondissement n'appartient pas au département")
    void shouldThrowExceptionWhenArrondissementNotInDepartment() {
        // Given
        Department otherDepartment = Department.builder()
                .id(2L)
                .name("Lekié")
                .region(region)
                .build();
        Arrondissement wrongArrondissement = Arrondissement.builder()
                .id(2L)
                .name("Monatélé")
                .department(otherDepartment)
                .build();

        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(arrondissementRepository.findById(2L)).thenReturn(Optional.of(wrongArrondissement));

        // When / Then
        assertThatThrownBy(() -> validationService.validateGeographicCoherence(1L, 1L, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("n'appartient pas au département");
    }

    @Test
    @DisplayName("Devrait valider uniquement la région si département et arrondissement sont null")
    void shouldValidateOnlyRegion() {
        // Given
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));

        // When / Then
        validationService.validateGeographicCoherence(1L, null, null);

        verify(regionRepository).findById(1L);
        verify(departmentRepository, never()).findById(any());
        verify(arrondissementRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Devrait retourner true si le département appartient à la région")
    void shouldReturnTrueWhenDepartmentInRegion() {
        // Given
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        // When
        boolean result = validationService.isDepartmentInRegion(1L, 1L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Devrait retourner false si le département n'appartient pas à la région")
    void shouldReturnFalseWhenDepartmentNotInRegion() {
        // Given
        Region otherRegion = Region.builder().id(2L).name("Littoral").build();
        Department wrongDepartment = Department.builder()
                .id(2L)
                .region(otherRegion)
                .build();
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(wrongDepartment));

        // When
        boolean result = validationService.isDepartmentInRegion(2L, 1L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Devrait retourner l'ID de la région depuis un arrondissement")
    void shouldGetRegionIdFromArrondissement() {
        // Given
        when(arrondissementRepository.findById(1L)).thenReturn(Optional.of(arrondissement));

        // When
        Long result = validationService.getRegionIdFromArrondissement(1L);

        // Then
        assertThat(result).isEqualTo(1L);
    }
}

