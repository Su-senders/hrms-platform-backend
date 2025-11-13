package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Historique des affectations d'un personnel
 * Enregistre tous les mouvements d'affectation à un poste/structure
 */
@Entity
@Table(name = "assignment_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class AssignmentHistory extends BaseEntity {

    /**
     * Personnel concerné
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel;

    /**
     * Poste précédent (peut être null si première affectation)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_old_id")
    private Position positionOld;

    /**
     * Nouveau poste (peut être null si fin d'affectation)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_new_id")
    private Position positionNew;

    /**
     * Structure précédente (peut être null si première affectation)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_old_id")
    private AdministrativeStructure structureOld;

    /**
     * Nouvelle structure
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_new_id")
    private AdministrativeStructure structureNew;

    /**
     * Date de début de l'affectation
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Date de fin de l'affectation (null si affectation en cours)
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Type de mouvement
     */
    @Column(name = "movement_type", length = 50)
    @Enumerated(EnumType.STRING)
    private MovementType movementType;

    /**
     * Référence vers le document de décision (chemin fichier)
     */
    @Column(name = "decision_document", length = 500)
    private String decisionDocument;

    /**
     * Numéro de la décision
     */
    @Column(name = "decision_number", length = 100)
    private String decisionNumber;

    /**
     * Date de la décision
     */
    @Column(name = "decision_date")
    private LocalDate decisionDate;

    /**
     * Motif de l'affectation/mutation
     */
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    /**
     * Statut de l'affectation
     */
    @Column(name = "status", length = 50)
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status = AssignmentStatus.ACTIVE;

    /**
     * Notes additionnelles
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Type de mouvement d'affectation
     */
    public enum MovementType {
        AFFECTATION,        // Première affectation
        MUTATION,           // Mutation (changement de structure)
        DETACHEMENT,        // Détachement temporaire
        MISE_DISPOSITION,   // Mise à disposition
        REAFFECTATION,      // Réaffectation après détachement
        FIN_DETACHEMENT,    // Fin de détachement
        PROMOTION_MOBILITE  // Promotion avec changement de poste
    }

    /**
     * Statut de l'affectation
     */
    public enum AssignmentStatus {
        ACTIVE,      // Affectation en cours
        COMPLETED,   // Affectation terminée
        CANCELLED    // Affectation annulée
    }

    /**
     * Termine l'affectation
     */
    public void endAssignment(LocalDate endDate) {
        this.endDate = endDate;
        this.status = AssignmentStatus.COMPLETED;
    }

    /**
     * Annule l'affectation
     */
    public void cancel(String reason) {
        this.status = AssignmentStatus.CANCELLED;
        this.notes = (this.notes != null ? this.notes + "\n" : "") + "Annulée: " + reason;
    }

    /**
     * Vérifie si l'affectation est en cours
     */
    public boolean isActive() {
        return status == AssignmentStatus.ACTIVE && endDate == null;
    }

    /**
     * Calcule la durée de l'affectation en jours
     */
    @Transient
    public long getDurationInDays() {
        if (startDate == null) {
            return 0;
        }

        LocalDate end = (endDate != null) ? endDate : LocalDate.now();
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, end);
    }
}
