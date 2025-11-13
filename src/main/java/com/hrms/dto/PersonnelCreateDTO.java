package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelCreateDTO {

    @NotBlank(message = "Le matricule est obligatoire")
    @Size(max = 50, message = "Le matricule ne peut pas dépasser 50 caractères")
    private String matricule;

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    private String middleName;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateOfBirth;

    private String placeOfBirth;

    @NotNull(message = "Le genre est obligatoire")
    private String gender; // MASCULIN, FEMININ

    private String maritalStatus;
    private String nationality;

    // CNI
    private String cniNumber;
    private LocalDate cniIssueDate;
    private LocalDate cniExpiryDate;

    // Contact
    @Pattern(regexp = "^[+]?[0-9]{9,20}$", message = "Numéro de téléphone invalide")
    private String phone;

    @Pattern(regexp = "^[+]?[0-9]{9,20}$", message = "Numéro de mobile invalide")
    private String mobile;

    @Email(message = "Email invalide")
    private String email;

    private String address;
    private String city;
    private String region;

    // Professional
    @NotNull(message = "La date d'embauche est obligatoire")
    @PastOrPresent(message = "La date d'embauche doit être dans le passé ou aujourd'hui")
    private LocalDate hireDate;

    private LocalDate serviceStartDate;

    @NotBlank(message = "Le grade est obligatoire")
    private String grade;

    private String corps;
    private String category;
    private String echelon;
    private String indice;

    // Position and Structure
    private Long currentPositionId;

    @NotNull(message = "La structure d'affectation est obligatoire")
    private Long structureId;

    // Origines géographiques
    private Long regionOrigineId;
    private Long departmentOrigineId;
    private Long arrondissementOrigineId;

    // Status
    private String status; // ACTIVE, INACTIVE, etc.
    private String situation; // EN_FONCTION, EN_STAGE, etc.

    // Retirement
    private Integer retirementAge;

    // Bank
    private String bankName;
    private String bankAccountNumber;
    private String bankBranch;

    // Other
    private String photoUrl;
    private Boolean officialCumul;
    private String notes;

    // ==================== SECTION B: QUALIFICATIONS ====================

    // --- B.1 DIPLÔME DE RECRUTEMENT ---
    @Size(max = 300, message = "L'intitulé du diplôme ne peut pas dépasser 300 caractères")
    private String recruitmentDiplomaTitle;

    private String recruitmentDiplomaType; // Enum: DiplomaType

    @PastOrPresent(message = "La date d'obtention du diplôme doit être dans le passé ou aujourd'hui")
    private LocalDate recruitmentDiplomaDate;

    @Size(max = 300, message = "Le lieu d'obtention ne peut pas dépasser 300 caractères")
    private String recruitmentDiplomaPlace;

    private String recruitmentEducationLevel; // Enum: EducationLevel

    @Size(max = 200, message = "La spécialité ne peut pas dépasser 200 caractères")
    private String recruitmentDiplomaSpecialty;

    private String recruitmentStudyField; // Enum: StudyField

    // --- B.2 DIPLÔME LE PLUS ÉLEVÉ ---
    @Size(max = 300, message = "L'intitulé du diplôme ne peut pas dépasser 300 caractères")
    private String highestDiplomaTitle;

    private String highestDiplomaType; // Enum: DiplomaType

    @PastOrPresent(message = "La date d'obtention du diplôme doit être dans le passé ou aujourd'hui")
    private LocalDate highestDiplomaDate;

    @Size(max = 300, message = "Le lieu d'obtention ne peut pas dépasser 300 caractères")
    private String highestDiplomaPlace;

    private String highestEducationLevel; // Enum: EducationLevel

    @Size(max = 200, message = "La spécialité ne peut pas dépasser 200 caractères")
    private String highestDiplomaSpecialty;

    private String highestStudyField; // Enum: StudyField
}
