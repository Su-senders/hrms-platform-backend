package com.hrms.service;

import com.hrms.dto.PersonnelDTO;
import com.hrms.dto.PersonnelSearchCriteriaDTO;
import com.hrms.entity.*;
import com.hrms.mapper.PersonnelMapper;
import com.hrms.repository.PersonnelRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de recherche avancée multicritère pour les personnels
 * Utilise Spring Data JPA Specifications pour des requêtes dynamiques
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonnelAdvancedSearchService {

    private final PersonnelRepository personnelRepository;
    private final PersonnelMapper personnelMapper;
    private final DateValidationService dateValidationService;

    /**
     * Recherche avancée avec pagination
     */
    public Page<PersonnelDTO> advancedSearch(PersonnelSearchCriteriaDTO criteria, Pageable pageable) {
        log.info("Recherche avancée avec critères: {}", criteria);

        // Créer la specification dynamique
        Specification<Personnel> spec = buildSpecification(criteria);

        // Appliquer le tri si spécifié
        Pageable pageableWithSort = applySort(criteria, pageable);

        // Exécuter la recherche
        Page<Personnel> personnelPage = personnelRepository.findAll(spec, pageableWithSort);

        log.info("Recherche avancée: {} résultats trouvés", personnelPage.getTotalElements());

        return personnelPage.map(personnelMapper::toDTO);
    }

    /**
     * Recherche avancée sans pagination (pour exports)
     */
    public List<PersonnelDTO> advancedSearchAll(PersonnelSearchCriteriaDTO criteria) {
        log.info("Recherche avancée complète avec critères: {}", criteria);

        Specification<Personnel> spec = buildSpecification(criteria);
        Sort sort = buildSort(criteria);

        List<Personnel> personnelList = personnelRepository.findAll(spec, sort);

        log.info("Recherche avancée complète: {} résultats trouvés", personnelList.size());

        return personnelList.stream()
            .map(personnelMapper::toDTO)
            .toList();
    }

    /**
     * Construit la Specification dynamique basée sur les critères
     */
    private Specification<Personnel> buildSpecification(PersonnelSearchCriteriaDTO criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Toujours exclure les supprimés
            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

            // ==================== IDENTIFICATION ====================

            if (criteria.getMatricule() != null && !criteria.getMatricule().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("matricule")),
                    "%" + criteria.getMatricule().toLowerCase() + "%"
                ));
            }

            if (criteria.getLastName() != null && !criteria.getLastName().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("lastName")),
                    "%" + criteria.getLastName().toLowerCase() + "%"
                ));
            }

            if (criteria.getFirstName() != null && !criteria.getFirstName().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("firstName")),
                    "%" + criteria.getFirstName().toLowerCase() + "%"
                ));
            }

            if (criteria.getCniNumber() != null && !criteria.getCniNumber().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("cniNumber")),
                    "%" + criteria.getCniNumber().toLowerCase() + "%"
                ));
            }

            // Recherche globale (nom OU prénom OU matricule)
            if (criteria.getGlobalSearch() != null && !criteria.getGlobalSearch().isBlank()) {
                String searchTerm = "%" + criteria.getGlobalSearch().toLowerCase() + "%";
                Predicate globalPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("matricule")), searchTerm)
                );
                predicates.add(globalPredicate);
            }

            // ==================== CARACTÉRISTIQUES PERSONNELLES ====================

            if (criteria.getGender() != null) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), criteria.getGender()));
            }

            if (criteria.getMaritalStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("maritalStatus"), criteria.getMaritalStatus()));
            }

            // Âge (calculé à partir de la date de naissance)
            if (criteria.getMinAge() != null || criteria.getMaxAge() != null) {
                LocalDate now = LocalDate.now();

                if (criteria.getMinAge() != null) {
                    LocalDate maxBirthDate = now.minusYears(criteria.getMinAge());
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateOfBirth"), maxBirthDate));
                }

                if (criteria.getMaxAge() != null) {
                    LocalDate minBirthDate = now.minusYears(criteria.getMaxAge() + 1).plusDays(1);
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateOfBirth"), minBirthDate));
                }
            }

            if (criteria.getDateOfBirthFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("dateOfBirth"), criteria.getDateOfBirthFrom()));
            }

            if (criteria.getDateOfBirthTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("dateOfBirth"), criteria.getDateOfBirthTo()));
            }

            // ==================== ORIGINE GÉOGRAPHIQUE ====================

            if (criteria.getRegionOrigineId() != null) {
                Join<Personnel, Region> regionJoin = root.join("regionOrigine", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(regionJoin.get("id"), criteria.getRegionOrigineId()));
            }

            if (criteria.getDepartmentOrigineId() != null) {
                Join<Personnel, Department> deptJoin = root.join("departmentOrigine", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(deptJoin.get("id"), criteria.getDepartmentOrigineId()));
            }

            if (criteria.getArrondissementOrigineId() != null) {
                Join<Personnel, Arrondissement> arrJoin = root.join("arrondissementOrigine", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(arrJoin.get("id"), criteria.getArrondissementOrigineId()));
            }

            // ==================== AFFECTATION ACTUELLE ====================

            if (criteria.getStructureId() != null) {
                Join<Personnel, AdministrativeStructure> structureJoin = root.join("structure", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(structureJoin.get("id"), criteria.getStructureId()));
            }

            if (criteria.getCurrentPositionId() != null) {
                Join<Personnel, Position> positionJoin = root.join("currentPosition", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(positionJoin.get("id"), criteria.getCurrentPositionId()));
            }

            if (criteria.getPositionType() != null && !criteria.getPositionType().isBlank()) {
                Join<Personnel, Position> positionJoin = root.join("currentPosition", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(positionJoin.get("type")),
                    "%" + criteria.getPositionType().toLowerCase() + "%"
                ));
            }

            // ==================== CORPS ET GRADE ====================

            if (criteria.getCorpsId() != null) {
                Join<Personnel, Corps> corpsJoin = root.join("corps", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(corpsJoin.get("id"), criteria.getCorpsId()));
            }

            if (criteria.getGradeId() != null) {
                Join<Personnel, Grade> gradeJoin = root.join("grade", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(gradeJoin.get("id"), criteria.getGradeId()));
            }

            if (criteria.getMinEchelon() != null && !criteria.getMinEchelon().isBlank()) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("echelon"), criteria.getMinEchelon()));
            }

            if (criteria.getMaxEchelon() != null && !criteria.getMaxEchelon().isBlank()) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("echelon"), criteria.getMaxEchelon()));
            }

            if (criteria.getCategory() != null && !criteria.getCategory().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("category"), criteria.getCategory()));
            }

            // ==================== SITUATION ADMINISTRATIVE ====================

            if (criteria.getAdministrativeStatus() != null && !criteria.getAdministrativeStatus().isEmpty()) {
                predicates.add(root.get("administrativeStatus").in(criteria.getAdministrativeStatus()));
            }

            if (criteria.getRecruitmentType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("recruitmentType"), criteria.getRecruitmentType()));
            }

            if (criteria.getPersonnelStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("personnelStatus"), criteria.getPersonnelStatus()));
            }

            // ==================== DATES DE CARRIÈRE ====================

            if (criteria.getRecruitmentDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("recruitmentDate"), criteria.getRecruitmentDateFrom()));
            }

            if (criteria.getRecruitmentDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("recruitmentDate"), criteria.getRecruitmentDateTo()));
            }

            if (criteria.getFirstAppointmentDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("firstAppointmentDate"), criteria.getFirstAppointmentDateFrom()));
            }

            if (criteria.getFirstAppointmentDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("firstAppointmentDate"), criteria.getFirstAppointmentDateTo()));
            }

            // ==================== ANCIENNETÉ ====================

            if (criteria.getMinSeniorityYears() != null || criteria.getMaxSeniorityYears() != null) {
                LocalDate now = LocalDate.now();

                if (criteria.getMinSeniorityYears() != null) {
                    LocalDate maxRecruitmentDate = now.minusYears(criteria.getMinSeniorityYears());
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("recruitmentDate"), maxRecruitmentDate));
                }

                if (criteria.getMaxSeniorityYears() != null) {
                    LocalDate minRecruitmentDate = now.minusYears(criteria.getMaxSeniorityYears());
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("recruitmentDate"), minRecruitmentDate));
                }
            }

            if (criteria.getMinGradeSeniorityYears() != null || criteria.getMaxGradeSeniorityYears() != null) {
                LocalDate now = LocalDate.now();

                if (criteria.getMinGradeSeniorityYears() != null) {
                    LocalDate maxGradeDate = now.minusYears(criteria.getMinGradeSeniorityYears());
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("currentGradeDate"), maxGradeDate));
                }

                if (criteria.getMaxGradeSeniorityYears() != null) {
                    LocalDate minGradeDate = now.minusYears(criteria.getMaxGradeSeniorityYears());
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("currentGradeDate"), minGradeDate));
                }
            }

            // ==================== RETRAITE ====================

            if (criteria.getRetirable() != null && criteria.getRetirable()) {
                // Personnel retraitable maintenant (a atteint l'âge de retraite)
                LocalDate maxBirthDate = LocalDate.now().minusYears(60); // 60 ans par défaut
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateOfBirth"), maxBirthDate));
            }

            if (criteria.getRetirableInYear() != null) {
                // Personnel retraitable dans une année spécifique
                LocalDate yearStart = LocalDate.of(criteria.getRetirableInYear(), 1, 1);
                LocalDate yearEnd = LocalDate.of(criteria.getRetirableInYear(), 12, 31);

                // Date de naissance doit être 60 ans avant l'année spécifiée
                LocalDate birthStart = yearStart.minusYears(60);
                LocalDate birthEnd = yearEnd.minusYears(60);

                predicates.add(criteriaBuilder.between(root.get("dateOfBirth"), birthStart, birthEnd));
            }

            if (criteria.getRetirableWithinYears() != null) {
                // Personnel retraitable dans les X prochaines années
                LocalDate maxBirthDate = LocalDate.now().minusYears(60 - criteria.getRetirableWithinYears());
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateOfBirth"), maxBirthDate));
            }

            // ==================== FONCTIONS ET RESPONSABILITÉS ====================

            if (criteria.getIsHierarchicalSupervisor() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("isHierarchicalSupervisor"), criteria.getIsHierarchicalSupervisor()));
            }

            if (criteria.getCurrentFunction() != null && !criteria.getCurrentFunction().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("currentFunction")),
                    "%" + criteria.getCurrentFunction().toLowerCase() + "%"
                ));
            }

            if (criteria.getOfficialCumul() != null) {
                predicates.add(criteriaBuilder.equal(root.get("officialCumul"), criteria.getOfficialCumul()));
            }

            // ==================== SITUATION PARTICULIÈRE ====================

            if (criteria.getOnSecondment() != null) {
                if (criteria.getOnSecondment()) {
                    predicates.add(criteriaBuilder.equal(
                        root.get("administrativeStatus"), Personnel.AdministrativeStatus.ON_SECONDMENT));
                }
            }

            if (criteria.getOnProbation() != null && criteria.getOnProbation()) {
                // En stage si moins de 12 mois depuis la date de prise de service
                LocalDate probationEndDate = LocalDate.now().minusMonths(12);
                predicates.add(criteriaBuilder.greaterThan(root.get("effectiveDate"), probationEndDate));
            }

            if (criteria.getEci() != null) {
                if (criteria.getEci()) {
                    // Personnel E.C.I (sans matricule)
                    predicates.add(criteriaBuilder.isNull(root.get("matricule")));
                } else {
                    // Personnel avec matricule
                    predicates.add(criteriaBuilder.isNotNull(root.get("matricule")));
                }
            }

            // ==================== CONTACT ====================

            if (criteria.getEmail() != null && !criteria.getEmail().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")),
                    "%" + criteria.getEmail().toLowerCase() + "%"
                ));
            }

            if (criteria.getPhoneNumber() != null && !criteria.getPhoneNumber().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("phoneNumber")),
                    "%" + criteria.getPhoneNumber().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Applique le tri spécifié dans les critères
     */
    private Pageable applySort(PersonnelSearchCriteriaDTO criteria, Pageable pageable) {
        Sort sort = buildSort(criteria);

        if (sort.isSorted()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        return pageable;
    }

    /**
     * Construit l'objet Sort basé sur les critères
     */
    private Sort buildSort(PersonnelSearchCriteriaDTO criteria) {
        if (criteria.getSortBy() != null && !criteria.getSortBy().isBlank()) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(criteria.getSortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

            return Sort.by(direction, criteria.getSortBy());
        }

        // Tri par défaut : nom de famille
        return Sort.by(Sort.Direction.ASC, "lastName", "firstName");
    }

    /**
     * Compte le nombre de résultats d'une recherche avancée sans les récupérer
     */
    public long countAdvancedSearch(PersonnelSearchCriteriaDTO criteria) {
        log.info("Comptage des résultats pour les critères: {}", criteria);

        Specification<Personnel> spec = buildSpecification(criteria);
        long count = personnelRepository.count(spec);

        log.info("Nombre de résultats trouvés: {}", count);
        return count;
    }
}
