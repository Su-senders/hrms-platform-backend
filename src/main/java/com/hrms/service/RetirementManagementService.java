package com.hrms.service;

import com.hrms.dto.RetirablePersonnelDTO;
import com.hrms.dto.SeniorityDetailsDTO;
import com.hrms.entity.Personnel;
import com.hrms.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service de gestion des personnels retraitables
 * Fournit des vues et rapports sur les personnels approchant de la retraite
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetirementManagementService {

    private final PersonnelRepository personnelRepository;
    private final DateValidationService dateValidationService;
    private final SeniorityCalculationService seniorityCalculationService;

    private static final int DEFAULT_RETIREMENT_AGE = 60;

    /**
     * Obtient tous les personnels retraitables (ayant atteint l'âge de retraite)
     */
    public List<RetirablePersonnelDTO> getRetirablePersonnel() {
        log.info("Recherche des personnels retraitables (âge >= 60 ans)");

        LocalDate maxBirthDate = LocalDate.now().minusYears(DEFAULT_RETIREMENT_AGE);

        List<Personnel> retirableList = personnelRepository.findByDateOfBirthLessThanEqualAndDeletedFalse(maxBirthDate);

        List<RetirablePersonnelDTO> result = retirableList.stream()
            .map(this::mapToRetirableDTO)
            .sorted(Comparator.comparing(RetirablePersonnelDTO::getRetirementDate))
            .toList();

        log.info("Trouvé {} personnels retraitables", result.size());
        return result;
    }

    /**
     * Obtient les personnels retraitables dans une année spécifique
     */
    public List<RetirablePersonnelDTO> getRetirablePersonnelByYear(int year) {
        log.info("Recherche des personnels retraitables en {}", year);

        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);

        // Date de naissance doit être 60 ans avant l'année spécifiée
        LocalDate birthStart = yearStart.minusYears(DEFAULT_RETIREMENT_AGE);
        LocalDate birthEnd = yearEnd.minusYears(DEFAULT_RETIREMENT_AGE);

        List<Personnel> retirableList = personnelRepository
            .findByDateOfBirthBetweenAndDeletedFalse(birthStart, birthEnd);

        List<RetirablePersonnelDTO> result = retirableList.stream()
            .map(this::mapToRetirableDTO)
            .filter(dto -> dto.getRetirementDate().getYear() == year)
            .sorted(Comparator.comparing(RetirablePersonnelDTO::getRetirementDate))
            .toList();

        log.info("Trouvé {} personnels retraitables en {}", result.size(), year);
        return result;
    }

    /**
     * Obtient les personnels retraitables par structure et année
     */
    public List<RetirablePersonnelDTO> getRetirablePersonnelByStructureAndYear(Long structureId, Integer year) {
        log.info("Recherche des personnels retraitables - Structure: {}, Année: {}", structureId, year);

        List<RetirablePersonnelDTO> allRetirable = (year != null)
            ? getRetirablePersonnelByYear(year)
            : getRetirablePersonnel();

        List<RetirablePersonnelDTO> filtered = allRetirable.stream()
            .filter(dto -> dto.getStructureId() != null && dto.getStructureId().equals(structureId))
            .toList();

        log.info("Trouvé {} personnels retraitables pour la structure {}", filtered.size(), structureId);
        return filtered;
    }

    /**
     * Obtient les personnels qui seront retraitables dans les X prochaines années
     */
    public List<RetirablePersonnelDTO> getUpcomingRetirablePersonnel(int withinYears) {
        log.info("Recherche des personnels retraitables dans les {} prochaines années", withinYears);

        LocalDate maxBirthDate = LocalDate.now().minusYears(DEFAULT_RETIREMENT_AGE - withinYears);
        LocalDate minBirthDate = LocalDate.now().minusYears(DEFAULT_RETIREMENT_AGE);

        List<Personnel> upcomingList = personnelRepository
            .findByDateOfBirthBetweenAndDeletedFalse(minBirthDate, maxBirthDate);

        List<RetirablePersonnelDTO> result = upcomingList.stream()
            .map(this::mapToRetirableDTO)
            .sorted(Comparator.comparing(RetirablePersonnelDTO::getRetirementDate))
            .toList();

        log.info("Trouvé {} personnels retraitables dans les {} prochaines années", result.size(), withinYears);
        return result;
    }

    /**
     * Obtient les personnels retraitables dans les 3-6 prochains mois (pré-retraite)
     */
    public List<RetirablePersonnelDTO> getPreRetirementPersonnel() {
        log.info("Recherche des personnels en pré-retraite (3-6 mois)");

        LocalDate threeMonthsFromNow = LocalDate.now().plusMonths(3);
        LocalDate sixMonthsFromNow = LocalDate.now().plusMonths(6);

        List<Personnel> allPersonnel = personnelRepository.findByDeletedFalse();

        List<RetirablePersonnelDTO> result = allPersonnel.stream()
            .map(this::mapToRetirableDTO)
            .filter(dto -> {
                LocalDate retirementDate = dto.getRetirementDate();
                return retirementDate != null &&
                       !retirementDate.isBefore(threeMonthsFromNow) &&
                       !retirementDate.isAfter(sixMonthsFromNow);
            })
            .sorted(Comparator.comparing(RetirablePersonnelDTO::getRetirementDate))
            .toList();

        log.info("Trouvé {} personnels en pré-retraite", result.size());
        return result;
    }

    /**
     * Statistiques de retraites par année
     */
    public Map<Integer, Long> getRetirementStatisticsByYear(int startYear, int endYear) {
        log.info("Calcul des statistiques de retraites de {} à {}", startYear, endYear);

        List<Personnel> allPersonnel = personnelRepository.findByDeletedFalse();

        return allPersonnel.stream()
            .map(this::mapToRetirableDTO)
            .filter(dto -> dto.getRetirementDate() != null)
            .filter(dto -> {
                int year = dto.getRetirementDate().getYear();
                return year >= startYear && year <= endYear;
            })
            .collect(Collectors.groupingBy(
                dto -> dto.getRetirementDate().getYear(),
                Collectors.counting()
            ));
    }

    /**
     * Statistiques de retraites par structure
     */
    public Map<String, Long> getRetirementStatisticsByStructure(Integer year) {
        log.info("Calcul des statistiques de retraites par structure pour l'année {}", year);

        List<RetirablePersonnelDTO> retirableList = (year != null)
            ? getRetirablePersonnelByYear(year)
            : getRetirablePersonnel();

        return retirableList.stream()
            .filter(dto -> dto.getStructureName() != null)
            .collect(Collectors.groupingBy(
                RetirablePersonnelDTO::getStructureName,
                Collectors.counting()
            ));
    }

    /**
     * Obtient les personnels retraitables avec pagination
     */
    public Page<RetirablePersonnelDTO> getRetirablePersonnelPaged(Integer year, Long structureId, Pageable pageable) {
        List<RetirablePersonnelDTO> allRetirable;

        if (structureId != null && year != null) {
            allRetirable = getRetirablePersonnelByStructureAndYear(structureId, year);
        } else if (structureId != null) {
            allRetirable = getRetirablePersonnelByStructureAndYear(structureId, null);
        } else if (year != null) {
            allRetirable = getRetirablePersonnelByYear(year);
        } else {
            allRetirable = getRetirablePersonnel();
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allRetirable.size());

        List<RetirablePersonnelDTO> pageContent = allRetirable.subList(start, end);

        return new PageImpl<>(pageContent, pageable, allRetirable.size());
    }

    /**
     * Obtient les personnels retraitables avec pagination (méthode simple)
     */
    public Page<RetirablePersonnelDTO> getRetirablePersonnelPaginated(Pageable pageable) {
        List<RetirablePersonnelDTO> allRetirable = getRetirablePersonnel();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allRetirable.size());

        if (start > allRetirable.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, allRetirable.size());
        }

        List<RetirablePersonnelDTO> pageContent = allRetirable.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allRetirable.size());
    }

    /**
     * Obtient les personnels en pré-retraite avec pagination
     */
    public Page<RetirablePersonnelDTO> getPreRetirementPersonnelPaginated(Pageable pageable) {
        List<RetirablePersonnelDTO> allPreRetirement = getPreRetirementPersonnel();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allPreRetirement.size());

        if (start > allPreRetirement.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, allPreRetirement.size());
        }

        List<RetirablePersonnelDTO> pageContent = allPreRetirement.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allPreRetirement.size());
    }

    /**
     * Obtient les personnels retraitables dans les N prochains mois
     */
    public List<RetirablePersonnelDTO> getRetirableWithinMonths(int months) {
        log.info("Recherche des personnels retraitables dans les {} prochains mois", months);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(months);

        List<Personnel> allPersonnel = personnelRepository.findByDeletedFalse();

        List<RetirablePersonnelDTO> result = allPersonnel.stream()
            .map(this::mapToRetirableDTO)
            .filter(dto -> {
                LocalDate retirementDate = dto.getRetirementDate();
                return retirementDate != null &&
                       !retirementDate.isBefore(startDate) &&
                       !retirementDate.isAfter(endDate);
            })
            .sorted(Comparator.comparing(RetirablePersonnelDTO::getRetirementDate))
            .toList();

        log.info("Trouvé {} personnels retraitables dans les {} prochains mois", result.size(), months);
        return result;
    }

    /**
     * Obtient les statistiques de retraites par structure (ID)
     */
    public Map<Long, Long> getRetirementStatisticsByStructure(int startYear, int endYear) {
        log.info("Calcul des statistiques de retraites par structure de {} à {}", startYear, endYear);

        List<Personnel> allPersonnel = personnelRepository.findByDeletedFalse();

        return allPersonnel.stream()
            .map(this::mapToRetirableDTO)
            .filter(dto -> dto.getRetirementDate() != null)
            .filter(dto -> {
                int year = dto.getRetirementDate().getYear();
                return year >= startYear && year <= endYear;
            })
            .filter(dto -> dto.getStructureId() != null)
            .collect(Collectors.groupingBy(
                RetirablePersonnelDTO::getStructureId,
                Collectors.counting()
            ));
    }

    /**
     * Obtient les statistiques globales de retraites
     */
    public Map<String, Object> getGlobalRetirementStatistics() {
        log.info("Calcul des statistiques globales de retraites");

        int currentYear = LocalDate.now().getYear();
        List<RetirablePersonnelDTO> allRetirable = getRetirablePersonnel();
        List<RetirablePersonnelDTO> preRetirement = getPreRetirementPersonnel();

        Map<Integer, Long> next5Years = getRetirementStatisticsByYear(currentYear, currentYear + 4);

        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalRetirable", allRetirable.size());
        stats.put("preRetirementCount", preRetirement.size());
        stats.put("currentYear", currentYear);
        stats.put("forecastNext5Years", next5Years);

        return stats;
    }

    /**
     * Mappe un Personnel vers RetirablePersonnelDTO avec tous les calculs
     */
    private RetirablePersonnelDTO mapToRetirableDTO(Personnel personnel) {
        SeniorityDetailsDTO seniority = seniorityCalculationService.calculateSeniorityDetails(personnel);

        LocalDate retirementDate = dateValidationService.calculateRetirementDate(
            personnel.getDateOfBirth(),
            personnel.getRetirementAge() != null ? personnel.getRetirementAge() : DEFAULT_RETIREMENT_AGE
        );

        int currentAge = Period.between(personnel.getDateOfBirth(), LocalDate.now()).getYears();

        Integer yearsUntil = null;
        Integer monthsUntil = null;
        Integer daysUntil = null;
        boolean isRetirable = false;

        if (retirementDate != null) {
            if (retirementDate.isBefore(LocalDate.now()) || retirementDate.isEqual(LocalDate.now())) {
                isRetirable = true;
                yearsUntil = 0;
                monthsUntil = 0;
                daysUntil = 0;
            } else {
                Period periodUntil = Period.between(LocalDate.now(), retirementDate);
                yearsUntil = periodUntil.getYears();
                monthsUntil = periodUntil.getMonths();
                daysUntil = periodUntil.getDays();
            }
        }

        return RetirablePersonnelDTO.builder()
            .id(personnel.getId())
            .matricule(personnel.getMatricule())
            .lastName(personnel.getLastName())
            .firstName(personnel.getFirstName())
            .fullName(personnel.getFirstName() + " " + personnel.getLastName())
            .dateOfBirth(personnel.getDateOfBirth())
            .currentAge(currentAge)
            .retirementDate(retirementDate)
            .retirementAge(personnel.getRetirementAge() != null ? personnel.getRetirementAge() : DEFAULT_RETIREMENT_AGE)
            .yearsUntilRetirement(yearsUntil)
            .monthsUntilRetirement(monthsUntil)
            .daysUntilRetirement(daysUntil)
            .isRetirable(isRetirable)
            .structureName(personnel.getStructure() != null ? personnel.getStructure().getName() : null)
            .structureId(personnel.getStructure() != null ? personnel.getStructure().getId() : null)
            .positionTitle(personnel.getCurrentPosition() != null ? personnel.getCurrentPosition().getTitle() : null)
            .positionId(personnel.getCurrentPosition() != null ? personnel.getCurrentPosition().getId() : null)
            .gradeName(personnel.getGrade() != null ? personnel.getGrade().getName() : null)
            .gradeId(personnel.getGrade() != null ? personnel.getGrade().getId() : null)
            .corpsName(personnel.getCorps() != null ? personnel.getCorps().getName() : null)
            .corpsId(personnel.getCorps() != null ? personnel.getCorps().getId() : null)
            .globalSeniorityFormatted(seniority != null ? seniority.getGlobalSeniorityFormatted() : null)
            .globalSeniorityYears(seniority != null ? seniority.getGlobalSeniorityDecimal() : null)
            .email(personnel.getEmail())
            .phoneNumber(personnel.getPhoneNumber())
            .regionOrigineName(personnel.getRegionOrigine() != null ? personnel.getRegionOrigine().getName() : null)
            .departmentOrigineName(personnel.getDepartmentOrigine() != null ? personnel.getDepartmentOrigine().getName() : null)
            .arrondissementOrigineName(personnel.getArrondissementOrigine() != null ? personnel.getArrondissementOrigine().getName() : null)
            .build();
    }
}
