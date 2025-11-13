package com.hrms.service;

import com.hrms.dto.reports.*;
import com.hrms.entity.*;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de rapports et statistiques sur les formations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainingReportService {

    private final TrainingEnrollmentRepository enrollmentRepository;
    private final TrainingSessionRepository sessionRepository;
    private final ProfessionalTrainingRepository professionalTrainingRepository;
    private final PersonnelRepository personnelRepository;
    private final TrainerRepository trainerRepository;
    private final StructureRepository structureRepository;
    private final TrainingRepository trainingRepository;

    /**
     * Statistiques des formations par personnel pour une année donnée
     */
    public PersonnelTrainingStatisticsDTO getPersonnelStatistics(Long personnelId, int year) {
        log.info("Génération des statistiques de formation pour le personnel {} - Année {}", personnelId, year);

        Personnel personnel = personnelRepository.findById(personnelId)
            .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", personnelId));

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        // Récupérer toutes les formations suivies
        List<TrainingEnrollment> enrollments = enrollmentRepository
            .findByPersonnelAndSessionStartDateBetween(personnel, startDate, endDate);

        // Filtrer celles où il a assisté
        List<TrainingEnrollment> attended = enrollments.stream()
            .filter(e -> e.getStatus() == TrainingEnrollment.EnrollmentStatus.ATTENDED)
            .collect(Collectors.toList());

        // Calcul des statistiques
        int totalTrainings = attended.size();
        int totalDays = attended.stream()
            .mapToInt(e -> calculateDuration(e.getSession().getStartDate(), e.getSession().getEndDate()))
            .sum();

        BigDecimal totalCost = attended.stream()
            .map(e -> calculatePersonnelCostForSession(e))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Répartition par domaine
        Map<String, Long> byField = attended.stream()
            .collect(Collectors.groupingBy(
                e -> e.getSession().getTraining().getTrainingField(),
                Collectors.counting()
            ));

        // Certifications obtenues
        long certificationsObtained = attended.stream()
            .filter(e -> Boolean.TRUE.equals(e.getCertificateIssued()))
            .count();

        return PersonnelTrainingStatisticsDTO.builder()
            .personnelId(personnelId)
            .matricule(personnel.getMatricule())
            .fullName(personnel.getFirstName() + " " + personnel.getLastName())
            .year(year)
            .totalTrainings(totalTrainings)
            .totalDays(totalDays)
            .totalCost(totalCost)
            .averageCostPerTraining(totalTrainings > 0
                ? totalCost.divide(BigDecimal.valueOf(totalTrainings), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO)
            .trainingsByField(byField)
            .certificationsObtained((int) certificationsObtained)
            .build();
    }

    /**
     * Statistiques des formations par structure pour une année donnée
     */
    public StructureTrainingStatisticsDTO getStructureStatistics(Long structureId, int year) {
        log.info("Génération des statistiques de formation pour la structure {} - Année {}", structureId, year);

        Structure structure = structureRepository.findById(structureId)
            .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", structureId));

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        // Récupérer les sessions organisées par cette structure
        List<TrainingSession> sessions = sessionRepository.findByStartDateBetween(startDate, endDate).stream()
            .filter(s -> s.getOrganizingStructure() != null && s.getOrganizingStructure().getId().equals(structureId))
            .collect(Collectors.toList());

        // Récupérer toutes les inscriptions ATTENDED pour ces sessions
        Set<Long> personnelTrained = new HashSet<>();
        int totalDays = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        Map<String, Long> byField = new HashMap<>();
        int totalCertifications = 0;

        for (TrainingSession session : sessions) {
            List<TrainingEnrollment> enrollments = enrollmentRepository.findBySessionId(session.getId()).stream()
                .filter(e -> e.getStatus() == TrainingEnrollment.EnrollmentStatus.ATTENDED)
                .collect(Collectors.toList());

            // Personnel unique
            enrollments.forEach(e -> personnelTrained.add(e.getPersonnel().getId()));

            // Jours de formation
            totalDays += calculateDuration(session.getStartDate(), session.getEndDate()) * enrollments.size();

            // Coûts
            if (session.getActualCost() != null) {
                totalCost = totalCost.add(session.getActualCost());
            }

            // Répartition par domaine
            String field = session.getTraining().getTrainingField();
            byField.put(field, byField.getOrDefault(field, 0L) + enrollments.size());

            // Certifications
            totalCertifications += (int) enrollments.stream()
                .filter(e -> Boolean.TRUE.equals(e.getCertificateIssued()))
                .count();
        }

        int personnelCount = personnelTrained.size();

        return StructureTrainingStatisticsDTO.builder()
            .structureId(structureId)
            .structureName(structure.getName())
            .structureCode(structure.getCode())
            .year(year)
            .totalPersonnelTrained(personnelCount)
            .totalTrainingSessions(sessions.size())
            .totalTrainingDays(totalDays)
            .totalCost(totalCost)
            .averageCostPerPersonnel(personnelCount > 0
                ? totalCost.divide(BigDecimal.valueOf(personnelCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO)
            .trainingsByField(byField)
            .totalCertificationsObtained(totalCertifications)
            .build();
    }

    /**
     * Statistiques globales pour une année donnée
     */
    public GlobalTrainingStatisticsDTO getGlobalStatistics(int year) {
        log.info("Génération des statistiques globales de formation - Année {}", year);

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        // Sessions de l'année
        List<TrainingSession> sessions = sessionRepository.findByStartDateBetween(startDate, endDate);

        // Toutes les inscriptions ATTENDED
        Set<Long> personnelTrained = new HashSet<>();
        int totalDays = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        Map<String, Long> sessionsByField = new HashMap<>();
        Map<String, Long> personnelByField = new HashMap<>();
        int totalCertifications = 0;

        for (TrainingSession session : sessions) {
            List<TrainingEnrollment> enrollments = enrollmentRepository.findBySessionId(session.getId()).stream()
                .filter(e -> e.getStatus() == TrainingEnrollment.EnrollmentStatus.ATTENDED)
                .collect(Collectors.toList());

            // Personnel unique
            enrollments.forEach(e -> personnelTrained.add(e.getPersonnel().getId()));

            // Jours
            totalDays += calculateDuration(session.getStartDate(), session.getEndDate()) * enrollments.size();

            // Coûts
            if (session.getActualCost() != null) {
                totalCost = totalCost.add(session.getActualCost());
            }

            // Répartition par domaine
            String field = session.getTraining().getTrainingField();
            sessionsByField.put(field, sessionsByField.getOrDefault(field, 0L) + 1);
            personnelByField.put(field, personnelByField.getOrDefault(field, 0L) + enrollments.size());

            // Certifications
            totalCertifications += (int) enrollments.stream()
                .filter(e -> Boolean.TRUE.equals(e.getCertificateIssued()))
                .count();
        }

        // Nombre de formations actives dans le catalogue
        long totalTrainingsInCatalog = trainingRepository.findAll().stream()
            .filter(Training::getActive)
            .count();

        // Formateurs actifs
        int activeTrainers = (int) trainerRepository.findAll().stream()
            .filter(Trainer::getActive)
            .count();

        int sessionCount = sessions.size();
        int personnelCount = personnelTrained.size();

        return GlobalTrainingStatisticsDTO.builder()
            .year(year)
            .totalTrainingsInCatalog((int) totalTrainingsInCatalog)
            .totalSessionsHeld(sessionCount)
            .totalPersonnelTrained(personnelCount)
            .totalTrainingDays(totalDays)
            .totalCost(totalCost)
            .averageCostPerSession(sessionCount > 0
                ? totalCost.divide(BigDecimal.valueOf(sessionCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO)
            .averageCostPerPersonnel(personnelCount > 0
                ? totalCost.divide(BigDecimal.valueOf(personnelCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO)
            .sessionsByField(sessionsByField)
            .personnelByField(personnelByField)
            .totalCertificationsIssued(totalCertifications)
            .totalActiveTrainers(activeTrainers)
            .build();
    }

    /**
     * Liste du personnel formé dans un domaine et période
     */
    public List<TrainedPersonnelDTO> getTrainedPersonnelByFieldAndPeriod(
            String trainingField,
            LocalDate startDate,
            LocalDate endDate) {

        log.info("Récupération du personnel formé - Domaine: {}, Période: {} à {}",
            trainingField, startDate, endDate);

        List<TrainingEnrollment> enrollments = enrollmentRepository
            .findByTrainingFieldAndPeriod(trainingField, startDate, endDate);

        // Filtrer ATTENDED uniquement
        return enrollments.stream()
            .filter(e -> e.getStatus() == TrainingEnrollment.EnrollmentStatus.ATTENDED)
            .map(this::mapToTrainedPersonnelDTO)
            .collect(Collectors.toList());
    }

    /**
     * Liste des participants d'une session
     */
    public List<ParticipantDTO> getSessionParticipants(Long sessionId) {
        log.info("Récupération des participants pour la session ID: {}", sessionId);

        TrainingSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("TrainingSession", "id", sessionId));

        List<TrainingEnrollment> enrollments = enrollmentRepository.findBySessionId(sessionId);

        return enrollments.stream()
            .map(this::mapToParticipantDTO)
            .collect(Collectors.toList());
    }

    /**
     * Top formateurs par activité
     */
    public List<TrainerActivityDTO> getTopTrainers(int year, int limit) {
        log.info("Récupération du top {} formateurs - Année {}", limit, year);

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<TrainingSession> sessions = sessionRepository.findByStartDateBetween(startDate, endDate);

        // Grouper par formateur
        Map<Long, List<TrainingSession>> sessionsByTrainer = sessions.stream()
            .filter(s -> s.getTrainer() != null)
            .collect(Collectors.groupingBy(s -> s.getTrainer().getId()));

        List<TrainerActivityDTO> trainerActivities = new ArrayList<>();

        for (Map.Entry<Long, List<TrainingSession>> entry : sessionsByTrainer.entrySet()) {
            Trainer trainer = trainerRepository.findById(entry.getKey()).orElse(null);
            if (trainer == null) continue;

            List<TrainingSession> trainerSessions = entry.getValue();
            int totalSessions = trainerSessions.size();

            int totalParticipants = trainerSessions.stream()
                .mapToInt(s -> (int) enrollmentRepository.findBySessionId(s.getId()).stream()
                    .filter(e -> e.getStatus() == TrainingEnrollment.EnrollmentStatus.ATTENDED)
                    .count())
                .sum();

            int totalDays = trainerSessions.stream()
                .mapToInt(s -> calculateDuration(s.getStartDate(), s.getEndDate()))
                .sum();

            trainerActivities.add(TrainerActivityDTO.builder()
                .trainerId(trainer.getId())
                .trainerName(trainer.getFullName())
                .organization(trainer.getOrganization())
                .specialization(trainer.getSpecialization())
                .totalSessions(totalSessions)
                .totalParticipants(totalParticipants)
                .totalDays(totalDays)
                .build());
        }

        // Trier par nombre de sessions décroissant et limiter
        return trainerActivities.stream()
            .sorted(Comparator.comparing(TrainerActivityDTO::getTotalSessions).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    // Méthodes utilitaires privées

    private int calculateDuration(LocalDate start, LocalDate end) {
        if (start == null || end == null) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }

    private BigDecimal calculatePersonnelCostForSession(TrainingEnrollment enrollment) {
        TrainingSession session = enrollment.getSession();
        if (session.getActualCost() == null) {
            return BigDecimal.ZERO;
        }
        int enrolledCount = session.getEnrolledCount();
        if (enrolledCount == 0) {
            return BigDecimal.ZERO;
        }
        return session.getActualCost().divide(
            BigDecimal.valueOf(enrolledCount),
            2,
            RoundingMode.HALF_UP
        );
    }

    private TrainedPersonnelDTO mapToTrainedPersonnelDTO(TrainingEnrollment enrollment) {
        Personnel personnel = enrollment.getPersonnel();
        TrainingSession session = enrollment.getSession();

        return TrainedPersonnelDTO.builder()
            .personnelId(personnel.getId())
            .matricule(personnel.getMatricule())
            .firstName(personnel.getFirstName())
            .lastName(personnel.getLastName())
            .structure(personnel.getStructure() != null
                ? personnel.getStructure().getName()
                : null)
            .trainingTitle(session.getTraining().getTitle())
            .trainingField(session.getTraining().getTrainingField())
            .sessionCode(session.getCode())
            .startDate(session.getStartDate())
            .endDate(session.getEndDate())
            .location(session.getLocation())
            .score(enrollment.getScore())
            .attendanceRate(enrollment.getAttendanceRate())
            .certificateObtained(Boolean.TRUE.equals(enrollment.getCertificateIssued()))
            .certificateNumber(enrollment.getCertificateNumber())
            .build();
    }

    private ParticipantDTO mapToParticipantDTO(TrainingEnrollment enrollment) {
        Personnel personnel = enrollment.getPersonnel();

        return ParticipantDTO.builder()
            .personnelId(personnel.getId())
            .matricule(personnel.getMatricule())
            .firstName(personnel.getFirstName())
            .lastName(personnel.getLastName())
            .position(personnel.getCurrentPosition() != null
                ? personnel.getCurrentPosition().getTitle()
                : null)
            .structure(personnel.getStructure() != null
                ? personnel.getStructure().getName()
                : null)
            .enrollmentDate(enrollment.getEnrollmentDate())
            .status(enrollment.getStatus())
            .build();
    }
}
