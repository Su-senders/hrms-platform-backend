package com.hrms.bootstrap.loader.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Structure de données pour charger les templates depuis JSON
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateData {
    private String code;
    private String name;
    private String appliesTo;
    private String description;
    private Integer version;
    private List<SubStructureData> subStructures;
    private List<PositionData> topLevelPositions;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SubStructureData {
    private String code;
    private String name;
    private String type;
    private String description;
    private Integer level;
    private List<PositionData> positions;
    private List<SubStructureData> subServices; // Pour les services rattachés (ex: services du Cabinet)
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PositionData {
    private String code;
    private String title;
    private Integer level;
    private String parentPositionCode;
    private Integer count;
    private String requiredGrade;
    private String requiredCorps;
    private Integer minimumExperienceYears;
    private Boolean isNominative;
    private Boolean isManagerial;
    private String category;
    private String description;
    private String responsibilities;
    private String requiredQualifications;
}
