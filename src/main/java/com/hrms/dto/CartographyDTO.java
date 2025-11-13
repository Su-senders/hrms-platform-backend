package com.hrms.dto;

import lombok.*;
import java.util.List;

/**
 * DTO pour la cartographie des personnels par structure et poste
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartographyDTO {
    
    /**
     * Informations sur la structure
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StructureInfo {
        private Long id;
        private String code;
        private String name;
        private String type;
        private Long parentStructureId;
        private String parentStructureName;
        private Integer totalPositions;
        private Integer occupiedPositions;
        private Integer vacantPositions;
        private Integer totalPersonnel;
    }
    
    /**
     * Informations sur un poste
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PositionInfo {
        private Long id;
        private String code;
        private String title;
        private String rank;
        private String category;
        private String status;
        private String requiredGrade;
        private String requiredCorps;
        private PersonnelInfo personnel;
    }
    
    /**
     * Informations sur le personnel
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonnelInfo {
        private Long id;
        private String matricule;
        private String fullName;
        private String grade;
        private String corps;
        private String situation;
        private String status;
        private Integer age;
        private String seniorityInPost;
        private String seniorityInAdministration;
    }
    
    /**
     * Structure avec ses postes et personnels
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StructureMapping {
        private StructureInfo structure;
        private List<PositionMapping> positions;
        private List<StructureMapping> children; // Structures enfants
    }
    
    /**
     * Poste avec son personnel
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PositionMapping {
        private PositionInfo position;
        private PersonnelInfo personnel;
    }
    
    /**
     * Statistiques globales
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Statistics {
        private Integer totalStructures;
        private Integer totalPositions;
        private Integer totalOccupiedPositions;
        private Integer totalVacantPositions;
        private Integer totalPersonnel;
        private Integer totalPersonnelWithPosition;
        private Integer totalPersonnelWithoutPosition;
    }
    
    // Données principales
    private Statistics statistics;
    private List<StructureMapping> structures;
    
    // Filtres appliqués
    private CartographyFilterDTO filters;
}




