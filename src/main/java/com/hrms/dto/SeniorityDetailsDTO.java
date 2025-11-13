package com.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO contenant tous les détails d'ancienneté d'un personnel
 * Fournit des calculs précis pour la gestion de carrière
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeniorityDetailsDTO {

    // ==================== ANCIENNETÉ GLOBALE (depuis recrutement) ====================

    /**
     * Ancienneté globale en années
     */
    private Integer globalSeniorityYears;

    /**
     * Ancienneté globale en mois (partie mois après les années)
     */
    private Integer globalSeniorityMonths;

    /**
     * Ancienneté globale en jours (partie jours après les mois)
     */
    private Integer globalSeniorityDays;

    /**
     * Ancienneté globale formatée (ex: "5 ans, 3 mois, 12 jours")
     */
    private String globalSeniorityFormatted;

    /**
     * Ancienneté globale totale en jours
     */
    private Long globalSeniorityTotalDays;

    /**
     * Ancienneté globale en années décimales (pour calculs précis)
     */
    private Double globalSeniorityDecimal;

    // ==================== ANCIENNETÉ DANS LE GRADE ACTUEL ====================

    /**
     * Ancienneté dans le grade actuel en années
     */
    private Integer gradeSeniorityYears;

    /**
     * Ancienneté dans le grade actuel en mois
     */
    private Integer gradeSeniorityMonths;

    /**
     * Ancienneté dans le grade actuel en jours
     */
    private Integer gradeSeniorityDays;

    /**
     * Ancienneté dans le grade formatée
     */
    private String gradeSeniorityFormatted;

    /**
     * Ancienneté dans le grade totale en jours
     */
    private Long gradeSeniorityTotalDays;

    // ==================== ANCIENNETÉ DANS L'ÉCHELON ACTUEL ====================

    /**
     * Ancienneté dans l'échelon actuel en années
     */
    private Integer echelonSeniorityYears;

    /**
     * Ancienneté dans l'échelon actuel en mois
     */
    private Integer echelonSeniorityMonths;

    /**
     * Ancienneté dans l'échelon actuel en jours
     */
    private Integer echelonSeniorityDays;

    /**
     * Ancienneté dans l'échelon formatée
     */
    private String echelonSeniorityFormatted;

    /**
     * Ancienneté dans l'échelon totale en jours
     */
    private Long echelonSeniorityTotalDays;

    // ==================== ANCIENNETÉ DANS LE POSTE ACTUEL ====================

    /**
     * Ancienneté dans le poste actuel en années
     */
    private Integer positionSeniorityYears;

    /**
     * Ancienneté dans le poste actuel en mois
     */
    private Integer positionSeniorityMonths;

    /**
     * Ancienneté dans le poste actuel en jours
     */
    private Integer positionSeniorityDays;

    /**
     * Ancienneté dans le poste formatée
     */
    private String positionSeniorityFormatted;

    /**
     * Ancienneté dans le poste totale en jours
     */
    private Long positionSeniorityTotalDays;

    // ==================== ÂGE ET RETRAITE ====================

    /**
     * Âge actuel en années
     */
    private Integer currentAge;

    /**
     * Âge actuel formaté
     */
    private String currentAgeFormatted;

    /**
     * Date de retraite estimée
     */
    private LocalDate retirementDate;

    /**
     * Nombre d'années avant la retraite
     */
    private Integer yearsUntilRetirement;

    /**
     * Nombre de mois avant la retraite (partie mois)
     */
    private Integer monthsUntilRetirement;

    /**
     * Nombre de jours avant la retraite (partie jours)
     */
    private Integer daysUntilRetirement;

    /**
     * Indique si le personnel est retraitable (a atteint l'âge de retraite)
     */
    private Boolean isRetirable;
}
