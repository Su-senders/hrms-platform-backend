package com.hrms.bootstrap.loader.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Modèle de données pour les structures administratives
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdministrativeStructureData {
    private OrganizationData organization;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationData {
    private String code;
    private String name;
    private String type;
    private String description;
    private List<StructureData> structures;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructureData {
    private String code;
    private String name;
    private String type;
    private String description;
    private List<StructureData> structures;
}

