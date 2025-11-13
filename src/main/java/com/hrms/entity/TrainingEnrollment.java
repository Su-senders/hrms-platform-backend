package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * TrainingEnrollment entity (Inscription à une Formation)
 * Links a Personnel to a TrainingSession
 */
@Entity
@Table(name = "training_enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"session_id", "personnel_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class TrainingEnrollment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TrainingSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Personnel personnel;

    /**
     * Statut de l'inscription
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status = EnrollmentStatus.PENDING;

    /**
     * Date d'inscription
     */
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    /**
     * Date d'approbation
     */
    @Column(name = "approval_date")
    private LocalDate approvalDate;

    /**
     * Approuvé par
     */
    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    /**
     * Raison du rejet (si rejeté)
     */
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    /**
     * Certificat délivré
     */
    @Column(name = "certificate_issued")
    private Boolean certificateIssued = false;

    /**
     * Numéro de certificat
     */
    @Column(name = "certificate_number", length = 100)
    private String certificateNumber;

    /**
     * Date de délivrance du certificat
     */
    @Column(name = "certificate_date")
    private LocalDate certificateDate;

    /**
     * Évaluation du participant
     */
    @Column(name = "evaluation", columnDefinition = "TEXT")
    private String evaluation;

    /**
     * Score/Note obtenu
     */
    @Column(name = "score")
    private Integer score;

    /**
     * Présence effective
     */
    @Column(name = "attendance_rate")
    private Double attendanceRate; // Pourcentage de présence (0-100)

    /**
     * Notes additionnelles
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum EnrollmentStatus {
        PENDING,    // En attente d'approbation
        APPROVED,   // Approuvée
        REJECTED,   // Rejetée
        ATTENDED,   // A assisté à la formation
        ABSENT,     // Absent
        CANCELLED   // Inscription annulée
    }

    /**
     * Approuver l'inscription
     */
    public void approve(String approver) {
        this.status = EnrollmentStatus.APPROVED;
        this.approvalDate = LocalDate.now();
        this.approvedBy = approver;
        this.rejectionReason = null;
    }

    /**
     * Rejeter l'inscription
     */
    public void reject(String reason) {
        this.status = EnrollmentStatus.REJECTED;
        this.rejectionReason = reason;
    }

    /**
     * Marquer comme ayant assisté
     */
    public void markAsAttended() {
        this.status = EnrollmentStatus.ATTENDED;
    }

    /**
     * Marquer comme absent
     */
    public void markAsAbsent() {
        this.status = EnrollmentStatus.ABSENT;
    }

    /**
     * Délivrer un certificat
     */
    public void issueCertificate(String certificateNumber) {
        this.certificateIssued = true;
        this.certificateNumber = certificateNumber;
        this.certificateDate = LocalDate.now();
    }
}




