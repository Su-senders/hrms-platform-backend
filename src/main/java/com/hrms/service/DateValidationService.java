package com.hrms.service;

import com.hrms.entity.Personnel;
import com.hrms.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

/**
 * Service de validation de la cohérence des dates
 * Assure que toutes les dates dans le système sont logiques et cohérentes
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DateValidationService {

    /**
     * Âge minimum légal pour être employé (18 ans)
     */
    private static final int MINIMUM_AGE = 18;

    /**
     * Âge maximum légal pour la retraite (60 ans par défaut au Cameroun)
     */
    private static final int DEFAULT_RETIREMENT_AGE = 60;

    /**
     * Âge minimum raisonnable pour débuter une carrière (16 ans pour les formations)
     */
    private static final int MINIMUM_CAREER_START_AGE = 16;

    /**
     * Valide qu'une date de fin est après une date de début
     */
    public void validateDateRange(LocalDate startDate, LocalDate endDate, String context) {
        if (startDate == null || endDate == null) {
            return; // Dates optionnelles
        }

        if (endDate.isBefore(startDate)) {
            throw new BusinessException(
                String.format("%s: La date de fin (%s) ne peut pas être antérieure à la date de début (%s)",
                    context, endDate, startDate)
            );
        }
    }

    /**
     * Valide qu'une date n'est pas dans le futur (sauf si explicitement autorisé)
     */
    public void validateNotFuture(LocalDate date, String fieldName) {
        if (date == null) {
            return;
        }

        if (date.isAfter(LocalDate.now())) {
            throw new BusinessException(
                String.format("%s ne peut pas être une date future (%s)", fieldName, date)
            );
        }
    }

    /**
     * Valide qu'une date est dans le futur
     */
    public void validateFuture(LocalDate date, String fieldName) {
        if (date == null) {
            return;
        }

        if (date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now())) {
            throw new BusinessException(
                String.format("%s doit être une date future (actuelle: %s)", fieldName, date)
            );
        }
    }

    /**
     * Valide l'âge d'un personnel en fonction de sa date de naissance
     */
    public void validatePersonnelAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new BusinessException("La date de naissance est obligatoire");
        }

        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new BusinessException(
                String.format("La date de naissance ne peut pas être dans le futur (%s)", dateOfBirth)
            );
        }

        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();

        if (age < MINIMUM_AGE) {
            throw new BusinessException(
                String.format("Le personnel doit avoir au moins %d ans (âge actuel: %d ans)",
                    MINIMUM_AGE, age)
            );
        }

        if (age > 120) {
            throw new BusinessException(
                String.format("Date de naissance invalide: âge calculé de %d ans", age)
            );
        }
    }

    /**
     * Valide qu'une date de recrutement est cohérente avec la date de naissance
     */
    public void validateRecruitmentDate(LocalDate dateOfBirth, LocalDate recruitmentDate) {
        if (dateOfBirth == null || recruitmentDate == null) {
            return;
        }

        if (recruitmentDate.isBefore(dateOfBirth)) {
            throw new BusinessException(
                "La date de recrutement ne peut pas être antérieure à la date de naissance"
            );
        }

        int ageAtRecruitment = Period.between(dateOfBirth, recruitmentDate).getYears();

        if (ageAtRecruitment < MINIMUM_CAREER_START_AGE) {
            throw new BusinessException(
                String.format("Le personnel ne peut pas être recruté avant l'âge de %d ans " +
                    "(âge au recrutement: %d ans)", MINIMUM_CAREER_START_AGE, ageAtRecruitment)
            );
        }
    }

    /**
     * Valide qu'une date d'affectation est cohérente avec la date de recrutement
     */
    public void validateAssignmentDate(LocalDate recruitmentDate, LocalDate assignmentDate) {
        if (recruitmentDate == null || assignmentDate == null) {
            return;
        }

        if (assignmentDate.isBefore(recruitmentDate)) {
            throw new BusinessException(
                String.format("La date d'affectation (%s) ne peut pas être antérieure " +
                    "à la date de recrutement (%s)", assignmentDate, recruitmentDate)
            );
        }
    }

    /**
     * Valide qu'une date de nomination est cohérente
     */
    public void validateAppointmentDate(LocalDate recruitmentDate, LocalDate appointmentDate) {
        if (recruitmentDate == null || appointmentDate == null) {
            return;
        }

        if (appointmentDate.isBefore(recruitmentDate)) {
            throw new BusinessException(
                String.format("La date de nomination (%s) ne peut pas être antérieure " +
                    "à la date de recrutement (%s)", appointmentDate, recruitmentDate)
            );
        }
    }

    /**
     * Valide la date de prise de service
     */
    public void validateEffectiveDate(LocalDate appointmentDate, LocalDate effectiveDate) {
        if (appointmentDate == null || effectiveDate == null) {
            return;
        }

        if (effectiveDate.isBefore(appointmentDate)) {
            throw new BusinessException(
                String.format("La date de prise de service (%s) ne peut pas être antérieure " +
                    "à la date de nomination (%s)", effectiveDate, appointmentDate)
            );
        }
    }

    /**
     * Calcule l'ancienneté en années, mois et jours
     */
    public Period calculateSeniority(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            return Period.ZERO;
        }

        LocalDate referenceDate = (endDate != null) ? endDate : LocalDate.now();
        return Period.between(startDate, referenceDate);
    }

    /**
     * Calcule l'ancienneté en jours
     */
    public long calculateSeniorityInDays(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            return 0;
        }

        LocalDate referenceDate = (endDate != null) ? endDate : LocalDate.now();
        return ChronoUnit.DAYS.between(startDate, referenceDate);
    }

    /**
     * Calcule l'ancienneté en mois
     */
    public long calculateSeniorityInMonths(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            return 0;
        }

        LocalDate referenceDate = (endDate != null) ? endDate : LocalDate.now();
        return ChronoUnit.MONTHS.between(startDate, referenceDate);
    }

    /**
     * Calcule l'ancienneté en années (décimal)
     */
    public double calculateSeniorityInYears(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            return 0.0;
        }

        LocalDate referenceDate = (endDate != null) ? endDate : LocalDate.now();
        long days = ChronoUnit.DAYS.between(startDate, referenceDate);
        return days / 365.25; // 365.25 pour tenir compte des années bissextiles
    }

    /**
     * Formate l'ancienneté en chaîne lisible (ex: "5 ans, 3 mois, 12 jours")
     */
    public String formatSeniority(Period seniority) {
        if (seniority == null || seniority.isZero()) {
            return "0 an";
        }

        StringBuilder sb = new StringBuilder();

        if (seniority.getYears() > 0) {
            sb.append(seniority.getYears()).append(" an");
            if (seniority.getYears() > 1) sb.append("s");
        }

        if (seniority.getMonths() > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(seniority.getMonths()).append(" mois");
        }

        if (seniority.getDays() > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(seniority.getDays()).append(" jour");
            if (seniority.getDays() > 1) sb.append("s");
        }

        return sb.toString();
    }

    /**
     * Calcule la date de retraite estimée d'un personnel
     *
     * @param dateOfBirth Date de naissance
     * @param retirementAge Âge de retraite (60 par défaut)
     * @return Date de retraite estimée
     */
    public LocalDate calculateRetirementDate(LocalDate dateOfBirth, Integer retirementAge) {
        if (dateOfBirth == null) {
            return null;
        }

        int age = (retirementAge != null && retirementAge > 0) ? retirementAge : DEFAULT_RETIREMENT_AGE;
        return dateOfBirth.plusYears(age);
    }

    /**
     * Vérifie si un personnel est retraitable (a atteint l'âge de retraite)
     */
    public boolean isRetirable(LocalDate dateOfBirth, Integer retirementAge) {
        if (dateOfBirth == null) {
            return false;
        }

        LocalDate retirementDate = calculateRetirementDate(dateOfBirth, retirementAge);
        return retirementDate != null && !retirementDate.isAfter(LocalDate.now());
    }

    /**
     * Calcule le nombre d'années avant la retraite
     */
    public int getYearsUntilRetirement(LocalDate dateOfBirth, Integer retirementAge) {
        if (dateOfBirth == null) {
            return -1;
        }

        LocalDate retirementDate = calculateRetirementDate(dateOfBirth, retirementAge);
        if (retirementDate == null) {
            return -1;
        }

        Period period = Period.between(LocalDate.now(), retirementDate);
        return Math.max(0, period.getYears());
    }

    /**
     * Vérifie si un personnel sera retraitable dans une année donnée
     */
    public boolean isRetirableInYear(LocalDate dateOfBirth, Integer retirementAge, int year) {
        if (dateOfBirth == null) {
            return false;
        }

        LocalDate retirementDate = calculateRetirementDate(dateOfBirth, retirementAge);
        return retirementDate != null && retirementDate.getYear() == year;
    }

    /**
     * Valide la cohérence de toutes les dates d'un personnel
     */
    public void validatePersonnelDates(Personnel personnel) {
        // Valider l'âge
        validatePersonnelAge(personnel.getDateOfBirth());

        // Valider date de recrutement
        if (personnel.getRecruitmentDate() != null) {
            validateRecruitmentDate(personnel.getDateOfBirth(), personnel.getRecruitmentDate());
            validateNotFuture(personnel.getRecruitmentDate(), "Date de recrutement");
        }

        // Valider date de première nomination
        if (personnel.getFirstAppointmentDate() != null) {
            validateAppointmentDate(personnel.getRecruitmentDate(), personnel.getFirstAppointmentDate());
            validateNotFuture(personnel.getFirstAppointmentDate(), "Date de première nomination");
        }

        // Valider date de prise de service
        if (personnel.getEffectiveDate() != null) {
            validateEffectiveDate(personnel.getFirstAppointmentDate(), personnel.getEffectiveDate());
            validateNotFuture(personnel.getEffectiveDate(), "Date de prise de service");
        }

        // Valider date de naturalisation si applicable
        if (personnel.getNaturalizationDate() != null) {
            validateNotFuture(personnel.getNaturalizationDate(), "Date de naturalisation");

            if (personnel.getNaturalizationDate().isBefore(personnel.getDateOfBirth())) {
                throw new BusinessException(
                    "La date de naturalisation ne peut pas être antérieure à la date de naissance"
                );
            }
        }

        log.debug("Validation des dates réussie pour le personnel: {}",
            personnel.getMatricule() != null ? personnel.getMatricule() : "E.C.I");
    }
}
