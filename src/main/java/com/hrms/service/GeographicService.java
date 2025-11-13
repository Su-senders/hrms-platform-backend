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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des données géographiques
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GeographicService {

    private final RegionRepository regionRepository;
    private final DepartmentRepository departmentRepository;
    private final ArrondissementRepository arrondissementRepository;
    private final PersonnelRepository personnelRepository;

    // ==================== RÉGIONS ====================

    /**
     * Obtenir toutes les régions actives
     */
    public List<RegionDTO> getAllRegions() {
        log.info("Récupération de toutes les régions actives");
        return regionRepository.findAllActiveOrderByName().stream()
                .map(this::toRegionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir une région par ID
     */
    public RegionDTO getRegionById(Long id) {
        log.info("Récupération de la région ID: {}", id);
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", id));
        return toRegionDTO(region);
    }

    /**
     * Obtenir une région par code
     */
    public RegionDTO getRegionByCode(String code) {
        log.info("Récupération de la région par code: {}", code);
        Region region = regionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "code", code));
        return toRegionDTO(region);
    }

    /**
     * Rechercher des régions par nom ou chef-lieu
     */
    public List<RegionDTO> searchRegions(String searchTerm) {
        log.info("Recherche de régions avec le terme: {}", searchTerm);
        return regionRepository.searchRegions(searchTerm).stream()
                .map(this::toRegionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les départements d'une région
     */
    public List<DepartmentDTO> getDepartmentsByRegion(Long regionId) {
        log.info("Récupération des départements de la région ID: {}", regionId);
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", regionId));
        return departmentRepository.findByRegionIdAndActiveTrue(regionId).stream()
                .map(this::toDepartmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les départements d'une région par code
     */
    public List<DepartmentDTO> getDepartmentsByRegionCode(String regionCode) {
        log.info("Récupération des départements de la région code: {}", regionCode);
        Region region = regionRepository.findByCode(regionCode)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "code", regionCode));
        return departmentRepository.findByRegionCodeAndActiveTrue(regionCode).stream()
                .map(this::toDepartmentDTO)
                .collect(Collectors.toList());
    }

    // ==================== DÉPARTEMENTS ====================

    /**
     * Obtenir tous les départements actifs
     */
    public List<DepartmentDTO> getAllDepartments() {
        log.info("Récupération de tous les départements actifs");
        return departmentRepository.findByActiveTrue().stream()
                .map(this::toDepartmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir un département par ID
     */
    public DepartmentDTO getDepartmentById(Long id) {
        log.info("Récupération du département ID: {}", id);
        Department department = departmentRepository.findByIdWithRegion(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        return toDepartmentDTO(department);
    }

    /**
     * Obtenir un département par code
     */
    public DepartmentDTO getDepartmentByCode(String code) {
        log.info("Récupération du département par code: {}", code);
        Department department = departmentRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "code", code));
        return toDepartmentDTO(department);
    }

    /**
     * Rechercher des départements par nom ou chef-lieu
     */
    public List<DepartmentDTO> searchDepartments(String searchTerm) {
        log.info("Recherche de départements avec le terme: {}", searchTerm);
        return departmentRepository.searchDepartments(searchTerm).stream()
                .map(this::toDepartmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les arrondissements d'un département
     */
    public List<ArrondissementDTO> getArrondissementsByDepartment(Long departmentId) {
        log.info("Récupération des arrondissements du département ID: {}", departmentId);
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));
        return arrondissementRepository.findByDepartmentIdAndActiveTrue(departmentId).stream()
                .map(this::toArrondissementDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les arrondissements d'un département par code
     */
    public List<ArrondissementDTO> getArrondissementsByDepartmentCode(String departmentCode) {
        log.info("Récupération des arrondissements du département code: {}", departmentCode);
        Department department = departmentRepository.findByCode(departmentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "code", departmentCode));
        return arrondissementRepository.findByDepartmentCodeAndActiveTrue(departmentCode).stream()
                .map(this::toArrondissementDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les départements par région ID
     */
    public List<DepartmentDTO> getDepartmentsByRegionId(Long regionId) {
        log.info("Récupération des départements de la région ID: {}", regionId);
        return departmentRepository.findByRegionIdAndActiveTrue(regionId).stream()
                .map(this::toDepartmentDTO)
                .collect(Collectors.toList());
    }

    // ==================== ARRONDISSEMENTS ====================

    /**
     * Obtenir tous les arrondissements actifs
     */
    public List<ArrondissementDTO> getAllArrondissements() {
        log.info("Récupération de tous les arrondissements actifs");
        return arrondissementRepository.findByActiveTrue().stream()
                .map(this::toArrondissementDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir un arrondissement par ID
     */
    public ArrondissementDTO getArrondissementById(Long id) {
        log.info("Récupération de l'arrondissement ID: {}", id);
        Arrondissement arrondissement = arrondissementRepository.findByIdWithDepartmentAndRegion(id)
                .orElseThrow(() -> new ResourceNotFoundException("Arrondissement", "id", id));
        return toArrondissementDTO(arrondissement);
    }

    /**
     * Obtenir un arrondissement par code
     */
    public ArrondissementDTO getArrondissementByCode(String code) {
        log.info("Récupération de l'arrondissement par code: {}", code);
        Arrondissement arrondissement = arrondissementRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Arrondissement", "code", code));
        return toArrondissementDTO(arrondissement);
    }

    /**
     * Rechercher des arrondissements par nom ou chef-lieu
     */
    public List<ArrondissementDTO> searchArrondissements(String searchTerm) {
        log.info("Recherche d'arrondissements avec le terme: {}", searchTerm);
        return arrondissementRepository.searchArrondissements(searchTerm).stream()
                .map(this::toArrondissementDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les arrondissements par région ID
     */
    public List<ArrondissementDTO> getArrondissementsByRegionId(Long regionId) {
        log.info("Récupération des arrondissements de la région ID: {}", regionId);
        return arrondissementRepository.findByRegionIdAndActiveTrue(regionId).stream()
                .map(this::toArrondissementDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les arrondissements par département ID
     */
    public List<ArrondissementDTO> getArrondissementsByDepartmentId(Long departmentId) {
        log.info("Récupération des arrondissements du département ID: {}", departmentId);
        return arrondissementRepository.findByDepartmentIdAndActiveTrue(departmentId).stream()
                .map(this::toArrondissementDTO)
                .collect(Collectors.toList());
    }

    // ==================== MÉTHODES DE CONVERSION ====================

    /**
     * Convertir Region en RegionDTO
     */
    private RegionDTO toRegionDTO(Region region) {
        RegionDTO.RegionDTOBuilder builder = RegionDTO.builder()
                .id(region.getId())
                .code(region.getCode())
                .name(region.getName())
                .chefLieu(region.getChefLieu())
                .superficieKm2(region.getSuperficieKm2())
                .population(region.getPopulation())
                .description(region.getDescription())
                .active(region.getActive());

        // Ajouter les statistiques
        Long nombreDepartements = departmentRepository.countByRegionId(region.getId());
        Long nombreArrondissements = arrondissementRepository.countByRegionId(region.getId());
        builder.nombreDepartements(nombreDepartements);
        builder.nombreArrondissements(nombreArrondissements);

        // Ajouter les informations sur le gouvernorat si disponible
        if (region.getGouvernorat() != null) {
            builder.gouvernoratId(region.getGouvernorat().getId())
                    .gouvernoratCode(region.getGouvernorat().getCode())
                    .gouvernoratName(region.getGouvernorat().getName());
        }

        return builder.build();
    }

    /**
     * Convertir Department en DepartmentDTO
     */
    private DepartmentDTO toDepartmentDTO(Department department) {
        DepartmentDTO.DepartmentDTOBuilder builder = DepartmentDTO.builder()
                .id(department.getId())
                .code(department.getCode())
                .name(department.getName())
                .chefLieu(department.getChefLieu())
                .superficieKm2(department.getSuperficieKm2())
                .population(department.getPopulation())
                .description(department.getDescription())
                .active(department.getActive());

        // Ajouter les informations sur la région
        if (department.getRegion() != null) {
            builder.regionId(department.getRegion().getId())
                    .regionCode(department.getRegion().getCode())
                    .regionName(department.getRegion().getName());
        }

        // Ajouter les statistiques
        Long nombreArrondissements = arrondissementRepository.countByDepartmentId(department.getId());
        builder.nombreArrondissements(nombreArrondissements);

        // Ajouter les informations sur la préfecture si disponible
        if (department.getPrefecture() != null) {
            builder.prefectureId(department.getPrefecture().getId())
                    .prefectureCode(department.getPrefecture().getCode())
                    .prefectureName(department.getPrefecture().getName());
        }

        return builder.build();
    }

    /**
     * Convertir Arrondissement en ArrondissementDTO
     */
    private ArrondissementDTO toArrondissementDTO(Arrondissement arrondissement) {
        ArrondissementDTO.ArrondissementDTOBuilder builder = ArrondissementDTO.builder()
                .id(arrondissement.getId())
                .code(arrondissement.getCode())
                .name(arrondissement.getName())
                .chefLieu(arrondissement.getChefLieu())
                .type(arrondissement.getType() != null ? arrondissement.getType().name() : null)
                .superficieKm2(arrondissement.getSuperficieKm2())
                .population(arrondissement.getPopulation())
                .description(arrondissement.getDescription())
                .active(arrondissement.getActive());

        // Ajouter les informations sur le département
        if (arrondissement.getDepartment() != null) {
            builder.departmentId(arrondissement.getDepartment().getId())
                    .departmentCode(arrondissement.getDepartment().getCode())
                    .departmentName(arrondissement.getDepartment().getName());

            // Ajouter les informations sur la région (via département)
            if (arrondissement.getDepartment().getRegion() != null) {
                builder.regionId(arrondissement.getDepartment().getRegion().getId())
                        .regionCode(arrondissement.getDepartment().getRegion().getCode())
                        .regionName(arrondissement.getDepartment().getRegion().getName());
            }
        }

        // Ajouter les informations sur la sous-préfecture si disponible
        if (arrondissement.getSousPrefecture() != null) {
            builder.sousPrefectureId(arrondissement.getSousPrefecture().getId())
                    .sousPrefectureCode(arrondissement.getSousPrefecture().getCode())
                    .sousPrefectureName(arrondissement.getSousPrefecture().getName());
        }

        return builder.build();
    }
}

