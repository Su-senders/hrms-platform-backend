package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente un grade au sein d'un corps de métier.
 * Les grades sont hiérarchisés par niveau (1 = plus élevé).
 *
 * Exemples:
 * - Administrateur Civil Principal (niveau 1, catégorie A1)
 * - Administrateur Civil (niveau 2, catégorie A2)
 * - Secrétaire d'Administration Principal (niveau 3, catégorie B1)
 */
@Entity
@Table(name = "grades", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"code"}),
    @UniqueConstraint(columnNames = {"corps_metier_id", "level"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Grade extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Code unique du grade
     * Exemples: ADM-CIV-PRINC, SEC-ADM, INSP-TRESOR
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /**
     * Nom complet du grade
     * Exemple: "Administrateur Civil Principal"
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Niveau hiérarchique dans le corps de métier
     * 1 = grade le plus élevé, 2, 3, etc.
     */
    @Column(nullable = false)
    private Integer level;

    /**
     * Catégorie de la fonction publique
     * Valeurs: A1, A2, A3, B1, B2, C1, C2, D1
     */
    @Column(nullable = false, length = 10)
    private String category;

    /**
     * Description du grade
     */
    @Column(length = 1000)
    private String description;

    /**
     * Indique si le grade est actif
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Corps de métier auquel appartient ce grade
     * Relation Many-to-One
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "corps_metier_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CorpsMetier corpsMetier;

    /**
     * Vérifie si ce grade est supérieur ou égal à un autre grade
     * (niveau plus petit = grade plus élevé)
     */
    public boolean isSuperiorOrEqualTo(Grade other) {
        if (other == null) return true;
        if (!this.corpsMetier.equals(other.corpsMetier)) {
            return false; // Grades de corps différents ne sont pas comparables
        }
        return this.level <= other.level;
    }

    /**
     * Vérifie si ce grade est supérieur à un autre grade
     */
    public boolean isSuperiorTo(Grade other) {
        if (other == null) return true;
        if (!this.corpsMetier.equals(other.corpsMetier)) {
            return false;
        }
        return this.level < other.level;
    }

    /**
     * Vérifie si ce grade est inférieur à un autre grade
     */
    public boolean isInferiorTo(Grade other) {
        if (other == null) return false;
        if (!this.corpsMetier.equals(other.corpsMetier)) {
            return false;
        }
        return this.level > other.level;
    }
}
