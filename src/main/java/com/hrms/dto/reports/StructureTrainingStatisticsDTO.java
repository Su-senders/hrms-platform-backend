package com.hrms.dto.reports;

import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO pour les statistiques de formation par structure
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructureTrainingStatisticsDTO {
    private Long structureId;
    private String structureName;
    private String structureCode;
    private Integer year;
    private Integer totalPersonnelTrained;
    private Integer totalTrainingSessions;
    private Integer totalTrainingDays;
    private BigDecimal totalCost;
    private BigDecimal averageCostPerPersonnel;
    private Map<String, Long> trainingsByField;
    private Integer totalCertificationsObtained;
}
