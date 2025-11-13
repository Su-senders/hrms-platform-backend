package com.hrms.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Format LocalDate to String (dd/MM/yyyy)
     */
    public static String format(LocalDate date) {
        return date != null ? date.format(FORMATTER) : null;
    }

    /**
     * Parse String to LocalDate (dd/MM/yyyy)
     */
    public static LocalDate parse(String dateStr) {
        return dateStr != null ? LocalDate.parse(dateStr, FORMATTER) : null;
    }

    /**
     * Calculate age from date of birth
     */
    public static int calculateAge(LocalDate dateOfBirth) {
        return dateOfBirth != null ? Period.between(dateOfBirth, LocalDate.now()).getYears() : 0;
    }

    /**
     * Calculate period between two dates
     */
    public static Period calculatePeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return Period.ZERO;
        }
        return Period.between(startDate, endDate);
    }

    /**
     * Format period as "X ans Y mois"
     */
    public static String formatPeriod(Period period) {
        if (period == null) {
            return "0 mois";
        }

        int years = period.getYears();
        int months = period.getMonths();

        if (years > 0 && months > 0) {
            return years + " ans " + months + " mois";
        } else if (years > 0) {
            return years + " ans";
        } else if (months > 0) {
            return months + " mois";
        }
        return "0 mois";
    }

    /**
     * Check if date is in current year
     */
    public static boolean isInCurrentYear(LocalDate date) {
        return date != null && date.getYear() == LocalDate.now().getYear();
    }

    /**
     * Check if date is in next year
     */
    public static boolean isInNextYear(LocalDate date) {
        return date != null && date.getYear() == LocalDate.now().getYear() + 1;
    }
}
