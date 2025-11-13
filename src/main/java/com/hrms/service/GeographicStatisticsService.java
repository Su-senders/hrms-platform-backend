package com.hrms.service;

import com.hrms.dto.GeographicStatisticsDTO;
import com.hrms.entity.Personnel;
import com.hrms.repository.ArrondissementRepository;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service de statistiques géographiques
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GeographicStatisticsService {

    private final RegionRepository regionRepository;
    private final DepartmentRepository departmentRepository;
    private final ArrondissementRepository arrondissementRepository;
    private final PersonnelRepository personnelRepository;

    /**
     * Statistiques globales géographiques
     */
    public GeographicStatisticsDTO getGlobalStatistics() {
        log.info("Calcul des statistiques géographiques globales");

        GeographicStatisticsDTO.GeographicStatisticsDTOBuilder builder = GeographicStatisticsDTO.builder();

        // Compteurs de base
        builder.totalRegions(regionRepository.count())
                .totalDepartments(departmentRepository.count())
                .totalArrondissements(arrondissementRepository.count())
                .activeRegions((long) regionRepository.findByActiveTrue().size())
                .activeDepartments((long) departmentRepository.findByActiveTrue().size())
                .activeArrondissements((long) arrondissementRepository.findByActiveTrue().size());

        // Répartition du personnel par région (via requête optimisée)
        Map<String, Long> personnelByRegion = new HashMap<>();
        List<Object[]> regionCounts = personnelRepository.countByRegionOrigine();
        for (Object[] result : regionCounts) {
            String regionName = (String) result[0];
            Long count = (Long) result[1];
            if (regionName != null) {
                personnelByRegion.put(regionName, count);
            }
        }
        builder.personnelByRegion(personnelByRegion);

        // Répartition du personnel par département (via requête optimisée)
        Map<String, Long> personnelByDepartment = new HashMap<>();
        List<Object[]> departmentCounts = personnelRepository.countByDepartmentOrigine();
        for (Object[] result : departmentCounts) {
            String departmentName = (String) result[0];
            Long count = (Long) result[1];
            if (departmentName != null) {
                personnelByDepartment.put(departmentName, count);
            }
        }
        builder.personnelByDepartment(personnelByDepartment);

        // Répartition du personnel par arrondissement (via requête optimisée)
        Map<String, Long> personnelByArrondissement = new HashMap<>();
        List<Object[]> arrondissementCounts = personnelRepository.countByArrondissementOrigine();
        for (Object[] result : arrondissementCounts) {
            String arrondissementName = (String) result[0];
            Long count = (Long) result[1];
            if (arrondissementName != null) {
                personnelByArrondissement.put(arrondissementName, count);
            }
        }
        builder.personnelByArrondissement(personnelByArrondissement);

        return builder.build();
    }

    /**
     * Statistiques par région
     */
    public GeographicStatisticsDTO.RegionStatisticsDTO getRegionStatistics(Long regionId) {
        log.info("Calcul des statistiques pour la région ID: {}", regionId);

        com.hrms.entity.Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new com.hrms.exception.ResourceNotFoundException("Region", "id", regionId));

        // Compter les départements et arrondissements
        Long nombreDepartements = departmentRepository.countByRegionId(regionId);
        Long nombreArrondissements = arrondissementRepository.countByRegionId(regionId);

        // Compter le personnel originaire de cette région (via requête optimisée)
        long personnelCount = personnelRepository.findByRegionOrigineId(regionId, 
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

        return GeographicStatisticsDTO.RegionStatisticsDTO.builder()
                .regionId(regionId)
                .regionName(region.getName())
                .regionCode(region.getCode())
                .nombreDepartements(nombreDepartements)
                .nombreArrondissements(nombreArrondissements)
                .nombrePersonnel(personnelCount)
                .build();
    }

    /**
     * Statistiques par département
     */
    public GeographicStatisticsDTO.DepartmentStatisticsDTO getDepartmentStatistics(Long departmentId) {
        log.info("Calcul des statistiques pour le département ID: {}", departmentId);

        com.hrms.entity.Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new com.hrms.exception.ResourceNotFoundException("Department", "id", departmentId));

        // Compter les arrondissements
        Long nombreArrondissements = arrondissementRepository.countByDepartmentId(departmentId);

        // Compter le personnel originaire de ce département (via requête optimisée)
        long personnelCount = personnelRepository.findByDepartmentOrigineId(departmentId, 
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

        return GeographicStatisticsDTO.DepartmentStatisticsDTO.builder()
                .departmentId(departmentId)
                .departmentName(department.getName())
                .departmentCode(department.getCode())
                .regionId(department.getRegion() != null ? department.getRegion().getId() : null)
                .regionName(department.getRegion() != null ? department.getRegion().getName() : null)
                .nombreArrondissements(nombreArrondissements)
                .nombrePersonnel(personnelCount)
                .build();
    }

    /**
     * Statistiques par arrondissement
     */
    public GeographicStatisticsDTO.ArrondissementStatisticsDTO getArrondissementStatistics(Long arrondissementId) {
        log.info("Calcul des statistiques pour l'arrondissement ID: {}", arrondissementId);

        com.hrms.entity.Arrondissement arrondissement = arrondissementRepository.findById(arrondissementId)
                .orElseThrow(() -> new com.hrms.exception.ResourceNotFoundException("Arrondissement", "id", arrondissementId));

        // Compter le personnel originaire de cet arrondissement (via requête optimisée)
        long personnelCount = personnelRepository.findByArrondissementOrigineId(arrondissementId, 
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

        return GeographicStatisticsDTO.ArrondissementStatisticsDTO.builder()
                .arrondissementId(arrondissementId)
                .arrondissementName(arrondissement.getName())
                .arrondissementCode(arrondissement.getCode())
                .departmentId(arrondissement.getDepartment() != null ? arrondissement.getDepartment().getId() : null)
                .departmentName(arrondissement.getDepartment() != null ? arrondissement.getDepartment().getName() : null)
                .regionId(arrondissement.getDepartment() != null && arrondissement.getDepartment().getRegion() != null 
                        ? arrondissement.getDepartment().getRegion().getId() : null)
                .regionName(arrondissement.getDepartment() != null && arrondissement.getDepartment().getRegion() != null 
                        ? arrondissement.getDepartment().getRegion().getName() : null)
                .nombrePersonnel(personnelCount)
                .build();
    }
}

