package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Position (Poste de travail) entity
 * Represents job positions within MINAT structures
 */
@Entity
@Table(name = "positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "title", nullable = false)
    private String title; // Intitulé du poste

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id", nullable = false)
    private AdministrativeStructure structure;

    @Column(name = "rank")
    private String rank; // Rang du poste

    @Column(name = "category")
    private String category; // Catégorie (A, B, C, etc.)

    @Column(name = "required_grade")
    private String requiredGrade; // Grade requis

    @Column(name = "required_corps")
    private String requiredCorps; // Corps requis

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PositionStatus status = PositionStatus.VACANT;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_personnel_id")
    private Personnel currentPersonnel; // Personnel actuellement affecté

    @Column(name = "assignment_date")
    private java.time.LocalDate assignmentDate; // Date d'affectation du personnel actuel

    @Column(name = "responsibilities", columnDefinition = "TEXT")
    private String responsibilities; // Responsabilités

    @Column(name = "required_qualifications", columnDefinition = "TEXT")
    private String requiredQualifications;

    @Column(name = "min_experience_years")
    private Integer minExperienceYears;

    @Column(name = "is_managerial")
    private Boolean isManagerial = false; // Poste de responsabilité

    @Column(name = "reports_to_position_id")
    private Long reportsToPositionId; // Poste hiérarchique supérieur

    @Column(name = "budget_code")
    private String budgetCode;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "is_nominative")
    private Boolean isNominative = false; // Poste nominatif (Gouverneur, Préfet, etc.)

    /**
     * Référence au template de poste organisationnel utilisé pour créer ce poste
     * Permet de tracer l'origine du poste depuis un template
     */
    @Column(name = "organizational_position_template_id")
    private Long organizationalPositionTemplateId;

    public enum PositionStatus {
        VACANT,      // Poste vacant (libre)
        OCCUPE,      // Poste occupé
        EN_CREATION, // Poste en cours de création
        SUPPRIME     // Poste supprimé
    }

    /**
     * Assign personnel to this position
     * @deprecated Utiliser assignPersonnel(Personnel, boolean) pour une meilleure gestion du cumul
     */
    @Deprecated
    public void assignPersonnel(Personnel personnel) {
        assignPersonnel(personnel, false);
    }

    /**
     * Assign personnel to this position
     *
     * @param personnel Personnel à affecter
     * @param allowCumul true si le cumul de poste est autorisé pour ce mouvement
     * @throws IllegalStateException si l'affectation n'est pas possible
     */
    public void assignPersonnel(Personnel personnel, boolean allowCumul) {
        // Vérifier que le poste est vacant (sauf si cumul autorisé)
        if (this.status != PositionStatus.VACANT && !allowCumul) {
            throw new IllegalStateException(
                String.format("Le poste '%s' n'est pas vacant (statut actuel: %s). " +
                              "Pour affecter un personnel sur un poste occupé, une autorisation de cumul est requise.",
                              this.code, this.status)
            );
        }

        // Vérifier que le personnel peut être affecté
        if (!personnel.canBeAssignedToPosition(allowCumul)) {
            String ineligibilityReason = personnel.getIneligibilityReason(allowCumul);

            if (ineligibilityReason != null) {
                throw new IllegalStateException(
                    String.format("Le personnel %s ne peut pas être affecté au poste '%s'. Raison: %s",
                                  personnel.getMatricule() != null ? personnel.getMatricule() : "E.C.I",
                                  this.code,
                                  ineligibilityReason)
                );
            } else {
                // Fallback si getIneligibilityReason retourne null mais canBeAssignedToPosition retourne false
                throw new IllegalStateException(
                    String.format("Le personnel %s ne peut pas être affecté au poste '%s'",
                                  personnel.getMatricule() != null ? personnel.getMatricule() : "E.C.I",
                                  this.code)
                );
            }
        }

        this.currentPersonnel = personnel;
        this.assignmentDate = java.time.LocalDate.now();
        this.status = PositionStatus.OCCUPE;
    }

    /**
     * Release personnel from this position
     */
    public void releasePersonnel() {
        this.currentPersonnel = null;
        this.assignmentDate = null;
        this.status = PositionStatus.VACANT;
    }

    /**
     * Check if position is available for assignment
     */
    public boolean isAvailableForAssignment() {
        return active && status == PositionStatus.VACANT;
    }

    /**
     * Get full position title with structure
     */
    @Transient
    public String getFullTitle() {
        return title + " - " + (structure != null ? structure.getName() : "N/A");
    }
}
