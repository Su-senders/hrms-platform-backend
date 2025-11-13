package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité géographique représentant une Région du Cameroun
 * Une région est une division administrative du pays (10 régions au total)
 */
@Entity
@Table(name = "regions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code; // Ex: "AD", "CE", "EN"

    @Column(name = "name", nullable = false, length = 100)
    private String name; // Ex: "Adamaoua", "Centre", "Extrême-Nord"

    @Column(name = "chef_lieu", nullable = false, length = 100)
    private String chefLieu; // Ex: "Ngaoundéré", "Yaoundé", "Maroua"

    @Column(name = "superficie_km2")
    private Double superficieKm2; // Superficie en km²

    @Column(name = "population")
    private Long population; // Population (dernier recensement)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "active")
    private Boolean active = true;

    /**
     * Référence vers le Gouvernorat (structure administrative) qui gère cette région
     * Relation OneToOne car une région = un gouvernorat
     */
    @OneToOne(mappedBy = "region", fetch = FetchType.LAZY)
    private AdministrativeStructure gouvernorat;
}
