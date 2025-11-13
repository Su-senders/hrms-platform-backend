package com.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO pour les statistiques globales de personnels
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelStatisticsDTO {

    // ==================== EFFECTIFS ====================

    /**
     * Effectif total
     */
    private Long totalPersonnel;

    /**
     * Effectif actif (situation administrative ACTIVE)
     */
    private Long activePersonnel;

    /**
     * Effectif inactif
     */
    private Long inactivePersonnel;

    /**
     * Personnels E.C.I (sans matricule)
     */
    private Long eciPersonnel;

    // ==================== RÉPARTITION PAR GENRE ====================

    private Long maleCount;
    private Long femaleCount;
    private Double malePercentage;
    private Double femalePercentage;

    // ==================== RÉPARTITION PAR SITUATION ADMINISTRATIVE ====================

    /**
     * Répartition par situation administrative
     * Map<SituationAdministrative, Count>
     */
    private Map<String, Long> byAdministrativeStatus;

    // ==================== RÉPARTITION PAR STATUT ====================

    /**
     * Répartition par statut (TITULAIRE, CONTRACTUEL, STAGIAIRE, etc.)
     * Map<PersonnelStatus, Count>
     */
    private Map<String, Long> byPersonnelStatus;

    // ==================== RÉPARTITION PAR CORPS ET GRADE ====================

    /**
     * Répartition par corps
     * Map<CorpsName, Count>
     */
    private Map<String, Long> byCorps;

    /**
     * Répartition par grade
     * Map<GradeName, Count>
     */
    private Map<String, Long> byGrade;

    /**
     * Répartition par catégorie (A, B, C)
     * Map<Category, Count>
     */
    private Map<String, Long> byCategory;

    // ==================== RÉPARTITION GÉOGRAPHIQUE ====================

    /**
     * Répartition par région d'origine
     * Map<RegionName, Count>
     */
    private Map<String, Long> byRegionOrigine;

    /**
     * Répartition par structure d'affectation
     * Map<StructureName, Count>
     */
    private Map<String, Long> byStructure;

    // ==================== ÂGE ET ANCIENNETÉ ====================

    /**
     * Âge moyen
     */
    private Double averageAge;

    /**
     * Ancienneté moyenne (en années)
     */
    private Double averageSeniority;

    /**
     * Répartition par tranche d'âge
     * Map<Tranche, Count> Ex: "20-30", "31-40", etc.
     */
    private Map<String, Long> byAgeGroup;

    /**
     * Répartition par tranche d'ancienneté
     * Map<Tranche, Count> Ex: "0-5", "6-10", etc.
     */
    private Map<String, Long> bySeniorityGroup;

    // ==================== RETRAITE ====================

    /**
     * Nombre de personnels retraitables actuellement
     */
    private Long retirableNow;

    /**
     * Nombre de personnels retraitables dans l'année en cours
     */
    private Long retirableThisYear;

    /**
     * Nombre de personnels retraitables dans les 5 prochaines années
     */
    private Long retirableNextFiveYears;

    /**
     * Répartition des retraites par année (5 prochaines années)
     * Map<Year, Count>
     */
    private Map<Integer, Long> retirementByYear;

    // ==================== POSTES ====================

    /**
     * Nombre de personnels avec poste affecté
     */
    private Long personnelWithPosition;

    /**
     * Nombre de personnels sans poste
     */
    private Long personnelWithoutPosition;

    // ==================== MÉTADONNÉES ====================

    /**
     * Date de génération des statistiques
     */
    private String generatedAt;

    /**
     * Période couverte (si applicable)
     */
    private String period;
}
