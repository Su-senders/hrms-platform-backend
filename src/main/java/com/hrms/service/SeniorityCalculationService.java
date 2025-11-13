package com.hrms.service;

import com.hrms.dto.SeniorityDetailsDTO;
import com.hrms.entity.Personnel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

/**
 * Service de calcul précis de l'ancienneté et autres métriques temporelles
 * Fournit des calculs détaillés pour la gestion de carrière
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeniorityCalculationService {

    private final DateValidationService dateValidationService;

    /**
     * Calcule tous les détails d'ancienneté d'un personnel
     *
     * @param personnel Personnel concerné
     * @return Détails complets de l'ancienneté
     */
    public SeniorityDetailsDTO calculateSeniorityDetails(Personnel personnel) {
        if (personnel == null) {
            return null;
        }

        LocalDate referenceDate = LocalDate.now();
        SeniorityDetailsDTO dto = new SeniorityDetailsDTO();

        // Ancienneté globale (depuis recrutement)
        if (personnel.getRecruitmentDate() != null) {
            Period globalSeniority = dateValidationService.calculateSeniority(
                personnel.getRecruitmentDate(), referenceDate);

            dto.setGlobalSeniorityYears(globalSeniority.getYears());
            dto.setGlobalSeniorityMonths(globalSeniority.getMonths());
            dto.setGlobalSeniorityDays(globalSeniority.getDays());
            dto.setGlobalSeniorityFormatted(dateValidationService.formatSeniority(globalSeniority));

            // Ancienneté totale en jours
            dto.setGlobalSeniorityTotalDays(
                dateValidationService.calculateSeniorityInDays(personnel.getRecruitmentDate(), referenceDate));

            // Ancienneté totale en années décimales
            dto.setGlobalSeniorityDecimal(
                dateValidationService.calculateSeniorityInYears(personnel.getRecruitmentDate(), referenceDate));
        }

        // Ancienneté dans le grade actuel
        if (personnel.getCurrentGradeDate() != null) {
            Period gradeSeniority = dateValidationService.calculateSeniority(
                personnel.getCurrentGradeDate(), referenceDate);

            dto.setGradeSeniorityYears(gradeSeniority.getYears());
            dto.setGradeSeniorityMonths(gradeSeniority.getMonths());
            dto.setGradeSeniorityDays(gradeSeniority.getDays());
            dto.setGradeSeniorityFormatted(dateValidationService.formatSeniority(gradeSeniority));
            dto.setGradeSeniorityTotalDays(
                dateValidationService.calculateSeniorityInDays(personnel.getCurrentGradeDate(), referenceDate));
        }

        // Ancienneté dans l'échelon actuel
        if (personnel.getCurrentEchelonDate() != null) {
            Period echelonSeniority = dateValidationService.calculateSeniority(
                personnel.getCurrentEchelonDate(), referenceDate);

            dto.setEchelonSeniorityYears(echelonSeniority.getYears());
            dto.setEchelonSeniorityMonths(echelonSeniority.getMonths());
            dto.setEchelonSeniorityDays(echelonSeniority.getDays());
            dto.setEchelonSeniorityFormatted(dateValidationService.formatSeniority(echelonSeniority));
            dto.setEchelonSeniorityTotalDays(
                dateValidationService.calculateSeniorityInDays(personnel.getCurrentEchelonDate(), referenceDate));
        }

        // Ancienneté dans le poste actuel
        if (personnel.getAppointmentDate() != null) {
            Period positionSeniority = dateValidationService.calculateSeniority(
                personnel.getAppointmentDate(), referenceDate);

            dto.setPositionSeniorityYears(positionSeniority.getYears());
            dto.setPositionSeniorityMonths(positionSeniority.getMonths());
            dto.setPositionSeniorityDays(positionSeniority.getDays());
            dto.setPositionSeniorityFormatted(dateValidationService.formatSeniority(positionSeniority));
            dto.setPositionSeniorityTotalDays(
                dateValidationService.calculateSeniorityInDays(personnel.getAppointmentDate(), referenceDate));
        }

        // Âge actuel
        if (personnel.getDateOfBirth() != null) {
            Period age = dateValidationService.calculateSeniority(
                personnel.getDateOfBirth(), referenceDate);

            dto.setCurrentAge(age.getYears());
            dto.setCurrentAgeFormatted(String.format("%d ans", age.getYears()));
        }

        // Date de retraite et années avant retraite
        LocalDate retirementDate = dateValidationService.calculateRetirementDate(
            personnel.getDateOfBirth(), personnel.getRetirementAge());

        dto.setRetirementDate(retirementDate);

        if (retirementDate != null) {
            int yearsUntilRetirement = dateValidationService.getYearsUntilRetirement(
                personnel.getDateOfBirth(), personnel.getRetirementAge());

            dto.setYearsUntilRetirement(yearsUntilRetirement);
            dto.setIsRetirable(yearsUntilRetirement <= 0);

            // Calcul des mois et jours jusqu'à la retraite si pas encore retraitable
            if (yearsUntilRetirement > 0) {
                Period periodUntilRetirement = Period.between(referenceDate, retirementDate);
                dto.setMonthsUntilRetirement(periodUntilRetirement.getMonths());
                dto.setDaysUntilRetirement(periodUntilRetirement.getDays());
            }
        }

        return dto;
    }

    /**
     * Calcule l'ancienneté spécifique entre deux dates
     */
    public SeniorityDetailsDTO calculateSeniorityBetweenDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            return null;
        }

        LocalDate referenceDate = (endDate != null) ? endDate : LocalDate.now();
        SeniorityDetailsDTO dto = new SeniorityDetailsDTO();

        Period seniority = dateValidationService.calculateSeniority(startDate, referenceDate);

        dto.setGlobalSeniorityYears(seniority.getYears());
        dto.setGlobalSeniorityMonths(seniority.getMonths());
        dto.setGlobalSeniorityDays(seniority.getDays());
        dto.setGlobalSeniorityFormatted(dateValidationService.formatSeniority(seniority));
        dto.setGlobalSeniorityTotalDays(
            dateValidationService.calculateSeniorityInDays(startDate, referenceDate));
        dto.setGlobalSeniorityDecimal(
            dateValidationService.calculateSeniorityInYears(startDate, referenceDate));

        return dto;
    }

    /**
     * Calcule l'ancienneté pour un avancement (vérifie l'éligibilité)
     */
    public boolean isEligibleForPromotion(LocalDate lastPromotionDate, int requiredYears) {
        if (lastPromotionDate == null) {
            return false;
        }

        long years = dateValidationService.calculateSeniorityInYears(lastPromotionDate, LocalDate.now());
        return years >= requiredYears;
    }

    /**
     * Calcule le nombre de jours de congé acquis en fonction de l'ancienneté
     * (Exemple : base 30 jours + 1 jour par tranche de 5 ans d'ancienneté)
     */
    public int calculateAnnualLeaveEntitlement(Personnel personnel) {
        if (personnel == null || personnel.getRecruitmentDate() == null) {
            return 30; // Base légale au Cameroun
        }

        double seniorityYears = dateValidationService.calculateSeniorityInYears(
            personnel.getRecruitmentDate(), LocalDate.now());

        // Base 30 jours + 1 jour supplémentaire par tranche de 5 ans
        int additionalDays = (int) (seniorityYears / 5);

        return 30 + additionalDays;
    }

    /**
     * Calcule si le personnel a atteint l'ancienneté minimale pour un concours/formation
     */
    public boolean hasMinimumSeniority(Personnel personnel, int requiredYears) {
        if (personnel == null || personnel.getRecruitmentDate() == null) {
            return false;
        }

        double seniorityYears = dateValidationService.calculateSeniorityInYears(
            personnel.getRecruitmentDate(), LocalDate.now());

        return seniorityYears >= requiredYears;
    }

    /**
     * Calcule l'ancienneté pour le calcul de prime d'ancienneté
     * (Souvent basé sur des tranches : 5, 10, 15, 20, 25, 30 ans)
     */
    public int getSeniorityBracket(Personnel personnel) {
        if (personnel == null || personnel.getRecruitmentDate() == null) {
            return 0;
        }

        double seniorityYears = dateValidationService.calculateSeniorityInYears(
            personnel.getRecruitmentDate(), LocalDate.now());

        if (seniorityYears >= 30) return 30;
        if (seniorityYears >= 25) return 25;
        if (seniorityYears >= 20) return 20;
        if (seniorityYears >= 15) return 15;
        if (seniorityYears >= 10) return 10;
        if (seniorityYears >= 5) return 5;

        return 0;
    }

    /**
     * Calcule la date estimée du prochain avancement/promotion
     */
    public LocalDate calculateNextPromotionDate(LocalDate lastPromotionDate, int requiredYears) {
        if (lastPromotionDate == null) {
            return null;
        }

        return lastPromotionDate.plusYears(requiredYears);
    }

    /**
     * Vérifie si le personnel est en période de stage
     */
    public boolean isOnProbation(Personnel personnel, int probationMonths) {
        if (personnel == null || personnel.getEffectiveDate() == null) {
            return false;
        }

        long monthsSinceStart = ChronoUnit.MONTHS.between(
            personnel.getEffectiveDate(), LocalDate.now());

        return monthsSinceStart < probationMonths;
    }

    /**
     * Calcule la date de fin de période de stage
     */
    public LocalDate calculateProbationEndDate(LocalDate effectiveDate, int probationMonths) {
        if (effectiveDate == null) {
            return null;
        }

        return effectiveDate.plusMonths(probationMonths);
    }
}
