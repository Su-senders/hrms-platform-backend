package com.hrms.dto;

import lombok.*;
import java.util.List;

/**
 * DTO pour l'historique complet des formations d'un personnel
 * Combine ProfessionalTraining (historique ancien) et TrainingEnrollment (sessions modernes)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelTrainingHistoryDTO {

    private Long personnelId;
    private String matricule;
    private String fullName;

    /**
     * Formations de l'historique professionnel (ProfessionalTraining)
     * Inclut les formations suivies avant la mise en place du système moderne
     */
    private List<ProfessionalTrainingDTO> professionalTrainings;

    /**
     * Inscriptions aux sessions modernes (TrainingEnrollment)
     * Formations gérées par le système de gestion complet
     */
    private List<TrainingEnrollmentDTO> modernEnrollments;

    /**
     * Statistiques globales
     */
    private TrainingStatsSummary statistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrainingStatsSummary {
        private Integer totalTrainings;              // Total toutes sources
        private Integer totalProfessionalTrainings;  // Nombre historique
        private Integer totalModernEnrollments;      // Nombre sessions modernes
        private Integer totalAttended;               // Nombre avec présence confirmée
        private Integer totalCertificatesObtained;   // Certificats délivrés
        private Integer totalTrainingDays;           // Jours de formation totaux
    }
}
