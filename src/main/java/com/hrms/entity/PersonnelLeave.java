package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.Period;

/**
 * Personnel Leave entity (Mise en congé)
 * Tracks leave/vacation periods for personnel
 * Part of Section B - Qualifications
 */
@Entity
@Table(name = "personnel_leaves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PersonnelLeave extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Personnel personnel;

    /**
     * Motif de mise en congé
     */
    @Column(name = "leave_reason", nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveReason leaveReason;

    /**
     * Durée en jours
     */
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    /**
     * Date d'effet (début du congé)
     */
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    /**
     * Date d'expiration (fin du congé)
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    /**
     * Numéro de décision administrative
     */
    @Column(name = "decision_number", length = 100)
    private String decisionNumber;

    /**
     * Date de la décision
     */
    @Column(name = "decision_date")
    private LocalDate decisionDate;

    /**
     * Statut du congé
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveStatus status = LeaveStatus.APPROVED;

    /**
     * Notes additionnelles
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum LeaveReason {
        ADMINISTRATIF,  // Congé administratif/annuel
        ANNUEL,         // Congé annuel
        MALADIE,        // Congé maladie
        MATERNITE,      // Congé de maternité
        PATERNITE,      // Congé de paternité
        ACCIDENT,       // Congé accident
        FORMATION,      // Congé pour formation
        PERSONNEL,      // Congé personnel
        SANS_SOLDE,     // Congé sans solde
        AUTRE           // Autre motif
    }

    public enum LeaveStatus {
        REQUESTED,      // Demandé
        APPROVED,       // Approuvé
        REJECTED,       // Rejeté
        IN_PROGRESS,    // En cours
        COMPLETED,      // Terminé
        CANCELLED       // Annulé
    }

    /**
     * Calcule automatiquement la durée en jours si non renseignée
     */
    @PrePersist
    @PreUpdate
    private void calculateDuration() {
        if (effectiveDate != null && expiryDate != null && durationDays == null) {
            this.durationDays = (int) java.time.temporal.ChronoUnit.DAYS.between(effectiveDate, expiryDate) + 1;
        }
    }

    /**
     * Vérifie si le congé est en cours
     */
    @Transient
    public boolean isInProgress() {
        LocalDate now = LocalDate.now();
        return status == LeaveStatus.IN_PROGRESS ||
               (effectiveDate != null && expiryDate != null &&
                !now.isBefore(effectiveDate) && !now.isAfter(expiryDate));
    }

    /**
     * Vérifie si le congé est terminé
     */
    @Transient
    public boolean isCompleted() {
        return status == LeaveStatus.COMPLETED ||
               (expiryDate != null && LocalDate.now().isAfter(expiryDate));
    }

    /**
     * Vérifie si le congé est à venir
     */
    @Transient
    public boolean isUpcoming() {
        return effectiveDate != null && LocalDate.now().isBefore(effectiveDate);
    }

    /**
     * Obtient la durée formatée
     */
    @Transient
    public String getFormattedDuration() {
        if (durationDays == null) {
            return "N/A";
        }
        if (durationDays < 30) {
            return durationDays + " jour(s)";
        } else if (durationDays < 365) {
            int months = durationDays / 30;
            int days = durationDays % 30;
            if (days > 0) {
                return months + " mois et " + days + " jour(s)";
            }
            return months + " mois";
        } else {
            int years = durationDays / 365;
            int remainingDays = durationDays % 365;
            int months = remainingDays / 30;
            if (months > 0) {
                return years + " an(s) et " + months + " mois";
            }
            return years + " an(s)";
        }
    }
}

