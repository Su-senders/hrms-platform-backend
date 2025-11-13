package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Training entity (Formation)
 * Represents a training program/course that can be offered in multiple sessions
 */
@Entity
@Table(name = "trainings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Training extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Domaine de formation
     * Ex: "Administration Publique", "Gestion des Ressources Humaines", "Informatique"
     */
    @Column(name = "training_field", nullable = false, length = 200)
    private String trainingField;

    /**
     * Durée standard en jours
     */
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    /**
     * Objectifs pédagogiques
     */
    @Column(name = "objectives", columnDefinition = "TEXT")
    private String objectives;

    /**
     * Prérequis pour suivre la formation
     */
    @Column(name = "prerequisites", columnDefinition = "TEXT")
    private String prerequisites;

    /**
     * Contenu de la formation
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * Méthodes pédagogiques
     */
    @Column(name = "pedagogical_methods", columnDefinition = "TEXT")
    private String pedagogicalMethods;

    /**
     * Catégorie de formation
     */
    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private TrainingCategory category;

    /**
     * Niveau requis
     */
    @Column(name = "required_level", length = 50)
    private String requiredLevel; // Ex: "A1", "B2", etc.

    /**
     * Coût standard par participant (optionnel)
     * @deprecated Utiliser le système de tarification (pricingType, fixedPrice, etc.)
     */
    @Deprecated
    @Column(name = "standard_cost_per_participant", precision = 15, scale = 2)
    private java.math.BigDecimal standardCostPerParticipant;

    /**
     * Mode de tarification
     */
    @Column(name = "pricing_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PricingType pricingType = PricingType.FIXED;

    /**
     * Prix fixe (forfait complet)
     */
    @Column(name = "fixed_price", precision = 15, scale = 2)
    private java.math.BigDecimal fixedPrice;

    /**
     * Prix par jour de formation
     */
    @Column(name = "price_per_day", precision = 15, scale = 2)
    private java.math.BigDecimal pricePerDay;

    /**
     * Prix par personne
     */
    @Column(name = "price_per_person", precision = 15, scale = 2)
    private java.math.BigDecimal pricePerPerson;

    /**
     * Prix par jour ET par personne (ex: 50 000 XAF/jour/personne)
     */
    @Column(name = "price_per_day_per_person", precision = 15, scale = 2)
    private java.math.BigDecimal pricePerDayPerPerson;

    /**
     * Devise
     */
    @Column(name = "currency", length = 3, nullable = false)
    @Builder.Default
    private String currency = "XAF";

    /**
     * Nombre minimum de participants requis
     */
    @Column(name = "min_participants")
    private Integer minParticipants;

    /**
     * Nombre maximum de participants
     */
    @Column(name = "max_participants")
    private Integer maxParticipants;

    /**
     * Indique si la formation est active
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Sessions de cette formation
     */
    @OneToMany(mappedBy = "training", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<TrainingSession> sessions = new ArrayList<>();

    public enum TrainingCategory {
        CATEGORY_A,      // Formation catégorie A
        CATEGORY_B,      // Formation catégorie B
        CATEGORY_C,      // Formation catégorie C
        MANAGEMENT,      // Formation managériale
        TECHNICAL,       // Formation technique
        SOFT_SKILLS,     // Compétences comportementales
        COMPLIANCE,      // Conformité réglementaire
        LANGUAGE,        // Formation linguistique
        IT,              // Informatique
        OTHER            // Autre
    }

    public enum PricingType {
        FIXED,              // Prix fixe (forfait)
        PER_DAY,           // Prix par jour (total)
        PER_PERSON,        // Prix par personne (total)
        PER_DAY_PER_PERSON // Prix par jour ET par personne
    }

    /**
     * Calcule le coût estimé pour une session
     *
     * @param durationDays Durée en jours
     * @param numberOfParticipants Nombre de participants
     * @return Coût estimé
     */
    @Transient
    public java.math.BigDecimal calculateEstimatedCost(int durationDays, int numberOfParticipants) {
        if (durationDays <= 0 || numberOfParticipants <= 0) {
            return java.math.BigDecimal.ZERO;
        }

        switch (pricingType) {
            case FIXED:
                return fixedPrice != null ? fixedPrice : java.math.BigDecimal.ZERO;

            case PER_DAY:
                return pricePerDay != null
                    ? pricePerDay.multiply(java.math.BigDecimal.valueOf(durationDays))
                    : java.math.BigDecimal.ZERO;

            case PER_PERSON:
                return pricePerPerson != null
                    ? pricePerPerson.multiply(java.math.BigDecimal.valueOf(numberOfParticipants))
                    : java.math.BigDecimal.ZERO;

            case PER_DAY_PER_PERSON:
                return pricePerDayPerPerson != null
                    ? pricePerDayPerPerson
                        .multiply(java.math.BigDecimal.valueOf(durationDays))
                        .multiply(java.math.BigDecimal.valueOf(numberOfParticipants))
                    : java.math.BigDecimal.ZERO;

            default:
                return java.math.BigDecimal.ZERO;
        }
    }

    /**
     * Retourne une description du mode de tarification
     */
    @Transient
    public String getPricingDescription() {
        switch (pricingType) {
            case FIXED:
                return String.format("Forfait : %s %s",
                    formatAmount(fixedPrice), currency);

            case PER_DAY:
                return String.format("%s %s par jour",
                    formatAmount(pricePerDay), currency);

            case PER_PERSON:
                return String.format("%s %s par personne",
                    formatAmount(pricePerPerson), currency);

            case PER_DAY_PER_PERSON:
                return String.format("%s %s par jour et par personne",
                    formatAmount(pricePerDayPerPerson), currency);

            default:
                return "Non défini";
        }
    }

    private String formatAmount(java.math.BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,.0f", amount);
    }
}




