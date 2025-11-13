package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionUpdateDTO {

    @Size(max = 50, message = "Le code ne peut pas dépasser 50 caractères")
    private String code;

    private String title;
    private String description;
    private Long structureId;
    private String rank;
    private String category;
    private String requiredGrade;
    private String requiredCorps;
    private String status;
    private String responsibilities;
    private String requiredQualifications;

    @Min(value = 0, message = "L'expérience minimale doit être positive")
    private Integer minExperienceYears;

    private Boolean isManagerial;
    private Long reportsToPositionId;
    private String budgetCode;
    private Boolean active;
}
