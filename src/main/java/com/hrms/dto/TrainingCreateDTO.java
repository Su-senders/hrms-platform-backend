package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingCreateDTO {
    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String description;

    @NotBlank(message = "Le domaine de formation est obligatoire")
    private String trainingField;

    @NotNull(message = "La durée est obligatoire")
    @Positive(message = "La durée doit être positive")
    private Integer durationDays;

    private String objectives;
    private String prerequisites;
    private String content;
    private String pedagogicalMethods;

    @NotNull(message = "La catégorie est obligatoire")
    private String category;

    private String requiredLevel;
    private BigDecimal standardCostPerParticipant;
    private Integer minParticipants;
    private Integer maxParticipants;
    private Boolean active = true;
}




