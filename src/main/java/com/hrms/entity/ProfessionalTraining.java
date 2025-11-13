package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.Period;

/**
 * Professional Training entity (Stage professionnel)
 * Tracks professional training/stages for personnel
 * Part of Section B - Qualifications
 */
@Entity
@Table(name = "professional_trainings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ProfessionalTraining extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Personnel personnel;

    /**
     * Domaine de formation
     * Ex: "Administration Publique", "Gestion des Ressources Humaines", "Informatique"
     */
    @Column(name = "training_field", nullable = false, length = 200)
    private String trainingField;

    /**
     * Formateur ou organisme de formation
     * Ex: "École Nationale d'Administration", "Centre de Formation XYZ"
     */
    @Column(name = "trainer", nullable = false, length = 300)
    private String trainer;

    /**
     * Date de début du stage/formation
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Date de fin du stage/formation
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Durée en jours (calculée automatiquement si null)
     */
    @Column(name = "duration_days")
    private Integer durationDays;

    /**
     * Lieu de formation
     * Ex: "Yaoundé", "Douala", "Étranger - Paris, France"
     */
    @Column(name = "training_location", nullable = false, length = 300)
    private String trainingLocation;

    /**
     * Description ou objectifs de la formation
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Certificat obtenu (si applicable)
     */
    @Column(name = "certificate_obtained", length = 200)
    private String certificateObtained;

    /**
     * Statut de la formation
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TrainingStatus status = TrainingStatus.COMPLETED;

    /**
     * Notes additionnelles
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum TrainingStatus {
        PLANNED,      // Planifiée
        IN_PROGRESS,  // En cours
        COMPLETED,    // Terminée
        CANCELLED,    // Annulée
        SUSPENDED     // Suspendue
    }

    /**
     * Calcule automatiquement la durée en jours si non renseignée
     */
    @PrePersist
    @PreUpdate
    private void calculateDuration() {
        if (startDate != null && endDate != null && durationDays == null) {
            Period period = Period.between(startDate, endDate);
            // Calculer le nombre total de jours (approximatif)
            this.durationDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        }
    }

    /**
     * Vérifie si la formation est en cours
     */
    @Transient
    public boolean isInProgress() {
        LocalDate now = LocalDate.now();
        return status == TrainingStatus.IN_PROGRESS ||
               (startDate != null && endDate != null &&
                !now.isBefore(startDate) && !now.isAfter(endDate));
    }

    /**
     * Vérifie si la formation est terminée
     */
    @Transient
    public boolean isCompleted() {
        return status == TrainingStatus.COMPLETED ||
               (endDate != null && LocalDate.now().isAfter(endDate));
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

