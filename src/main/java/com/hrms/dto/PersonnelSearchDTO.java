package com.hrms.dto;

import lombok.*;

/**
 * DTO for personnel search criteria
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelSearchDTO {
    private String searchTerm; // Search in name, matricule
    private String matricule;
    private String firstName;
    private String lastName;
    private String grade;
    private String corps;
    private String category;
    private Long positionId;
    private String positionTitle;
    private Long structureId;
    private String structureName;
    private String status;
    private String situation;
    private Boolean isRetirableThisYear;
    private Boolean isRetirableNextYear;
    private Integer minAge;
    private Integer maxAge;
}
