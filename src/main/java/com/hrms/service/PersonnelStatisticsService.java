package com.hrms.service;

import com.hrms.dto.PersonnelStatisticsDTO;
import com.hrms.entity.Personnel;
import com.hrms.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de calcul des statistiques complètes sur les personnels
 * Fournit des analyses globales et par structure
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonnelStatisticsService {

    private final PersonnelRepository personnelRepository;
    private final DateValidationService dateValidationService;

    /**
     * Calcule les statistiques globales de tous les personnels
     */
    public PersonnelStatisticsDTO getGlobalStatistics() {
        log.info("Calcul des statistiques globales de personnels");

        List<Personnel> allPersonnel = personnelRepository.findByDeletedFalse();
        return calculateStatistics(allPersonnel, "Global");
    }

    /**
     * Calcule les statistiques pour une structure spécifique
     */
    public PersonnelStatisticsDTO getStructureStatistics(Long structureId) {
        log.info("Calcul des statistiques pour la structure {}", structureId);

        List<Personnel> structurePersonnel = personnelRepository
            .findByStructureIdAndDeletedFalse(structureId);

        return calculateStatistics(structurePersonnel, "Structure ID: " + structureId);
    }

    /**
     * Calcule les statistiques pour une région
     */
    public PersonnelStatisticsDTO getRegionStatistics(Long regionId) {
        log.info("Calcul des statistiques pour la région {}", regionId);

        List<Personnel> regionPersonnel = personnelRepository
            .findByRegionOrigineIdAndDeletedFalse(regionId);

        return calculateStatistics(regionPersonnel, "Région ID: " + regionId);
    }

    /**
     * Méthode principale de calcul des statistiques
     */
    private PersonnelStatisticsDTO calculateStatistics(List<Personnel> personnelList, String period) {
        PersonnelStatisticsDTO stats = new PersonnelStatisticsDTO();
        stats.setPeriod(period);
        stats.setGeneratedAt(LocalDateTime.now().toString());

        if (personnelList.isEmpty()) {
            log.warn("Aucun personnel trouvé pour la période: {}", period);
            stats.setTotalPersonnel(0L);
            return stats;
        }

        // Effectif total
        stats.setTotalPersonnel((long) personnelList.size());

        // Effectifs par situation
        long activeCount = personnelList.stream()
            .filter(p -> p.getAdministrativeStatus() == Personnel.AdministrativeStatus.ACTIVE)
            .count();
        stats.setActivePersonnel(activeCount);
        stats.setInactivePersonnel(stats.getTotalPersonnel() - activeCount);

        // Personnels E.C.I
        long eciCount = personnelList.stream()
            .filter(p -> p.getMatricule() == null || p.getMatricule().isBlank())
            .count();
        stats.setEciPersonnel(eciCount);

        // Répartition par genre
        long maleCount = personnelList.stream()
            .filter(p -> p.getGender() == Personnel.Gender.MALE)
            .count();
        long femaleCount = personnelList.stream()
            .filter(p -> p.getGender() == Personnel.Gender.FEMALE)
            .count();

        stats.setMaleCount(maleCount);
        stats.setFemaleCount(femaleCount);
        stats.setMalePercentage(calculatePercentage(maleCount, stats.getTotalPersonnel()));
        stats.setFemalePercentage(calculatePercentage(femaleCount, stats.getTotalPersonnel()));

        // Répartition par situation administrative
        Map<String, Long> byAdminStatus = personnelList.stream()
            .filter(p -> p.getAdministrativeStatus() != null)
            .collect(Collectors.groupingBy(
                p -> p.getAdministrativeStatus().name(),
                Collectors.counting()
            ));
        stats.setByAdministrativeStatus(byAdminStatus);

        // Répartition par statut personnel
        Map<String, Long> byPersonnelStatus = personnelList.stream()
            .filter(p -> p.getPersonnelStatus() != null)
            .collect(Collectors.groupingBy(
                p -> p.getPersonnelStatus().name(),
                Collectors.counting()
            ));
        stats.setByPersonnelStatus(byPersonnelStatus);

        // Répartition par corps
        Map<String, Long> byCorps = personnelList.stream()
            .filter(p -> p.getCorps() != null)
            .collect(Collectors.groupingBy(
                p -> p.getCorps().getName(),
                Collectors.counting()
            ));
        stats.setByCorps(byCorps);

        // Répartition par grade
        Map<String, Long> byGrade = personnelList.stream()
            .filter(p -> p.getGrade() != null)
            .collect(Collectors.groupingBy(
                p -> p.getGrade().getName(),
                Collectors.counting()
            ));
        stats.setByGrade(byGrade);

        // Répartition par catégorie
        Map<String, Long> byCategory = personnelList.stream()
            .filter(p -> p.getCategory() != null && !p.getCategory().isBlank())
            .collect(Collectors.groupingBy(
                Personnel::getCategory,
                Collectors.counting()
            ));
        stats.setByCategory(byCategory);

        // Répartition par région d'origine
        Map<String, Long> byRegion = personnelList.stream()
            .filter(p -> p.getRegionOrigine() != null)
            .collect(Collectors.groupingBy(
                p -> p.getRegionOrigine().getName(),
                Collectors.counting()
            ));
        stats.setByRegionOrigine(byRegion);

        // Répartition par structure d'affectation
        Map<String, Long> byStructure = personnelList.stream()
            .filter(p -> p.getStructure() != null)
            .collect(Collectors.groupingBy(
                p -> p.getStructure().getName(),
                Collectors.counting()
            ));
        stats.setByStructure(byStructure);

        // Âge moyen
        double averageAge = personnelList.stream()
            .filter(p -> p.getDateOfBirth() != null)
            .mapToInt(p -> Period.between(p.getDateOfBirth(), LocalDate.now()).getYears())
            .average()
            .orElse(0.0);
        stats.setAverageAge(Math.round(averageAge * 100.0) / 100.0);

        // Ancienneté moyenne
        double avgSeniority = personnelList.stream()
            .filter(p -> p.getRecruitmentDate() != null)
            .mapToDouble(p -> dateValidationService.calculateSeniorityInYears(
                p.getRecruitmentDate(), LocalDate.now()))
            .average()
            .orElse(0.0);
        stats.setAverageSeniority(Math.round(avgSeniority * 100.0) / 100.0);

        // Répartition par tranche d'âge
        Map<String, Long> byAgeGroup = personnelList.stream()
            .filter(p -> p.getDateOfBirth() != null)
            .collect(Collectors.groupingBy(
                p -> getAgeGroup(Period.between(p.getDateOfBirth(), LocalDate.now()).getYears()),
                Collectors.counting()
            ));
        stats.setByAgeGroup(byAgeGroup);

        // Répartition par tranche d'ancienneté
        Map<String, Long> bySeniorityGroup = personnelList.stream()
            .filter(p -> p.getRecruitmentDate() != null)
            .collect(Collectors.groupingBy(
                p -> getSeniorityGroup(dateValidationService.calculateSeniorityInYears(
                    p.getRecruitmentDate(), LocalDate.now())),
                Collectors.counting()
            ));
        stats.setBySeniorityGroup(bySeniorityGroup);

        // Statistiques de retraite
        calculateRetirementStatistics(personnelList, stats);

        // Postes
        long withPosition = personnelList.stream()
            .filter(p -> p.getCurrentPosition() != null)
            .count();
        stats.setPersonnelWithPosition(withPosition);
        stats.setPersonnelWithoutPosition(stats.getTotalPersonnel() - withPosition);

        log.info("Statistiques calculées pour {} personnels", stats.getTotalPersonnel());

        return stats;
    }

    /**
     * Calcule les statistiques de retraite
     */
    private void calculateRetirementStatistics(List<Personnel> personnelList, PersonnelStatisticsDTO stats) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();

        // Retraitables maintenant
        long retirableNow = personnelList.stream()
            .filter(p -> p.getDateOfBirth() != null)
            .filter(p -> dateValidationService.isRetirable(p.getDateOfBirth(), p.getRetirementAge()))
            .count();
        stats.setRetirableNow(retirableNow);

        // Retraitables cette année
        long retirableThisYear = personnelList.stream()
            .filter(p -> p.getDateOfBirth() != null)
            .filter(p -> dateValidationService.isRetirableInYear(
                p.getDateOfBirth(), p.getRetirementAge(), currentYear))
            .count();
        stats.setRetirableThisYear(retirableThisYear);

        // Retraitables dans les 5 prochaines années
        long retirableNextFive = personnelList.stream()
            .filter(p -> p.getDateOfBirth() != null)
            .filter(p -> {
                int yearsUntil = dateValidationService.getYearsUntilRetirement(
                    p.getDateOfBirth(), p.getRetirementAge());
                return yearsUntil >= 0 && yearsUntil <= 5;
            })
            .count();
        stats.setRetirableNextFiveYears(retirableNextFive);

        // Répartition par année (5 prochaines années)
        Map<Integer, Long> byYear = new LinkedHashMap<>();
        for (int year = currentYear; year <= currentYear + 5; year++) {
            int finalYear = year;
            long count = personnelList.stream()
                .filter(p -> p.getDateOfBirth() != null)
                .filter(p -> dateValidationService.isRetirableInYear(
                    p.getDateOfBirth(), p.getRetirementAge(), finalYear))
                .count();
            byYear.put(year, count);
        }
        stats.setRetirementByYear(byYear);
    }

    /**
     * Détermine la tranche d'âge
     */
    private String getAgeGroup(int age) {
        if (age < 25) return "< 25";
        if (age < 30) return "25-29";
        if (age < 35) return "30-34";
        if (age < 40) return "35-39";
        if (age < 45) return "40-44";
        if (age < 50) return "45-49";
        if (age < 55) return "50-54";
        if (age < 60) return "55-59";
        return "60+";
    }

    /**
     * Détermine la tranche d'ancienneté
     */
    private String getSeniorityGroup(double years) {
        if (years < 1) return "< 1 an";
        if (years < 5) return "1-4 ans";
        if (years < 10) return "5-9 ans";
        if (years < 15) return "10-14 ans";
        if (years < 20) return "15-19 ans";
        if (years < 25) return "20-24 ans";
        if (years < 30) return "25-29 ans";
        return "30+ ans";
    }

    /**
     * Calcule un pourcentage
     */
    private Double calculatePercentage(long count, long total) {
        if (total == 0) return 0.0;
        return Math.round((count * 100.0 / total) * 100.0) / 100.0;
    }
}
