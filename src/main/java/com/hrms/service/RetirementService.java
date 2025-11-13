package com.hrms.service;

import com.hrms.entity.Personnel;
import com.hrms.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Service for retirement calculations and management
 * Handles automatic calculation of retirement dates and eligibility
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RetirementService {

    private final PersonnelRepository personnelRepository;
    private static final int RETIREMENT_AGE = 60;

    /**
     * Get list of personnel retirable this year
     */
    @Transactional(readOnly = true)
    public List<Personnel> getRetirableThisYear() {
        log.info("Fetching personnel retirable this year");
        return personnelRepository.findRetirableThisYear();
    }

    /**
     * Get list of personnel retirable next year
     */
    @Transactional(readOnly = true)
    public List<Personnel> getRetirableNextYear() {
        log.info("Fetching personnel retirable next year");
        return personnelRepository.findRetirableNextYear();
    }

    /**
     * Get list of personnel retirable in a specific year
     */
    @Transactional(readOnly = true)
    public List<Personnel> getRetirableInYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return personnelRepository.findByRetirementDateBetween(startDate, endDate);
    }

    /**
     * Calculate retirement date for a person
     */
    public LocalDate calculateRetirementDate(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return null;
        }
        return dateOfBirth.plusYears(RETIREMENT_AGE);
    }

    /**
     * Calculate age
     */
    public int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Check if personnel is eligible for retirement
     */
    public boolean isEligibleForRetirement(Personnel personnel) {
        if (personnel.getDateOfBirth() == null) {
            return false;
        }
        int age = calculateAge(personnel.getDateOfBirth());
        return age >= RETIREMENT_AGE;
    }

    /**
     * Calculate years until retirement
     */
    public int getYearsUntilRetirement(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return -1;
        }

        LocalDate retirementDate = calculateRetirementDate(dateOfBirth);
        Period period = Period.between(LocalDate.now(), retirementDate);

        if (period.isNegative()) {
            return 0; // Already past retirement age
        }

        return period.getYears();
    }

    /**
     * Calculate seniority in current position (years, months, days)
     */
    public Period calculateSeniorityInPosition(LocalDate serviceStartDate) {
        if (serviceStartDate == null) {
            return Period.ZERO;
        }
        return Period.between(serviceStartDate, LocalDate.now());
    }

    /**
     * Calculate seniority in administration (years, months, days)
     */
    public Period calculateSeniorityInAdministration(LocalDate hireDate) {
        if (hireDate == null) {
            return Period.ZERO;
        }
        return Period.between(hireDate, LocalDate.now());
    }

    /**
     * Format seniority period as string
     */
    public String formatSeniority(Period period) {
        if (period == null) {
            return "N/A";
        }
        return String.format("%d an(s), %d mois, %d jour(s)",
                period.getYears(), period.getMonths(), period.getDays());
    }

    /**
     * Update retirement flags for all active personnel
     * Scheduled to run daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void updateRetirementFlags() {
        log.info("Starting scheduled update of retirement flags");

        List<Personnel> allActivePersonnel = personnelRepository
                .findByStatusAndDeletedFalse(Personnel.PersonnelStatus.ACTIVE, null)
                .getContent();

        int currentYear = LocalDate.now().getYear();
        int nextYear = currentYear + 1;

        int updatedCount = 0;

        for (Personnel personnel : allActivePersonnel) {
            if (personnel.getRetirementDate() != null) {
                int retirementYear = personnel.getRetirementDate().getYear();

                boolean wasUpdated = false;

                if (personnel.getIsRetirableThisYear() != (retirementYear == currentYear)) {
                    personnel.setIsRetirableThisYear(retirementYear == currentYear);
                    wasUpdated = true;
                }

                if (personnel.getIsRetirableNextYear() != (retirementYear == nextYear)) {
                    personnel.setIsRetirableNextYear(retirementYear == nextYear);
                    wasUpdated = true;
                }

                if (wasUpdated) {
                    personnelRepository.save(personnel);
                    updatedCount++;
                }
            }
        }

        log.info("Updated retirement flags for {} personnel", updatedCount);
    }

    /**
     * Get retirement statistics
     */
    @Transactional(readOnly = true)
    public RetirementStatistics getRetirementStatistics() {
        List<Personnel> thisYear = getRetirableThisYear();
        List<Personnel> nextYear = getRetirableNextYear();

        int threeYears = getRetirableInYear(LocalDate.now().getYear() + 2).size();
        int fourYears = getRetirableInYear(LocalDate.now().getYear() + 3).size();
        int fiveYears = getRetirableInYear(LocalDate.now().getYear() + 4).size();

        return RetirementStatistics.builder()
                .retirableThisYear(thisYear.size())
                .retirableNextYear(nextYear.size())
                .retirableInThreeYears(threeYears)
                .retirableInFourYears(fourYears)
                .retirableInFiveYears(fiveYears)
                .build();
    }

    /**
     * DTO for retirement statistics
     */
    @lombok.Data
    @lombok.Builder
    public static class RetirementStatistics {
        private int retirableThisYear;
        private int retirableNextYear;
        private int retirableInThreeYears;
        private int retirableInFourYears;
        private int retirableInFiveYears;
    }
}
