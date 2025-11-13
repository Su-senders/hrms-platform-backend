package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Administrative Structure entity representing the hierarchical organization
 * of MINAT: Ministere -> Gouvernorat -> Prefecture -> Sous-Prefecture
 */
@Entity
@Table(name = "administrative_structures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministrativeStructure extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private StructureType type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_structure_id")
    private AdministrativeStructure parentStructure;

    @Column(name = "level")
    private Integer level; // 1: Ministere, 2: Gouvernorat, 3: Prefecture, 4: Sous-Prefecture

    /**
     * Référence vers la Région géographique (uniquement pour GOUVERNORAT)
     * Relation OneToOne bidirectionnelle
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    /**
     * Référence vers le Département géographique (uniquement pour PREFECTURE)
     * Relation OneToOne bidirectionnelle
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * Référence vers l'Arrondissement géographique (uniquement pour SOUS_PREFECTURE)
     * Relation OneToOne bidirectionnelle
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrondissement_id")
    private Arrondissement arrondissement;

    @Column(name = "city")
    private String city; // Chef-lieu de la structure (ville principale)

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "fax")
    private String fax;

    @Column(name = "head_officer_name")
    private String headOfficerName; // Nom du responsable

    @Column(name = "head_officer_title")
    private String headOfficerTitle; // Titre du responsable

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "total_positions")
    private Integer totalPositions; // Nombre total de postes

    @Column(name = "occupied_positions")
    private Integer occupiedPositions = 0; // Postes occupés

    @Column(name = "vacant_positions")
    private Integer vacantPositions = 0; // Postes vacants

    /**
     * Référence au template organisationnel utilisé pour créer cette structure
     * Permet de savoir quel template a été instancié
     */
    @Column(name = "organizational_template_id")
    private Long organizationalTemplateId;

    /**
     * Code de la sous-structure dans le template (pour les structures créées depuis un template)
     * Ex: "GOUV-SP", "GOUV-CABINET-SCOM"
     */
    @Column(name = "sub_structure_template_code", length = 100)
    private String subStructureTemplateCode;

    public enum StructureType {
        MINISTERE,
        GOUVERNORAT,
        PREFECTURE,
        SOUS_PREFECTURE,
        DIRECTION,
        SERVICE,
        AUTRE
    }

    /**
     * Calculate level based on structure type
     */
    @PrePersist
    @PreUpdate
    private void calculateLevel() {
        if (this.type != null) {
            switch (this.type) {
                case MINISTERE:
                    this.level = 1;
                    break;
                case GOUVERNORAT:
                    this.level = 2;
                    break;
                case PREFECTURE:
                    this.level = 3;
                    break;
                case SOUS_PREFECTURE:
                    this.level = 4;
                    break;
                default:
                    this.level = 5;
            }
        }
    }

    /**
     * Update position statistics
     */
    public void updatePositionStatistics(int total, int occupied) {
        this.totalPositions = total;
        this.occupiedPositions = occupied;
        this.vacantPositions = total - occupied;
    }

    /**
     * Get full hierarchical path
     */
    @Transient
    public String getFullPath() {
        if (parentStructure != null) {
            return parentStructure.getFullPath() + " > " + this.name;
        }
        return this.name;
    }

    /**
     * Obtenir le nom de la région (depuis l'entité géographique ou via parent)
     */
    @Transient
    public String getRegionName() {
        if (region != null) {
            return region.getName();
        }
        if (department != null && department.getRegion() != null) {
            return department.getRegion().getName();
        }
        if (arrondissement != null && arrondissement.getDepartment() != null
            && arrondissement.getDepartment().getRegion() != null) {
            return arrondissement.getDepartment().getRegion().getName();
        }
        return null;
    }

    /**
     * Obtenir le nom du département (depuis l'entité géographique ou via parent)
     */
    @Transient
    public String getDepartmentName() {
        if (department != null) {
            return department.getName();
        }
        if (arrondissement != null && arrondissement.getDepartment() != null) {
            return arrondissement.getDepartment().getName();
        }
        return null;
    }

    /**
     * Obtenir le nom de l'arrondissement (depuis l'entité géographique)
     */
    @Transient
    public String getArrondissementName() {
        return arrondissement != null ? arrondissement.getName() : null;
    }
}
