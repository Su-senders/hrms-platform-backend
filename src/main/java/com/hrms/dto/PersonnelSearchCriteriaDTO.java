package com.hrms.dto;

import com.hrms.entity.Personnel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour la recherche avancée multicritère de personnels
 * Permet de combiner plusieurs critères pour des recherches complexes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelSearchCriteriaDTO {

    // ==================== IDENTIFICATION ====================

    /**
     * Recherche par matricule (partiel ou complet)
     */
    private String matricule;

    /**
     * Recherche par nom (partiel, insensible à la casse)
     */
    private String lastName;

    /**
     * Recherche par prénom (partiel, insensible à la casse)
     */
    private String firstName;

    /**
     * Recherche par numéro CNI
     */
    private String cniNumber;

    /**
     * Recherche globale (nom OU prénom OU matricule)
     */
    private String globalSearch;

    // ==================== CARACTÉRISTIQUES PERSONNELLES ====================

    /**
     * Genre (MALE, FEMALE)
     */
    private Personnel.Gender gender;

    /**
     * État civil (SINGLE, MARRIED, DIVORCED, WIDOWED)
     */
    private Personnel.MaritalStatus maritalStatus;

    /**
     * Âge minimum
     */
    private Integer minAge;

    /**
     * Âge maximum
     */
    private Integer maxAge;

    /**
     * Date de naissance minimum
     */
    private LocalDate dateOfBirthFrom;

    /**
     * Date de naissance maximum
     */
    private LocalDate dateOfBirthTo;

    // ==================== ORIGINE GÉOGRAPHIQUE ====================

    /**
     * Région d'origine (ID)
     */
    private Long regionOrigineId;

    /**
     * Département d'origine (ID)
     */
    private Long departmentOrigineId;

    /**
     * Arrondissement d'origine (ID)
     */
    private Long arrondissementOrigineId;

    // ==================== AFFECTATION ACTUELLE ====================

    /**
     * Structure actuelle (ID)
     */
    private Long structureId;

    /**
     * Inclure les sous-structures dans la recherche
     */
    private Boolean includeSubStructures = false;

    /**
     * Poste actuel (ID)
     */
    private Long currentPositionId;

    /**
     * Type de poste
     */
    private String positionType;

    // ==================== CORPS ET GRADE ====================

    /**
     * Corps (ID)
     */
    private Long corpsId;

    /**
     * Grade (ID)
     */
    private Long gradeId;

    /**
     * Échelon minimum
     */
    private String minEchelon;

    /**
     * Échelon maximum
     */
    private String maxEchelon;

    /**
     * Catégorie (A, B, C)
     */
    private String category;

    // ==================== SITUATION ADMINISTRATIVE ====================

    /**
     * Situation administrative (liste)
     * Ex: ACTIVE, ON_LEAVE, RETIRED, SUSPENDED, etc.
     */
    private List<Personnel.AdministrativeStatus> administrativeStatus;

    /**
     * Type de recrutement
     */
    private Personnel.RecruitmentType recruitmentType;

    /**
     * Statut personnel (TITULAIRE, CONTRACTUEL, STAGIAIRE, etc.)
     */
    private Personnel.PersonnelStatus personnelStatus;

    // ==================== DATES DE CARRIÈRE ====================

    /**
     * Date de recrutement minimum
     */
    private LocalDate recruitmentDateFrom;

    /**
     * Date de recrutement maximum
     */
    private LocalDate recruitmentDateTo;

    /**
     * Date de première nomination minimum
     */
    private LocalDate firstAppointmentDateFrom;

    /**
     * Date de première nomination maximum
     */
    private LocalDate firstAppointmentDateTo;

    // ==================== ANCIENNETÉ ====================

    /**
     * Ancienneté minimum (en années)
     */
    private Integer minSeniorityYears;

    /**
     * Ancienneté maximum (en années)
     */
    private Integer maxSeniorityYears;

    /**
     * Ancienneté dans le grade minimum (en années)
     */
    private Integer minGradeSeniorityYears;

    /**
     * Ancienneté dans le grade maximum (en années)
     */
    private Integer maxGradeSeniorityYears;

    // ==================== RETRAITE ====================

    /**
     * Personnels retraitables (ont atteint l'âge de retraite)
     */
    private Boolean retirable;

    /**
     * Personnels retraitables dans une année spécifique
     */
    private Integer retirableInYear;

    /**
     * Personnels retraitables dans les X prochaines années
     */
    private Integer retirableWithinYears;

    // ==================== FONCTIONS ET RESPONSABILITÉS ====================

    /**
     * Responsable hiérarchique (Boolean)
     */
    private Boolean isHierarchicalSupervisor;

    /**
     * Fonction actuelle (partiel)
     */
    private String currentFunction;

    /**
     * Cumul officiel autorisé
     */
    private Boolean officialCumul;

    // ==================== SITUATION PARTICULIÈRE ====================

    /**
     * Personnel en détachement
     */
    private Boolean onSecondment;

    /**
     * Personnel en position de stage
     */
    private Boolean onProbation;

    /**
     * Personnels E.C.I (En Cours d'Intégration - sans matricule)
     */
    private Boolean eci;

    // ==================== CONTACT ====================

    /**
     * Email (partiel)
     */
    private String email;

    /**
     * Téléphone (partiel)
     */
    private String phoneNumber;

    // ==================== OPTIONS DE TRI ====================

    /**
     * Champ de tri (lastName, firstName, matricule, recruitmentDate, etc.)
     */
    private String sortBy;

    /**
     * Direction du tri (ASC, DESC)
     */
    private String sortDirection;

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Vérifie si au moins un critère est spécifié
     */
    public boolean hasAnyCriteria() {
        return matricule != null ||
               lastName != null ||
               firstName != null ||
               cniNumber != null ||
               globalSearch != null ||
               gender != null ||
               maritalStatus != null ||
               minAge != null ||
               maxAge != null ||
               dateOfBirthFrom != null ||
               dateOfBirthTo != null ||
               regionOrigineId != null ||
               departmentOrigineId != null ||
               arrondissementOrigineId != null ||
               structureId != null ||
               currentPositionId != null ||
               positionType != null ||
               corpsId != null ||
               gradeId != null ||
               minEchelon != null ||
               maxEchelon != null ||
               category != null ||
               (administrativeStatus != null && !administrativeStatus.isEmpty()) ||
               recruitmentType != null ||
               personnelStatus != null ||
               recruitmentDateFrom != null ||
               recruitmentDateTo != null ||
               firstAppointmentDateFrom != null ||
               firstAppointmentDateTo != null ||
               minSeniorityYears != null ||
               maxSeniorityYears != null ||
               minGradeSeniorityYears != null ||
               maxGradeSeniorityYears != null ||
               retirable != null ||
               retirableInYear != null ||
               retirableWithinYears != null ||
               isHierarchicalSupervisor != null ||
               currentFunction != null ||
               officialCumul != null ||
               onSecondment != null ||
               onProbation != null ||
               eci != null ||
               email != null ||
               phoneNumber != null;
    }
}
