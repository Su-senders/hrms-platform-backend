package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TrainingSession entity (Session de Formation)
 * Represents a specific instance of a training program with dates, location, and participants
 */
@Entity
@Table(name = "training_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class TrainingSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Training training;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Lieu de la formation
     */
    @Column(name = "location", nullable = false, length = 300)
    private String location;

    /**
     * Nombre maximum de participants
     */
    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    /**
     * Nombre minimum de participants requis
     */
    @Column(name = "min_participants")
    private Integer minParticipants;

    /**
     * Statut de la session
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.PLANNED;

    /**
     * Formateur principal
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Trainer trainer;

    /**
     * Co-formateurs
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "training_session_co_trainers",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Trainer> coTrainers = new ArrayList<>();

    /**
     * Structure organisatrice
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizing_structure_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AdministrativeStructure organizingStructure;

    /**
     * Coût total de la session
     * @deprecated Utiliser actualCost qui est calculé automatiquement
     */
    @Deprecated
    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost;

    /**
     * Coût par participant
     * @deprecated Utiliser getActualCostPerParticipant() qui est calculé automatiquement
     */
    @Deprecated
    @Column(name = "cost_per_participant", precision = 15, scale = 2)
    private BigDecimal costPerParticipant;

    /**
     * Coût estimé (calculé automatiquement)
     */
    @Column(name = "estimated_cost", precision = 15, scale = 2)
    private BigDecimal estimatedCost;

    /**
     * Coût réel total (somme des TrainingCost)
     */
    @Column(name = "actual_cost", precision = 15, scale = 2)
    private BigDecimal actualCost;

    /**
     * Budget alloué
     */
    @Column(name = "budget", precision = 15, scale = 2)
    private BigDecimal budget;

    /**
     * Description de la session
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Notes additionnelles
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Date d'ouverture des inscriptions
     */
    @Column(name = "enrollment_start_date")
    private LocalDate enrollmentStartDate;

    /**
     * Date de fermeture des inscriptions
     */
    @Column(name = "enrollment_end_date")
    private LocalDate enrollmentEndDate;

    /**
     * Inscriptions à cette session
     */
    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<TrainingEnrollment> enrollments = new ArrayList<>();

    /**
     * Coûts de cette session
     */
    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<TrainingCost> costs = new ArrayList<>();

    public enum SessionStatus {
        PLANNED,        // Planifiée
        OPEN,           // Inscriptions ouvertes
        IN_PROGRESS,    // En cours
        COMPLETED,      // Terminée
        CANCELLED,      // Annulée
        POSTPONED       // Reportée
    }

    /**
     * Calcule le nombre de participants inscrits
     */
    @Transient
    public int getEnrolledCount() {
        return (int) enrollments.stream()
                .filter(e -> e.getStatus() == TrainingEnrollment.EnrollmentStatus.APPROVED ||
                           e.getStatus() == TrainingEnrollment.EnrollmentStatus.ATTENDED)
                .count();
    }

    /**
     * Calcule le nombre de places disponibles
     */
    @Transient
    public int getAvailableSlots() {
        return maxParticipants - getEnrolledCount();
    }

    /**
     * Vérifie si la session est complète (plus de places)
     */
    @Transient
    public boolean isFull() {
        return getEnrolledCount() >= maxParticipants;
    }

    /**
     * Vérifie si les inscriptions sont ouvertes
     */
    @Transient
    public boolean isEnrollmentOpen() {
        LocalDate now = LocalDate.now();
        if (enrollmentStartDate == null || enrollmentEndDate == null) {
            return status == SessionStatus.OPEN;
        }
        return status == SessionStatus.OPEN &&
               !now.isBefore(enrollmentStartDate) &&
               !now.isAfter(enrollmentEndDate);
    }

    /**
     * Vérifie si la session peut démarrer (nombre min de participants atteint)
     */
    @Transient
    public boolean canStart() {
        return getEnrolledCount() >= (minParticipants != null ? minParticipants : 1);
    }

    /**
     * Calcule et met à jour le coût estimé
     */
    @PrePersist
    @PreUpdate
    public void calculateEstimatedCost() {
        if (training != null && startDate != null && endDate != null) {
            int durationDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
            int participants = Math.max(1, getEnrolledCount());

            this.estimatedCost = training.calculateEstimatedCost(durationDays, participants);
        }
    }

    /**
     * Calcule le coût réel total (somme des coûts)
     */
    @Transient
    public BigDecimal getCalculatedActualCost() {
        return costs.stream()
            .filter(cost -> cost.getPaymentStatus() != TrainingCost.PaymentStatus.CANCELLED)
            .map(TrainingCost::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Met à jour le coût réel
     */
    public void updateActualCost() {
        this.actualCost = getCalculatedActualCost();
    }

    /**
     * Calcule l'écart budgétaire (estimé vs réel)
     */
    @Transient
    public BigDecimal getBudgetVariance() {
        if (estimatedCost == null || actualCost == null) {
            return BigDecimal.ZERO;
        }
        return actualCost.subtract(estimatedCost);
    }

    /**
     * Calcule le pourcentage d'écart
     */
    @Transient
    public Double getBudgetVariancePercentage() {
        if (estimatedCost == null || estimatedCost.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return getBudgetVariance()
            .divide(estimatedCost, 4, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .doubleValue();
    }

    /**
     * Vérifie si le budget est dépassé
     */
    @Transient
    public boolean isBudgetExceeded() {
        if (budget == null || actualCost == null) {
            return false;
        }
        return actualCost.compareTo(budget) > 0;
    }

    /**
     * Obtient le coût par participant (réel)
     */
    @Transient
    public BigDecimal getActualCostPerParticipant() {
        int enrolled = getEnrolledCount();
        if (enrolled == 0 || actualCost == null) {
            return BigDecimal.ZERO;
        }
        return actualCost.divide(
            BigDecimal.valueOf(enrolled),
            2,
            java.math.RoundingMode.HALF_UP
        );
    }
}




