package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionCreateDTO {

    @NotBlank(message = "Le code du poste est obligatoire")
    @Size(max = 50, message = "Le code ne peut pas dépasser 50 caractères")
    private String code;

    @NotBlank(message = "L'intitulé du poste est obligatoire")
    private String title;

    private String description;

    @NotNull(message = "La structure est obligatoire")
    private Long structureId;

    private String rank;
    private String category;
    private String requiredGrade;
    private String requiredCorps;
    private String status; // VACANT, OCCUPE, EN_CREATION, SUPPRIME
    private String responsibilities;
    private String requiredQualifications;

    @Min(value = 0, message = "L'expérience minimale doit être positive")
    private Integer minExperienceYears;

    private Boolean isManagerial;
    private Long reportsToPositionId;
    private String budgetCode;
    private Boolean active;
}
