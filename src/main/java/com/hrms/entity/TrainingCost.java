package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TrainingCost entity (Coût de Formation)
 * Tracks costs associated with a training session
 */
@Entity
@Table(name = "training_costs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class TrainingCost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TrainingSession session;

    /**
     * Type de coût
     */
    @Column(name = "cost_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CostType costType;

    /**
     * Description du coût
     */
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    /**
     * Montant
     */
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /**
     * Devise
     */
    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "XAF";

    /**
     * Date de la dépense
     */
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    /**
     * Numéro de facture
     */
    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    /**
     * Statut du paiement
     */
    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    /**
     * Date de paiement
     */
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    /**
     * Fournisseur/prestataire
     */
    @Column(name = "supplier", length = 300)
    private String supplier;

    /**
     * Notes additionnelles
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum CostType {
        TRAINER_FEE,    // Honoraires formateur
        VENUE,          // Location de salle
        MATERIALS,      // Matériel pédagogique
        TRANSPORT,      // Transport
        ACCOMMODATION,  // Hébergement
        MEALS,          // Restauration
        CERTIFICATION,  // Coûts de certification
        OTHER           // Autre
    }

    public enum PaymentStatus {
        PENDING,    // En attente
        PAID,       // Payé
        CANCELLED   // Annulé
    }

    /**
     * Marquer comme payé
     */
    public void markAsPaid() {
        this.paymentStatus = PaymentStatus.PAID;
        this.paymentDate = LocalDate.now();
    }
}




