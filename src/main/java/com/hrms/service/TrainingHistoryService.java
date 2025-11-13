package com.hrms.service;

import com.hrms.entity.*;
import com.hrms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de synchronisation entre TrainingEnrollment et ProfessionalTraining
 * Maintient la cohérence entre le système d'inscription aux formations
 * et l'historique professionnel du personnel
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainingHistoryService {

    private final TrainingEnrollmentRepository enrollmentRepository;
    private final ProfessionalTrainingRepository professionalTrainingRepository;

    /**
     * Synchronise automatiquement l'historique des formations
     * Appelé quand un TrainingEnrollment passe à ATTENDED
     *
     * @param enrollmentId ID de l'inscription
     */
    public void synchronizeProfessionalTraining(Long enrollmentId) {
        log.info("Synchronisation de l'historique pour l'inscription ID: {}", enrollmentId);

        TrainingEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Inscription non trouvée: " + enrollmentId));

        // Vérifier si un ProfessionalTraining existe déjà pour cette inscription
        if (professionalTrainingExistsForEnrollment(enrollment)) {
            log.debug("ProfessionalTraining déjà existant pour cette inscription");
            return;
        }

        // Créer le ProfessionalTraining
        TrainingSession session = enrollment.getSession();
        Training training = session.getTraining();

        ProfessionalTraining professionalTraining = ProfessionalTraining.builder()
            .personnel(enrollment.getPersonnel())
            .trainingField(training.getTrainingField())
            .trainer(getTrainerDescription(session))
            .startDate(session.getStartDate())
            .endDate(session.getEndDate())
            .trainingLocation(session.getLocation())
            .description(String.format("%s - Session: %s",
                training.getTitle(),
                session.getCode()))
            .certificateObtained(enrollment.getCertificateNumber())
            .status(ProfessionalTraining.TrainingStatus.COMPLETED)
            .notes(String.format("Formation organisée par %s. Score: %d. Taux de présence: %.1f%%",
                session.getOrganizingStructure() != null
                    ? session.getOrganizingStructure().getName()
                    : "N/A",
                enrollment.getScore() != null ? enrollment.getScore() : 0,
                enrollment.getAttendanceRate() != null ? enrollment.getAttendanceRate() : 0.0))
            .build();

        professionalTrainingRepository.save(professionalTraining);

        log.info("ProfessionalTraining créé pour le personnel {} - Formation: {}",
            enrollment.getPersonnel().getMatricule() != null
                ? enrollment.getPersonnel().getMatricule()
                : "E.C.I",
            training.getTitle());
    }

    /**
     * Vérifie si un ProfessionalTraining existe déjà pour cette inscription
     * Évite les doublons dans l'historique
     */
    private boolean professionalTrainingExistsForEnrollment(TrainingEnrollment enrollment) {
        TrainingSession session = enrollment.getSession();
        Personnel personnel = enrollment.getPersonnel();

        // Chercher un ProfessionalTraining avec les mêmes dates et personnel
        return professionalTrainingRepository.existsByPersonnelAndStartDateAndEndDate(
            personnel,
            session.getStartDate(),
            session.getEndDate()
        );
    }

    /**
     * Construit la description du formateur
     * Inclut le formateur principal et les co-formateurs
     */
    private String getTrainerDescription(TrainingSession session) {
        Trainer trainer = session.getTrainer();
        if (trainer == null) {
            return "Formateur non spécifié";
        }

        StringBuilder description = new StringBuilder(trainer.getFullName());

        if (trainer.getOrganization() != null) {
            description.append(" (").append(trainer.getOrganization()).append(")");
        }

        // Ajouter les co-formateurs
        if (!session.getCoTrainers().isEmpty()) {
            description.append(" - Co-formateurs: ");
            description.append(session.getCoTrainers().stream()
                .map(Trainer::getFullName)
                .reduce((a, b) -> a + ", " + b)
                .orElse(""));
        }

        return description.toString();
    }
}
