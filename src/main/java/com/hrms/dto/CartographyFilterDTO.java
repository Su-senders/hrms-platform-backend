package com.hrms.dto;

import lombok.*;

/**
 * DTO pour les filtres de cartographie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartographyFilterDTO {
    
    // Filtres par structure
    private Long structureId;
    private String structureType; // MINISTERE, DIRECTION, SERVICE, etc.
    private Boolean includeChildren; // Inclure les structures enfants
    
    // Filtres par poste
    private Long positionId;
    private String positionStatus; // VACANT, OCCUPE
    private String rank;
    private String category;
    
    // Filtres par personnel
    private Long personnelId;
    private String grade;
    private String corps;
    private String situation; // EN_FONCTION, EN_STAGE, etc.
    private String status; // ACTIVE, RETIRED, etc.
    
    // Filtres géographiques
    private Long regionId;
    private Long departmentId;
    
    // Options d'affichage
    private Boolean onlyOccupied; // Afficher uniquement les postes occupés
    private Boolean onlyVacant; // Afficher uniquement les postes vacants
    private Boolean includeEmptyStructures; // Inclure les structures sans postes
    private Boolean hierarchical; // Vue hiérarchique (parent → enfants)
}




