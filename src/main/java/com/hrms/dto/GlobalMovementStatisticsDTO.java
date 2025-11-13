package com.hrms.dto;

import lombok.*;
import java.util.Map;

/**
 * DTO pour les statistiques globales de mouvements de carrière
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalMovementStatisticsDTO {
    private Integer year;
    private Long totalMovements;
    private Long pendingMovements;
    private Long approvedMovements;
    private Long executedMovements;
    private Long cancelledMovements;
    private Long rejectedMovements;
    
    // Répartition par type
    private Map<String, Long> movementsByType;
    
    // Répartition par statut
    private Map<String, Long> movementsByStatus;
    
    // Répartition par mois
    private Map<String, Long> movementsByMonth;
    
    // Délai moyen de traitement (en jours)
    private Double averageProcessingDays;
    
    // Mouvements par trimestre
    private Map<String, Long> movementsByQuarter;
    
    // Évolution mensuelle
    private Map<String, Long> monthlyEvolution;
}

