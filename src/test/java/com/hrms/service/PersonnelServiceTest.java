package com.hrms.service;

import com.hrms.dto.PersonnelCreateDTO;
import com.hrms.dto.PersonnelDTO;
import com.hrms.dto.PersonnelUpdateDTO;
import com.hrms.entity.*;
import com.hrms.exception.BusinessException;
import com.hrms.exception.DuplicateResourceException;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.PersonnelMapper;
import com.hrms.repository.*;
import com.hrms.util.AuditUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour PersonnelService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service personnel")
class PersonnelServiceTest {

    @Mock
    private PersonnelRepository personnelRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private AdministrativeStructureRepository structureRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ArrondissementRepository arrondissementRepository;

    @Mock
    private PersonnelMapper personnelMapper;

    @Mock
    private AuditUtil auditUtil;

    @Mock
    private GeographicValidationService geographicValidationService;

    @Mock
    private DateValidationService dateValidationService;

    @Mock
    private SeniorityCalculationService seniorityCalculationService;

    @Mock
    private AssignmentHistoryService assignmentHistoryService;

    @InjectMocks
    private PersonnelService personnelService;

    private PersonnelCreateDTO createDTO;
    private Personnel personnel;
    private PersonnelDTO personnelDTO;
    private AdministrativeStructure structure;
    private Region region;
    private Department department;
    private Arrondissement arrondissement;

    @BeforeEach
    void setUp() {
        // Créer une structure de test
        structure = AdministrativeStructure.builder()
                .id(1L)
                .code("MINAT")
                .name("Ministère de l'Administration Territoriale")
                .build();

        // Créer des entités géographiques
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

        // Créer un DTO de création
        createDTO = PersonnelCreateDTO.builder()
                .matricule("MAT001")
                .firstName("Jean")
                .lastName("Dupont")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .cniNumber("123456789")
                .structureId(1L)
                .regionOrigineId(1L)
                .departmentOrigineId(1L)
                .arrondissementOrigineId(1L)
                .build();

        // Créer une entité Personnel
        personnel = Personnel.builder()
                .id(1L)
                .matricule("MAT001")
                .firstName("Jean")
                .lastName("Dupont")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .cniNumber("123456789")
                .structure(structure)
                .regionOrigine(region)
                .departmentOrigine(department)
                .arrondissementOrigine(arrondissement)
                .build();

        // Créer un DTO
        personnelDTO = PersonnelDTO.builder()
                .id(1L)
                .matricule("MAT001")
                .firstName("Jean")
                .lastName("Dupont")
                .build();
    }

    @Test
    @DisplayName("Devrait créer un nouveau personnel")
    void shouldCreatePersonnel() {
        // Given
        when(personnelRepository.findByMatricule("MAT001")).thenReturn(Optional.empty());
        when(personnelRepository.findByCniNumber("123456789")).thenReturn(Optional.empty());
        when(personnelRepository.findByFirstNameAndLastNameAndDateOfBirth(
                "Jean", "Dupont", LocalDate.of(1980, 1, 1))).thenReturn(Optional.empty());
        when(personnelMapper.toEntity(createDTO)).thenReturn(personnel);
        when(structureRepository.findById(1L)).thenReturn(Optional.of(structure));
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(arrondissementRepository.findById(1L)).thenReturn(Optional.of(arrondissement));
        when(personnelRepository.save(any(Personnel.class))).thenReturn(personnel);
        when(personnelMapper.toDTO(any(Personnel.class))).thenReturn(personnelDTO);
        when(auditUtil.getCurrentUser()).thenReturn("test-user");

        doNothing().when(geographicValidationService).validateGeographicCoherence(anyLong(), anyLong(), anyLong());
        doNothing().when(dateValidationService).validatePersonnelDates(any(Personnel.class));
        doNothing().when(seniorityCalculationService).calculateAndSetSeniority(any(Personnel.class));
        doNothing().when(assignmentHistoryService).recordAssignment(any(Personnel.class), any(), any());

        // When
        PersonnelDTO result = personnelService.createPersonnel(createDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMatricule()).isEqualTo("MAT001");
        verify(personnelRepository).save(any(Personnel.class));
        verify(geographicValidationService).validateGeographicCoherence(1L, 1L, 1L);
    }

