package com.hrms.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingUpdateDTO {
    private String title;
    private String description;
    private String trainingField;
    private Integer durationDays;
    private String objectives;
    private String prerequisites;
    private String content;
    private String pedagogicalMethods;
    private String category;
    private String requiredLevel;
    private BigDecimal standardCostPerParticipant;
    private Integer minParticipants;
    private Integer maxParticipants;
    private Boolean active;
}




