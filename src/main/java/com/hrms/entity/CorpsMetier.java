package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un corps de métier de la fonction publique camerounaise.
 * Un corps de métier regroupe plusieurs grades hiérarchisés.
 *
 * Exemples:
 * - Administration Générale
 * - Informaticiens
 * - Régies Financières
 */
@Entity
@Table(name = "corps_metiers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CorpsMetier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Code unique du corps de métier
     * Exemples: ADMIN-GENERALE, INFORMATICIENS, REGIES-FINANCIERES
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /**
     * Nom complet du corps de métier
     * Exemple: "Administration Générale"
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Description du corps de métier
     */
    @Column(length = 1000)
    private String description;

    /**
     * Catégorie principale du corps
     * Exemples: ADMINISTRATIF, TECHNIQUE, FINANCIER
     */
    @Column(length = 50)
    private String category;

    /**
     * Ministère de tutelle principal
     * Exemples: MINAT, MINFI, MINPOSTEL
     */
    @Column(length = 100)
    private String ministere;

    /**
     * Indique si le corps est actif
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Liste des grades associés à ce corps de métier
     * Relation bidirectionnelle
     */
    @OneToMany(mappedBy = "corpsMetier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Grade> grades = new ArrayList<>();

    /**
     * Méthode helper pour ajouter un grade
     */
    public void addGrade(Grade grade) {
        grades.add(grade);
        grade.setCorpsMetier(this);
    }

    /**
     * Méthode helper pour retirer un grade
     */
    public void removeGrade(Grade grade) {
        grades.remove(grade);
        grade.setCorpsMetier(null);
    }
}
