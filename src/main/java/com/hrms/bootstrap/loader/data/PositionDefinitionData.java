package com.hrms.bootstrap.loader.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Modèle de données pour les définitions de postes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionDefinitionData {
    private List<PositionDefinition> positionDefinitions;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PositionDefinition {
    private String code;
    private String title;
    private String description;
    private String rank;
    private String category;
    private String requiredGrade;
    private String requiredCorps;
    private Boolean isManagerial;
    private Boolean isNominative;
    private Boolean isUniquePerStructure;
    private Boolean autoCreate;
    private String applicableStructureType;
    private String structureCode; // Code exact de la structure
    private String structurePattern; // Pattern regex pour matcher les structures
}

