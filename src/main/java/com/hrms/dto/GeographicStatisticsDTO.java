package com.hrms.dto;

import lombok.*;
import java.util.Map;

/**
 * DTO pour les statistiques géographiques
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeographicStatisticsDTO {
    // Statistiques globales
    private Long totalRegions;
    private Long totalDepartments;
    private Long totalArrondissements;
    private Long activeRegions;
    private Long activeDepartments;
    private Long activeArrondissements;
    
    // Répartition du personnel
    private Map<String, Long> personnelByRegion;
    private Map<String, Long> personnelByDepartment;
    private Map<String, Long> personnelByArrondissement;

    /**
     * Statistiques par région
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegionStatisticsDTO {
        private Long regionId;
        private String regionName;
        private String regionCode;
        private Long nombreDepartements;
        private Long nombreArrondissements;
        private Long nombrePersonnel;
    }

    /**
     * Statistiques par département
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepartmentStatisticsDTO {
        private Long departmentId;
        private String departmentName;
        private String departmentCode;
        private Long regionId;
        private String regionName;
        private Long nombreArrondissements;
        private Long nombrePersonnel;
    }

    /**
     * Statistiques par arrondissement
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ArrondissementStatisticsDTO {
        private Long arrondissementId;
        private String arrondissementName;
        private String arrondissementCode;
        private Long departmentId;
        private String departmentName;
        private Long regionId;
        private String regionName;
        private Long nombrePersonnel;
    }
}

