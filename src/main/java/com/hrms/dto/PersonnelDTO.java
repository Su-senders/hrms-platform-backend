package com.hrms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelDTO {
    private Long id;
    private String matricule;

    // Personal Information
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String gender;
    private String maritalStatus;
    private String nationality;

    // CNI
    private String cniNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cniIssueDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cniExpiryDate;

    // Contact
    private String phone;
    private String mobile;
    private String email;
    private String address;
    private String city;
    private String region;

    // Professional
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate serviceStartDate;
    private String grade;
    private String corps;
    private String category;
    private String echelon;
    private String indice;

    // Position and Structure
    private Long currentPositionId;
    private String currentPositionTitle;
    private Long structureId;
    private String structureName;

    // Status
    private String status;
    private String situation;

    // Retirement
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate retirementDate;
    private Integer retirementAge;
    private Boolean isRetirableThisYear;
    private Boolean isRetirableNextYear;

    // Bank
    private String bankName;
    private String bankAccountNumber;
    private String bankBranch;

    // Other
    private String photoUrl;
    private Boolean officialCumul;
    private String notes;

    // Calculated fields
    private Integer age;
    private String seniorityInPost; // Format: "X ans Y mois"
    private String seniorityInAdministration; // Format: "X ans Y mois"

    // ==================== SECTION B: QUALIFICATIONS ====================

    // --- B.1 DIPLÔME DE RECRUTEMENT ---
    private String recruitmentDiplomaTitle;
    private String recruitmentDiplomaType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitmentDiplomaDate;
    private String recruitmentDiplomaPlace;
    private String recruitmentEducationLevel;
    private String recruitmentDiplomaSpecialty;
    private String recruitmentStudyField;

    // --- B.2 DIPLÔME LE PLUS ÉLEVÉ ---
    private String highestDiplomaTitle;
    private String highestDiplomaType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate highestDiplomaDate;
    private String highestDiplomaPlace;
    private String highestEducationLevel;
    private String highestDiplomaSpecialty;
    private String highestStudyField;

    // Audit fields
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate createdDate;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate updatedDate;
}
