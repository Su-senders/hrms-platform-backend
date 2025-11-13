package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Career Movement entity (Mouvement de carrière)
 * Tracks all career-related movements for personnel
 */
@Entity
@Table(name = "career_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerMovement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel;

    @Column(name = "movement_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementType movementType;

    @Column(name = "movement_date", nullable = false)
    private LocalDate movementDate; // Date effective du mouvement

    @Column(name = "decision_number")
    private String decisionNumber; // Numéro de décision administrative

    @Column(name = "decision_date")
    private LocalDate decisionDate;

    // Source (ancien poste/structure)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_position_id")
    private Position sourcePosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_structure_id")
    private AdministrativeStructure sourceStructure;

    @Column(name = "source_grade")
    private String sourceGrade;

    // Destination (nouveau poste/structure)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_position_id")
    private Position destinationPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_structure_id")
    private AdministrativeStructure destinationStructure;

    @Column(name = "destination_grade")
    private String destinationGrade;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason; // Motif du mouvement

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_official_cumul")
    private Boolean isOfficialCumul = false; // Cumul officiel

    @Column(name = "duration_months")
    private Integer durationMonths; // Durée (pour détachement, formation, etc.)

    @Column(name = "end_date")
    private LocalDate endDate; // Date de fin (pour mouvements temporaires)

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MovementStatus status = MovementStatus.PENDING;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "document_path")
    private String documentPath; // Chemin du document de décision

    public enum MovementType {
        AFFECTATION,           // Première affectation
        MUTATION,              // Changement de poste/structure
        PROMOTION,             // Promotion (changement de grade)
        DETACHEMENT,           // Détachement
        MISE_A_DISPOSITION,    // Mise à disposition
        FORMATION,             // Départ en formation
        STAGE,                 // Stage
        INTEGRATION,           // Intégration (après stage)
        RETRAITE,              // Départ à la retraite
        DECES,                 // Décès
        SUSPENSION,            // Suspension
        REVOCATION,            // Révocation
        DEMISSION,             // Démission
        DISPONIBILITE,         // Mise en disponibilité
        REINTEGRATION          // Réintégration
    }

    public enum MovementStatus {
        PENDING,      // En attente
        APPROVED,     // Approuvé
        EXECUTED,     // Exécuté
        CANCELLED,    // Annulé
        REJECTED      // Rejeté
    }

    /**
     * Approve the movement
     */
    public void approve(String approver) {
        this.status = MovementStatus.APPROVED;
        this.approvedBy = approver;
        this.approvalDate = LocalDate.now();
    }

    /**
     * Execute the movement (update personnel and positions)
     */
    public void execute() {
        if (this.status != MovementStatus.APPROVED) {
            throw new IllegalStateException("Le mouvement doit être approuvé avant exécution");
        }

        // Validation: Vérifier que le type de mouvement est compatible avec le cumul
        if (Boolean.TRUE.equals(isOfficialCumul) && !isMovementTypeCompatibleWithCumul()) {
            throw new IllegalStateException(
                String.format("Le type de mouvement '%s' n'est pas compatible avec un cumul de poste. " +
                              "Les mouvements de type RETRAITE, DECES, SUSPENSION, REVOCATION, DEMISSION et DISPONIBILITE " +
                              "ne peuvent pas être effectués avec un cumul de poste.",
                              movementType)
            );
        }

        // Update personnel
        if (destinationPosition != null) {
            personnel.setCurrentPosition(destinationPosition);
            personnel.setServiceStartDate(movementDate);
        }

        if (destinationStructure != null) {
            personnel.setStructure(destinationStructure);
        }

        if (destinationGrade != null) {
            personnel.setGrade(destinationGrade);
        }

        // Update situation based on movement type
        switch (movementType) {
            case FORMATION:
                personnel.setSituation(Personnel.PersonnelSituation.EN_FORMATION);
                break;
            case DETACHEMENT:
                personnel.setSituation(Personnel.PersonnelSituation.EN_DETACHEMENT);
                break;
            case MISE_A_DISPOSITION:
                personnel.setSituation(Personnel.PersonnelSituation.EN_MISE_A_DISPOSITION);
                break;
            case STAGE:
                personnel.setSituation(Personnel.PersonnelSituation.EN_STAGE);
                break;
            case RETRAITE:
                personnel.setSituation(Personnel.PersonnelSituation.RETRAITE);
                personnel.setStatus(Personnel.PersonnelStatus.RETIRED);
                break;
            case DECES:
                personnel.setSituation(Personnel.PersonnelSituation.DECEDE);
                personnel.setStatus(Personnel.PersonnelStatus.DECEASED);
                break;
            case SUSPENSION:
                personnel.setSituation(Personnel.PersonnelSituation.SUSPENDU);
                personnel.setStatus(Personnel.PersonnelStatus.SUSPENDED);
                break;
            default:
                personnel.setSituation(Personnel.PersonnelSituation.EN_FONCTION);
                personnel.setStatus(Personnel.PersonnelStatus.ACTIVE);
        }

        // Update position status
        if (sourcePosition != null && !Boolean.TRUE.equals(isOfficialCumul)) {
            sourcePosition.releasePersonnel();
        }

        if (destinationPosition != null) {
            // Passer le flag cumul à la méthode assignPersonnel
            destinationPosition.assignPersonnel(personnel, Boolean.TRUE.equals(isOfficialCumul));
        }

        this.status = MovementStatus.EXECUTED;
    }

    /**
     * Vérifie si le type de mouvement est compatible avec un cumul de poste
     *
     * @return true si le type de mouvement permet le cumul
     */
    private boolean isMovementTypeCompatibleWithCumul() {
        return movementType != MovementType.RETRAITE &&
               movementType != MovementType.DECES &&
               movementType != MovementType.SUSPENSION &&
               movementType != MovementType.REVOCATION &&
               movementType != MovementType.DEMISSION &&
               movementType != MovementType.DISPONIBILITE;
    }

    /**
     * Vérifie si le mouvement peut être exécuté (toutes les validations)
     *
     * @return message d'erreur ou null si le mouvement peut être exécuté
     */
    public String canExecute() {
        if (this.status != MovementStatus.APPROVED) {
            return "Le mouvement doit être approuvé avant exécution";
        }

        if (Boolean.TRUE.equals(isOfficialCumul) && !isMovementTypeCompatibleWithCumul()) {
            return String.format("Le type de mouvement '%s' n'est pas compatible avec un cumul de poste",
                                 movementType);
        }

        if (destinationPosition != null) {
            if (!destinationPosition.isAvailableForAssignment() && !Boolean.TRUE.equals(isOfficialCumul)) {
                return String.format("Le poste de destination '%s' n'est pas disponible",
                                     destinationPosition.getCode());
            }
        }

        if (!personnel.canBeAssignedToPosition(Boolean.TRUE.equals(isOfficialCumul))) {
            String reason = personnel.getIneligibilityReason(Boolean.TRUE.equals(isOfficialCumul));
            return reason != null ? reason : "Le personnel ne peut pas être affecté";
        }

        return null; // Peut être exécuté
    }

    /**
     * Cancel the movement
     */
    public void cancel() {
        if (this.status == MovementStatus.EXECUTED) {
            throw new IllegalStateException("Un mouvement exécuté ne peut pas être annulé");
        }
        this.status = MovementStatus.CANCELLED;
    }

    /**
     * Check if movement is temporary
     */
    public boolean isTemporary() {
        return movementType == MovementType.DETACHEMENT ||
               movementType == MovementType.MISE_A_DISPOSITION ||
               movementType == MovementType.FORMATION ||
               movementType == MovementType.STAGE ||
               movementType == MovementType.DISPONIBILITE;
    }
}
