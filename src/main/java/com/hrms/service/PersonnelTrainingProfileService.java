package com.hrms.service;

import com.hrms.dto.PersonnelTrainingHistoryDTO;
import com.hrms.dto.ProfessionalTrainingDTO;
import com.hrms.dto.TrainingEnrollmentDTO;
import com.hrms.entity.Personnel;
import com.hrms.entity.ProfessionalTraining;
import com.hrms.entity.TrainingEnrollment;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.ProfessionalTrainingMapper;
import com.hrms.mapper.TrainingEnrollmentMapper;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.ProfessionalTrainingRepository;
import com.hrms.repository.TrainingEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour gérer l'historique unifié des formations d'un personnel
 * Combine les données de ProfessionalTraining (historique) et TrainingEnrollment (moderne)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonnelTrainingProfileService {

    private final PersonnelRepository personnelRepository;
    private final ProfessionalTrainingRepository professionalTrainingRepository;
    private final TrainingEnrollmentRepository enrollmentRepository;
    private final ProfessionalTrainingMapper professionalTrainingMapper;
    private final TrainingEnrollmentMapper enrollmentMapper;

    /**
     * Obtient l'historique complet des formations d'un personnel
     * Combine ProfessionalTraining et TrainingEnrollment
     */
    public PersonnelTrainingHistoryDTO getPersonnelTrainingHistory(Long personnelId) {
        log.info("Récupération de l'historique complet des formations pour personnel ID: {}", personnelId);

        Personnel personnel = personnelRepository.findById(personnelId)
            .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", personnelId));

        // Récupérer formations de l'historique professionnel
        List<ProfessionalTraining> professionalTrainings =
            professionalTrainingRepository.findByPersonnelId(personnelId);

        List<ProfessionalTrainingDTO> professionalTrainingDTOs = professionalTrainings.stream()
            .map(professionalTrainingMapper::toDTO)
            .collect(Collectors.toList());

        // Récupérer inscriptions modernes
        List<TrainingEnrollment> enrollments =
            enrollmentRepository.findByPersonnelId(personnelId);

        List<TrainingEnrollmentDTO> enrollmentDTOs = enrollments.stream()
            .map(enrollmentMapper::toDTO)
            .collect(Collectors.toList());

        // Calculer statistiques
        PersonnelTrainingHistoryDTO.TrainingStatsSummary stats = calculateStatistics(
            professionalTrainings,
            enrollments
        );

        return PersonnelTrainingHistoryDTO.builder()
            .personnelId(personnelId)
            .matricule(personnel.getMatricule())
            .fullName(personnel.getFirstName() + " " + personnel.getLastName())
            .professionalTrainings(professionalTrainingDTOs)
            .modernEnrollments(enrollmentDTOs)
            .statistics(stats)
            .build();
    }

    /**
     * Calcule les statistiques globales de formation
     */
    private PersonnelTrainingHistoryDTO.TrainingStatsSummary calculateStatistics(
            List<ProfessionalTraining> professionalTrainings,
            List<TrainingEnrollment> enrollments) {

        int totalProfessional = professionalTrainings.size();

        int totalModern = enrollments.size();

        int totalAttended = (int) enrollments.stream()
            .filter(e -> e.getStatus() == TrainingEnrollment.EnrollmentStatus.ATTENDED)
            .count();

        // Certificats de l'historique
        int certsProfessional = (int) professionalTrainings.stream()
            .filter(pt -> pt.getCertificateObtained() != null &&
                         !pt.getCertificateObtained().trim().isEmpty())
            .count();

        // Certificats des sessions modernes
        int certsModern = (int) enrollments.stream()
            .filter(e -> Boolean.TRUE.equals(e.getCertificateIssued()))
            .count();

        int totalCertificates = certsProfessional + certsModern;

        // Jours de formation
        int daysProfessional = professionalTrainings.stream()
            .mapToInt(pt -> pt.getDurationDays() != null ? pt.getDurationDays() : 0)
            .sum();

        int daysModern = enrollments.stream()
            .filter(e -> e.getStatus() == TrainingEnrollment.EnrollmentStatus.ATTENDED)
            .mapToInt(e -> {
                if (e.getSession() != null &&
                    e.getSession().getStartDate() != null &&
                    e.getSession().getEndDate() != null) {
                    return (int) java.time.temporal.ChronoUnit.DAYS.between(
                        e.getSession().getStartDate(),
                        e.getSession().getEndDate()
                    ) + 1;
                }
                return 0;
            })
            .sum();

        int totalDays = daysProfessional + daysModern;

        return PersonnelTrainingHistoryDTO.TrainingStatsSummary.builder()
            .totalTrainings(totalProfessional + totalModern)
            .totalProfessionalTrainings(totalProfessional)
            .totalModernEnrollments(totalModern)
            .totalAttended(totalAttended)
            .totalCertificatesObtained(totalCertificates)
            .totalTrainingDays(totalDays)
            .build();
    }
}
