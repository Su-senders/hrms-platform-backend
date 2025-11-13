package com.hrms.service;

import com.hrms.entity.Arrondissement;
import com.hrms.entity.Department;
import com.hrms.entity.Region;
import com.hrms.exception.BusinessException;
import com.hrms.repository.ArrondissementRepository;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de validation de la cohérence des données géographiques
 * Assure que Région → Département → Arrondissement sont cohérents
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GeographicValidationService {

    private final RegionRepository regionRepository;
    private final DepartmentRepository departmentRepository;
    private final ArrondissementRepository arrondissementRepository;

    /**
     * Valide la cohérence entre une région, un département et un arrondissement
     *
     * @param regionId ID de la région
     * @param departmentId ID du département (nullable)
     * @param arrondissementId ID de l'arrondissement (nullable)
     * @throws BusinessException si les données ne sont pas cohérentes
     */
    public void validateGeographicCoherence(Long regionId, Long departmentId, Long arrondissementId) {
        if (regionId == null) {
            throw new BusinessException("La région est obligatoire");
        }

        Region region = regionRepository.findById(regionId)
            .orElseThrow(() -> new BusinessException("Région non trouvée avec l'ID: " + regionId));

        // Si département spécifié, vérifier qu'il appartient bien à la région
        if (departmentId != null) {
            Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new BusinessException("Département non trouvé avec l'ID: " + departmentId));

            if (!department.getRegion().getId().equals(regionId)) {
                throw new BusinessException(
                    String.format("Le département '%s' n'appartient pas à la région '%s'. " +
                        "Il appartient à la région '%s'.",
                        department.getName(),
                        region.getName(),
                        department.getRegion().getName()
                    )
                );
            }

            // Si arrondissement spécifié, vérifier qu'il appartient bien au département
            if (arrondissementId != null) {
                Arrondissement arrondissement = arrondissementRepository.findById(arrondissementId)
                    .orElseThrow(() -> new BusinessException("Arrondissement non trouvé avec l'ID: " + arrondissementId));

                if (!arrondissement.getDepartment().getId().equals(departmentId)) {
                    throw new BusinessException(
                        String.format("L'arrondissement '%s' n'appartient pas au département '%s'. " +
                            "Il appartient au département '%s'.",
                            arrondissement.getName(),
                            department.getName(),
                            arrondissement.getDepartment().getName()
                        )
                    );
                }
            }
        } else if (arrondissementId != null) {
            // Arrondissement spécifié sans département : erreur
            throw new BusinessException("Le département est obligatoire si l'arrondissement est spécifié");
        }

        log.debug("Validation géographique réussie: Région={}, Département={}, Arrondissement={}",
            regionId, departmentId, arrondissementId);
    }

    /**
     * Valide qu'un département appartient à une région donnée
     */
    public boolean isDepartmentInRegion(Long departmentId, Long regionId) {
        if (departmentId == null || regionId == null) {
            return false;
        }

        return departmentRepository.findById(departmentId)
            .map(dept -> dept.getRegion().getId().equals(regionId))
            .orElse(false);
    }

    /**
     * Valide qu'un arrondissement appartient à un département donné
     */
    public boolean isArrondissementInDepartment(Long arrondissementId, Long departmentId) {
        if (arrondissementId == null || departmentId == null) {
            return false;
        }

        return arrondissementRepository.findById(arrondissementId)
            .map(arr -> arr.getDepartment().getId().equals(departmentId))
            .orElse(false);
    }

    /**
     * Valide qu'un arrondissement appartient à une région donnée (via son département)
     */
    public boolean isArrondissementInRegion(Long arrondissementId, Long regionId) {
        if (arrondissementId == null || regionId == null) {
            return false;
        }

        return arrondissementRepository.findById(arrondissementId)
            .map(arr -> arr.getDepartment().getRegion().getId().equals(regionId))
            .orElse(false);
    }

    /**
     * Obtient l'ID de la région à partir d'un arrondissement
     */
    public Long getRegionIdFromArrondissement(Long arrondissementId) {
        return arrondissementRepository.findById(arrondissementId)
            .map(arr -> arr.getDepartment().getRegion().getId())
            .orElse(null);
    }

    /**
     * Obtient l'ID du département à partir d'un arrondissement
     */
    public Long getDepartmentIdFromArrondissement(Long arrondissementId) {
        return arrondissementRepository.findById(arrondissementId)
            .map(arr -> arr.getDepartment().getId())
            .orElse(null);
    }

    /**
     * Obtient l'ID de la région à partir d'un département
     */
    public Long getRegionIdFromDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
            .map(dept -> dept.getRegion().getId())
            .orElse(null);
    }

    /**
     * Valide la hiérarchie géographique complète
     * Retourne un message d'erreur descriptif si invalide, null sinon
     */
    public String validateGeographicHierarchy(Long regionId, Long departmentId, Long arrondissementId) {
        try {
            validateGeographicCoherence(regionId, departmentId, arrondissementId);
            return null;
        } catch (BusinessException e) {
            return e.getMessage();
        }
    }
}
