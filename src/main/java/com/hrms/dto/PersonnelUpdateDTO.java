package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelUpdateDTO {

    @Size(max = 50, message = "Le matricule ne peut pas dépasser 50 caractères")
    private String matricule;

    private String firstName;
    private String lastName;
    private String middleName;

    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateOfBirth;

    private String placeOfBirth;
    private String gender;
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
    @PastOrPresent(message = "La date d'embauche doit être dans le passé ou aujourd'hui")
    private LocalDate hireDate;

    private LocalDate serviceStartDate;
    private String grade;
    private String corps;
    private String category;
    private String echelon;
    private String indice;

    // Position and Structure
    private Long currentPositionId;
    private Long structureId;

    // Status
    private String status;
    private String situation;

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
