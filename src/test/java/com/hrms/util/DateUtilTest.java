package com.hrms.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires pour DateUtil
 */
@DisplayName("Tests de l'utilitaire de dates")
class DateUtilTest {

    @Test
    @DisplayName("Devrait calculer correctement l'âge")
    void shouldCalculateAge() {
        // Given
        LocalDate birthDate = LocalDate.now().minusYears(30).minusMonths(5);

        // When
        int age = DateUtil.calculateAge(birthDate);

        // Then
        assertThat(age).isEqualTo(30);
    }

    @Test
    @DisplayName("Devrait calculer l'ancienneté en années")
    void shouldCalculateSeniorityInYears() {
        // Given
        LocalDate startDate = LocalDate.now().minusYears(5).minusMonths(3);

        // When
        Period period = DateUtil.calculatePeriod(startDate, LocalDate.now());

        // Then
        assertThat(period.getYears()).isEqualTo(5);
        assertThat(period.getMonths()).isEqualTo(3);
    }

    @Test
    @DisplayName("Devrait formater une date correctement")
    void shouldFormatDate() {
        // Given
        LocalDate date = LocalDate.of(2024, 1, 15);

        // When
        String formatted = DateUtil.format(date);

        // Then
        assertThat(formatted).isNotNull();
        assertThat(formatted).contains("2024");
        assertThat(formatted).contains("15/01/2024");
    }

    @Test
    @DisplayName("Devrait formater une période correctement")
    void shouldFormatPeriod() {
        // Given
        Period period = Period.of(5, 3, 0);

        // When
        String formatted = DateUtil.formatPeriod(period);

        // Then
        assertThat(formatted).isNotNull();
        assertThat(formatted).contains("5 ans");
        assertThat(formatted).contains("3 mois");
    }

    @Test
    @DisplayName("Devrait vérifier si une date est dans l'année courante")
    void shouldCheckIfDateIsInCurrentYear() {
        // Given
        LocalDate currentYearDate = LocalDate.now();
        LocalDate pastYearDate = LocalDate.now().minusYears(1);

        // When
        boolean isCurrent = DateUtil.isInCurrentYear(currentYearDate);
        boolean isPast = DateUtil.isInCurrentYear(pastYearDate);

        // Then
        assertThat(isCurrent).isTrue();
        assertThat(isPast).isFalse();
    }
}

