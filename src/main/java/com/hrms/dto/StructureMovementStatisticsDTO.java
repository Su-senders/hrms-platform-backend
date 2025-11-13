package com.hrms.dto;

import lombok.*;
import java.util.Map;

/**
 * DTO pour les statistiques de mouvements par structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StructureMovementStatisticsDTO {
    private Long structureId;
    private String structureName;
    private String structureCode;
    private Integer year;
    
    // Mouvements entrants (vers cette structure)
    private Long incomingMovements;
    
    // Mouvements sortants (depuis cette structure)
    private Long outgoingMovements;
    
    // Total mouvements (entrants + sortants)
    private Long totalMovements;
    
    // Répartition par type
    private Map<String, Long> movementsByType;
    
    // Répartition par statut
    private Map<String, Long> movementsByStatus;
    
    // Taux de rotation (mouvements / effectif moyen)
    private Double rotationRate;
    
    // Durée moyenne d'affectation (en jours)
    private Double averageAssignmentDuration;
    
    // Mouvements entrants par type
    private Map<String, Long> incomingByType;
    
    // Mouvements sortants par type
    private Map<String, Long> outgoingByType;
}

