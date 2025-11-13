package com.hrms.dto.reports;

import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO pour les statistiques globales de formation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalTrainingStatisticsDTO {
    private Integer year;
    private Integer totalTrainingsInCatalog;
    private Integer totalSessionsHeld;
    private Integer totalPersonnelTrained;
    private Integer totalTrainingDays;
    private BigDecimal totalCost;
    private BigDecimal averageCostPerSession;
    private BigDecimal averageCostPerPersonnel;
    private Map<String, Long> sessionsByField;
    private Map<String, Long> personnelByField;
    private Integer totalCertificationsIssued;
    private Integer totalActiveTrainers;
}
