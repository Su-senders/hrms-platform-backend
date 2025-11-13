package com.hrms.bootstrap.loader.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Modèle de données pour les structures territoriales du Cameroun
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerritorialData {
    private List<RegionData> regions;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionData {
    private String code;
    private String name;
    private String region;
    private String chefLieu;
    private List<DepartmentData> departments;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentData {
    private String name;
    private String chefLieu;
    private List<ArrondissementData> arrondissements;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArrondissementData {
    private String name;
    private String chefLieu;
}

