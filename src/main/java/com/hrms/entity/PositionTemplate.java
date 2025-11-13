package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Modèle de poste prédéfini
 * Définit les postes types qui peuvent être créés dans différentes structures
 */
@Entity
@Table(name = "position_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionTemplate extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "title", nullable = false)
    private String title; // Intitulé du poste

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "rank")
    private String rank; // Rang du poste

    @Column(name = "category")
    private String category; // Catégorie (A, B, C, etc.)

    @Column(name = "required_grade")
    private String requiredGrade; // Grade requis

    @Column(name = "required_corps")
    private String requiredCorps; // Corps requis

    @Column(name = "responsibilities", columnDefinition = "TEXT")
    private String responsibilities;

    @Column(name = "required_qualifications", columnDefinition = "TEXT")
    private String requiredQualifications;

    @Column(name = "min_experience_years")
    private Integer minExperienceYears;

    @Column(name = "is_managerial")
    private Boolean isManagerial = false;

    @Column(name = "is_nominative")
    private Boolean isNominative = false; // Poste nominatif (Gouverneur, Préfet, etc.)

    @Column(name = "applicable_to_structure_type")
    @Enumerated(EnumType.STRING)
    private ApplicableStructureType applicableStructureType;

    @Column(name = "is_unique_per_structure")
    private Boolean isUniquePerStructure = false; // Un seul poste de ce type par structure

    @Column(name = "auto_create")
    private Boolean autoCreate = false; // Créer automatiquement lors de la création d'une structure

    @Column(name = "active")
    private Boolean active = true;

    public enum ApplicableStructureType {
        MINISTERE_ONLY,        // Uniquement au niveau ministère (Ministre, SG, etc.)
        GOUVERNORAT_ONLY,      // Uniquement dans les Gouvernorats (Gouverneur, etc.)
        PREFECTURE_ONLY,       // Uniquement dans les Préfectures (Préfet, etc.)
        SOUS_PREFECTURE_ONLY,  // Uniquement dans les Sous-Préfectures (Sous-Préfet, etc.)
        DIRECTION_ONLY,        // Uniquement dans les Directions
        SERVICE_ONLY,          // Uniquement dans les Services
        ALL_STRUCTURES,        // Dans toutes les structures
        TERRITORIAL_ONLY       // Dans toutes les structures territoriales (Gouv, Préf, S-Préf)
    }

    /**
     * Check if this template is applicable to a structure type
     */
    public boolean isApplicableTo(AdministrativeStructure.StructureType structureType) {
        if (applicableStructureType == ApplicableStructureType.ALL_STRUCTURES) {
            return true;
        }

        switch (applicableStructureType) {
            case MINISTERE_ONLY:
                return structureType == AdministrativeStructure.StructureType.MINISTERE;
            case GOUVERNORAT_ONLY:
                return structureType == AdministrativeStructure.StructureType.GOUVERNORAT;
            case PREFECTURE_ONLY:
                return structureType == AdministrativeStructure.StructureType.PREFECTURE;
            case SOUS_PREFECTURE_ONLY:
                return structureType == AdministrativeStructure.StructureType.SOUS_PREFECTURE;
            case DIRECTION_ONLY:
                return structureType == AdministrativeStructure.StructureType.DIRECTION;
            case SERVICE_ONLY:
                return structureType == AdministrativeStructure.StructureType.SERVICE;
            case TERRITORIAL_ONLY:
                return structureType == AdministrativeStructure.StructureType.GOUVERNORAT ||
                       structureType == AdministrativeStructure.StructureType.PREFECTURE ||
                       structureType == AdministrativeStructure.StructureType.SOUS_PREFECTURE;
            default:
                return false;
        }
    }
}
