package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Template de poste pour les templates organisationnels.
 * Définit les postes qui seront automatiquement créés lors de l'instanciation
 * d'un template organisationnel (Gouvernorat, Préfecture, Sous-Préfecture).
 *
 * Exemple:
 * - OrganizationalPositionTemplate "Gouverneur" → 10 postes créés (1 par Gouvernorat)
 * - OrganizationalPositionTemplate "Chef de Cabinet" → 10 postes créés (1 par Gouvernorat)
 */
@Entity
@Table(name = "organizational_position_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationalPositionTemplate extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 100)
    private String code; // "GOUV-GOUVERNEUR", "GOUV-CHEF-CABINET"

    @Column(name = "title", nullable = false, length = 200)
    private String title; // "Gouverneur", "Chef de Cabinet du Gouverneur"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizational_template_id", nullable = false)
    private OrganizationalTemplate organizationalTemplate; // Template organisationnel parent

    @Column(name = "sub_structure_code")
    private String subStructureCode; // Code de la sous-structure dans le template (ex: "GOUV-CABINET")

    @Column(name = "level")
    private Integer level; // Niveau hiérarchique (1=plus haut, 2, 3, etc.)

    @Column(name = "parent_position_code")
    private String parentPositionCode; // Code du poste parent pour recréer la hiérarchie

    @Column(name = "count")
    private Integer count = 1; // Nombre de postes de ce type à créer

    @Column(name = "required_grade", length = 50)
    private String requiredGrade; // Grade requis (A1, A2, B1, etc.)

    @Column(name = "required_corps", length = 100)
    private String requiredCorps; // Corps requis

    @Column(name = "minimum_experience_years")
    private Integer minimumExperienceYears;

    @Column(name = "is_nominative")
    private Boolean isNominative = false; // Poste nominatif (Gouverneur, Préfet, etc.)

    @Column(name = "is_managerial")
    private Boolean isManagerial = false; // Poste de management

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private PositionCategory category; // DIRECTION, TECHNIQUE, ADMINISTRATIF, SUPPORT

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "responsibilities", columnDefinition = "TEXT")
    private String responsibilities;

    @Column(name = "required_qualifications", columnDefinition = "TEXT")
    private String requiredQualifications;

    @Column(name = "active")
    private Boolean active = true;

    public enum PositionCategory {
        DIRECTION,      // Postes de direction (Gouverneur, SG, Chef de Cabinet, etc.)
        TECHNIQUE,      // Postes techniques (Inspecteurs, Cadres d'appui, etc.)
        ADMINISTRATIF,  // Postes administratifs (Secrétaires, Agents, etc.)
        SUPPORT         // Postes de support (Chauffeurs, Agents de liaison, etc.)
    }
}
