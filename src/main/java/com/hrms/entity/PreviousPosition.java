package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.Period;

/**
 * Previous Position entity (Poste de travail occupé antérieurement)
 * Tracks previous positions held by personnel (last 3 years)
 * Part of Section B - Qualifications
 */
@Entity
@Table(name = "previous_positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PreviousPosition extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Personnel personnel;

    /**
     * Intitulé du poste occupé
     */
    @Column(name = "position_title", nullable = false, length = 300)
    private String positionTitle;

    /**
     * Structure où le poste était occupé
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AdministrativeStructure structure;

    /**
     * Nom de la structure (si structure n'est pas référencée)
     */
    @Column(name = "structure_name", length = 300)
    private String structureName;

    /**
     * Date de début d'occupation du poste
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Date de fin d'occupation du poste
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Durée en mois (calculée automatiquement si null)
     */
    @Column(name = "duration_months")
    private Integer durationMonths;

    /**
     * Grade occupé à ce poste
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Grade grade;

    /**
     * Nom du grade (si grade n'est pas référencé)
     */
    @Column(name = "grade_name", length = 100)
    private String gradeName;

    /**
     * Responsabilités principales
     */
    @Column(name = "responsibilities", columnDefinition = "TEXT")
    private String responsibilities;

    /**
     * Raison de la fin d'occupation
     */
    @Column(name = "end_reason", length = 200)
    private String endReason;

    /**
     * Type de fin
     */
    @Column(name = "end_type")
    @Enumerated(EnumType.STRING)
    private EndType endType;

    /**
     * Notes additionnelles
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum EndType {
        MUTATION,           // Mutation vers un autre poste
        PROMOTION,          // Promotion
        AFFECTATION,        // Nouvelle affectation
        RETRAITE,           // Retraite
        DEMISSION,          // Démission
        REVOCATION,         // Révocation
        FIN_CONTRAT,        // Fin de contrat
        AUTRE               // Autre raison
    }

    /**
     * Calcule automatiquement la durée en mois si non renseignée
     */
    @PrePersist
    @PreUpdate
    private void calculateDuration() {
        if (startDate != null && endDate != null && durationMonths == null) {
            Period period = Period.between(startDate, endDate);
            this.durationMonths = period.getYears() * 12 + period.getMonths();
        }
    }

    /**
     * Vérifie si le poste est dans les 3 dernières années
     */
    @Transient
    public boolean isWithinLastThreeYears() {
        if (endDate == null) {
            return false;
        }
        LocalDate threeYearsAgo = LocalDate.now().minusYears(3);
        return !endDate.isBefore(threeYearsAgo);
    }

    /**
     * Obtient la durée formatée
     */
    @Transient
    public String getFormattedDuration() {
        if (durationMonths == null) {
            return "N/A";
        }
        if (durationMonths < 12) {
            return durationMonths + " mois";
        } else {
            int years = durationMonths / 12;
            int months = durationMonths % 12;
            if (months > 0) {
                return years + " an(s) et " + months + " mois";
            }
            return years + " an(s)";
        }
    }

    /**
     * Obtient le nom de la structure (depuis l'entité ou le champ)
     */
    @Transient
    public String getStructureNameValue() {
        return structure != null ? structure.getName() : structureName;
    }

    /**
     * Obtient le nom du grade (depuis l'entité ou le champ)
     */
    @Transient
    public String getGradeNameValue() {
        return grade != null ? grade.getName() : gradeName;
    }
}