    @Test
    @DisplayName("Devrait lever une exception si le matricule existe déjà")
    void shouldThrowExceptionWhenMatriculeExists() {
        // Given
        when(personnelRepository.findByMatricule("MAT001")).thenReturn(Optional.of(personnel));

        // When / Then
        assertThatThrownBy(() -> personnelService.createPersonnel(createDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("matricule");
    }

    @Test
    @DisplayName("Devrait retourner un personnel par ID")
    void shouldGetPersonnelById() {
        // Given
        when(personnelRepository.findById(1L)).thenReturn(Optional.of(personnel));
        when(personnelMapper.toDTO(personnel)).thenReturn(personnelDTO);

        // When
        PersonnelDTO result = personnelService.getPersonnelById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(personnelRepository).findById(1L);
    }

    @Test
    @DisplayName("Devrait lever une exception si le personnel n'existe pas")
    void shouldThrowExceptionWhenPersonnelNotFound() {
        // Given
        when(personnelRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> personnelService.getPersonnelById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Personnel");
    }

    @Test
    @DisplayName("Devrait retourner un personnel par matricule")
    void shouldGetPersonnelByMatricule() {
        // Given
        when(personnelRepository.findByMatricule("MAT001")).thenReturn(Optional.of(personnel));
        when(personnelMapper.toDTO(personnel)).thenReturn(personnelDTO);

        // When
        PersonnelDTO result = personnelService.getPersonnelByMatricule("MAT001");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMatricule()).isEqualTo("MAT001");
        verify(personnelRepository).findByMatricule("MAT001");
    }

    @Test
    @DisplayName("Devrait retourner tous les personnels paginés")
    void shouldGetAllPersonnel() {
        // Given
        Page<Personnel> personnelPage = new PageImpl<>(Arrays.asList(personnel));
        when(personnelRepository.findByDeletedFalse(any(Pageable.class))).thenReturn(personnelPage);
        when(personnelMapper.toDTO(any(Personnel.class))).thenReturn(personnelDTO);

        // When
        Page<PersonnelDTO> result = personnelService.getAllPersonnel(Pageable.unpaged());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(personnelRepository).findByDeletedFalse(any(Pageable.class));
    }

    @Test
    @DisplayName("Devrait mettre à jour un personnel")
    void shouldUpdatePersonnel() {
        // Given
        PersonnelUpdateDTO updateDTO = PersonnelUpdateDTO.builder()
                .firstName("Jean-Pierre")
                .lastName("Dupont")
                .build();

        when(personnelRepository.findById(1L)).thenReturn(Optional.of(personnel));
        when(personnelRepository.save(any(Personnel.class))).thenReturn(personnel);
        when(personnelMapper.toDTO(any(Personnel.class))).thenReturn(personnelDTO);
        when(auditUtil.getCurrentUser()).thenReturn("test-user");

        doNothing().when(dateValidationService).validatePersonnelDates(any(Personnel.class));
        doNothing().when(seniorityCalculationService).calculateAndSetSeniority(any(Personnel.class));

        // When
        PersonnelDTO result = personnelService.updatePersonnel(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(personnelRepository).save(any(Personnel.class));
    }

    @Test
    @DisplayName("Devrait supprimer logiquement un personnel")
    void shouldDeletePersonnel() {
        // Given
        when(personnelRepository.findById(1L)).thenReturn(Optional.of(personnel));
        when(personnelRepository.save(any(Personnel.class))).thenReturn(personnel);
        when(auditUtil.getCurrentUser()).thenReturn("test-user");

        // When
        personnelService.deletePersonnel(1L);

        // Then
        verify(personnelRepository).save(any(Personnel.class));
        assertThat(personnel.getDeleted()).isTrue();
    }
}

