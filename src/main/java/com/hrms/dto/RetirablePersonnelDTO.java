package com.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour les informations de personnel retraitable
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetirablePersonnelDTO {

    private Long id;
    private String matricule;
    private String lastName;
    private String firstName;
    private String fullName;

    // Date de naissance et âge
    private LocalDate dateOfBirth;
    private Integer currentAge;

    // Retraite
    private LocalDate retirementDate;
    private Integer retirementAge;
    private Integer yearsUntilRetirement;
    private Integer monthsUntilRetirement;
    private Integer daysUntilRetirement;
    private Boolean isRetirable; // A déjà atteint l'âge de retraite

    // Affectation actuelle
    private String structureName;
    private Long structureId;
    private String positionTitle;
    private Long positionId;

    // Grade et corps
    private String gradeName;
    private Long gradeId;
    private String corpsName;
    private Long corpsId;

    // Ancienneté
    private String globalSeniorityFormatted;
    private Double globalSeniorityYears;

    // Contact
    private String email;
    private String phoneNumber;

    // Origine géographique
    private String regionOrigineName;
    private String departmentOrigineName;
    private String arrondissementOrigineName;
}
