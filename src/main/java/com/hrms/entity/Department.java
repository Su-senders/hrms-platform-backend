package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité géographique représentant un Département du Cameroun
 * Un département est une subdivision d'une région (58 départements au total)
 */
@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code; // Ex: "AD-DJER", "CE-MFOU"

    @Column(name = "name", nullable = false, length = 100)
    private String name; // Ex: "Djérem", "Mfoundi", "Lekié"

    @Column(name = "chef_lieu", nullable = false, length = 100)
    private String chefLieu; // Ex: "Tibati", "Yaoundé", "Monatélé"

    /**
     * Région parente à laquelle appartient ce département
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "superficie_km2")
    private Double superficieKm2;

    @Column(name = "population")
    private Long population;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "active")
    private Boolean active = true;

    /**
     * Référence vers la Préfecture (structure administrative) qui gère ce département
     * Relation OneToOne car un département = une préfecture
     */
    @OneToOne(mappedBy = "department", fetch = FetchType.LAZY)
    private AdministrativeStructure prefecture;
}
