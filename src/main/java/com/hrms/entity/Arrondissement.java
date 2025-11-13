package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité géographique représentant un Arrondissement du Cameroun
 * Un arrondissement est une subdivision d'un département (~360 arrondissements au total)
 */
@Entity
@Table(name = "arrondissements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arrondissement extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 30)
    private String code; // Ex: "AD-DJER-TIB", "CE-MFOU-YDE1"

    @Column(name = "name", nullable = false, length = 100)
    private String name; // Ex: "Tibati", "Yaoundé 1er", "Yaoundé 2ème"

    @Column(name = "chef_lieu", length = 100)
    private String chefLieu; // Ex: "Tibati", "Yaoundé", "Yaoundé"

    /**
     * Département parent auquel appartient cet arrondissement
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "superficie_km2")
    private Double superficieKm2;

    @Column(name = "population")
    private Long population;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ArrondissementType type = ArrondissementType.NORMAL;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "active")
    private Boolean active = true;

    /**
     * Référence vers la Sous-Préfecture (structure administrative) qui gère cet arrondissement
     * Relation OneToOne car un arrondissement = une sous-préfecture
     */
    @OneToOne(mappedBy = "arrondissement", fetch = FetchType.LAZY)
    private AdministrativeStructure sousPrefecture;

    public enum ArrondissementType {
        NORMAL,      // Arrondissement normal
        URBAIN,      // Arrondissement urbain (dans les grandes villes)
        SPECIAL      // Arrondissement à statut spécial
    }

    /**
     * Obtenir le code de la région via le département
     */
    public String getRegionCode() {
        return department != null && department.getRegion() != null
            ? department.getRegion().getCode()
            : null;
    }

    /**
     * Obtenir le nom de la région via le département
     */
    public String getRegionName() {
        return department != null && department.getRegion() != null
            ? department.getRegion().getName()
            : null;
    }
}
