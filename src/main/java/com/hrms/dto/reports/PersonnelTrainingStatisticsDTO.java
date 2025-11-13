package com.hrms.dto.reports;

import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO pour les statistiques de formation d'un personnel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelTrainingStatisticsDTO {
    private Long personnelId;
    private String matricule;
    private String fullName;
    private Integer year;
    private Integer totalTrainings;
    private Integer totalDays;
    private BigDecimal totalCost;
    private BigDecimal averageCostPerTraining;
    private Map<String, Long> trainingsByField;
    private Integer certificationsObtained;
}
