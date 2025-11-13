package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Trainer entity (Formateur)
 * Represents internal or external trainers for training sessions
 */
@Entity
@Table(name = "trainers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Trainer extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    /**
     * Domaine de spécialisation
     * Ex: "Administration Publique", "Gestion RH", "Informatique"
     */
    @Column(name = "specialization", nullable = false, length = 200)
    private String specialization;

    /**
     * Organisme d'appartenance
     * Ex: "École Nationale d'Administration", "MINAT", "Organisme externe"
     */
    @Column(name = "organization", length = 300)
    private String organization;

    /**
     * Type de formateur
     */
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TrainerType type = TrainerType.EXTERNAL;

    /**
     * Référence au personnel si formateur interne
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Personnel personnel;

    /**
     * Qualifications et certifications
     */
    @Column(name = "qualifications", columnDefinition = "TEXT")
    private String qualifications;

    /**
     * Expérience en années
     */
    @Column(name = "experience_years")
    private Integer experienceYears;

    /**
     * Taux horaire (pour formateurs externes)
     */
    @Column(name = "hourly_rate", precision = 15, scale = 2)
    private java.math.BigDecimal hourlyRate;

    /**
     * Devise du taux horaire
     */
    @Column(name = "currency", length = 3)
    private String currency = "XAF";

    /**
     * Notes et commentaires
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Indique si le formateur est actif
     */
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    /**
     * Sessions de formation animées
     */
    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<TrainingSession> sessions = new ArrayList<>();

    /**
     * Sessions où le formateur est co-formateur
     */
    @ManyToMany(mappedBy = "coTrainers", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<TrainingSession> coTrainingSessions = new ArrayList<>();

    public enum TrainerType {
        INTERNAL,  // Formateur interne (personnel MINAT)
        EXTERNAL   // Formateur externe
    }

    /**
     * Retourne le nom complet
     */
    @Transient
    public String getFullName() {
        return lastName + " " + firstName;
    }

    /**
     * Vérifie si le formateur est disponible pour une période
     */
    @Transient
    public boolean isAvailable(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        // Vérifier les conflits avec les sessions existantes
        return sessions.stream()
                .noneMatch(session -> {
                    boolean startsDuring = !startDate.isBefore(session.getStartDate()) &&
                                         !startDate.isAfter(session.getEndDate());
                    boolean endsDuring = !endDate.isBefore(session.getStartDate()) &&
                                       !endDate.isAfter(session.getEndDate());
                    boolean overlaps = startDate.isBefore(session.getStartDate()) &&
                                     endDate.isAfter(session.getEndDate());
                    return (startsDuring || endsDuring || overlaps) &&
                           session.getStatus() != TrainingSession.SessionStatus.CANCELLED;
                });
    }
}

