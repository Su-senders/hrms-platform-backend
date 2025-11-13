package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Template d'organisation réutilisable pour les structures territoriales.
 * Un template définit la structure organisationnelle standard qui sera
 * instanciée pour chaque Gouvernorat, Préfecture ou Sous-Préfecture.
 *
 * Exemple:
 * - Template Gouvernorat (1) → Instancié 10 fois (1 par région)
 * - Template Préfecture (1) → Instancié 58 fois (1 par département)
 */
@Entity
@Table(name = "organizational_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationalTemplate extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code; // "TPL-GOUV", "TPL-PREF", "TPL-SPREF"

    @Column(name = "name", nullable = false, length = 200)
    private String name; // "Template Gouvernorat"

    @Column(name = "applies_to", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdministrativeStructure.StructureType appliesTo; // GOUVERNORAT, PREFECTURE, SOUS_PREFECTURE

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "version")
    private Integer version = 1; // Versioning pour évolution des templates

    @Column(name = "active")
    private Boolean active = true;

    /**
     * Définition de la structure en JSON
     * Contient la hiérarchie des sous-structures et leurs positions
     */
    @Column(name = "structure_definition", columnDefinition = "TEXT")
    private String structureDefinition;

    /**
     * Métadonnées additionnelles (JSON)
     * Peut contenir des informations comme le nombre total de postes,
     * les grades requis, etc.
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
}
