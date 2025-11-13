package com.hrms.service;

import com.hrms.dto.TrainingSessionCreateDTO;
import com.hrms.dto.TrainingSessionDTO;
import com.hrms.dto.TrainingSessionUpdateDTO;
import com.hrms.entity.*;
import com.hrms.exception.BusinessException;
import com.hrms.exception.DuplicateResourceException;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.TrainingSessionMapper;
import com.hrms.repository.*;
import com.hrms.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainingSessionService {

    private final TrainingSessionRepository sessionRepository;
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final AdministrativeStructureRepository structureRepository;
    private final TrainingCostRepository costRepository;
    private final TrainingSessionMapper sessionMapper;
    private final ProfessionalTrainingRepository professionalTrainingRepository;
    private final AuditUtil auditUtil;

    public TrainingSessionDTO createSession(TrainingSessionCreateDTO dto) {
        log.info("Creating training session with code: {}", dto.getCode());

        // Check for duplicate code
        if (sessionRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("TrainingSession", "code", dto.getCode());
        }

        // Validate dates
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("La date de fin doit être après la date de début");
        }

        // Load related entities
        Training training = trainingRepository.findById(dto.getTrainingId())
                .orElseThrow(() -> new ResourceNotFoundException("Training", "id", dto.getTrainingId()));

        Trainer trainer = trainerRepository.findById(dto.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", dto.getTrainerId()));

        // Check trainer availability
        if (!trainer.isAvailable(dto.getStartDate(), dto.getEndDate())) {
            throw new BusinessException("Le formateur n'est pas disponible pour cette période");
        }

        AdministrativeStructure organizingStructure = null;
        if (dto.getOrganizingStructureId() != null) {
            organizingStructure = structureRepository.findById(dto.getOrganizingStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("AdministrativeStructure", "id", dto.getOrganizingStructureId()));
        }

        // Load co-trainers
        List<Trainer> coTrainers = new ArrayList<>();
        if (dto.getCoTrainerIds() != null && !dto.getCoTrainerIds().isEmpty()) {
            coTrainers = dto.getCoTrainerIds().stream()
                    .map(id -> trainerRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", id)))
                    .collect(Collectors.toList());
        }

        TrainingSession session = sessionMapper.toEntity(dto);
        session.setTraining(training);
        session.setTrainer(trainer);
        session.setCoTrainers(coTrainers);
        session.setOrganizingStructure(organizingStructure);
        session.setStatus(TrainingSession.SessionStatus.PLANNED);
        session.setCreatedBy(auditUtil.getCurrentUser());
        session.setCreatedAt(java.time.LocalDateTime.now());

        TrainingSession saved = sessionRepository.save(session);
        log.info("Training session created with ID: {}", saved.getId());

        return sessionMapper.toDTO(saved);
    }

    public TrainingSessionDTO updateSession(Long id, TrainingSessionUpdateDTO dto) {
        log.info("Updating training session with ID: {}", id);

        TrainingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSession", "id", id));

        // Validate dates if provided
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            if (dto.getEndDate().isBefore(dto.getStartDate())) {
                throw new BusinessException("La date de fin doit être après la date de début");
            }
        }

        // Update related entities if changed
        if (dto.getTrainingId() != null) {
            Training training = trainingRepository.findById(dto.getTrainingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Training", "id", dto.getTrainingId()));
            session.setTraining(training);
        }

        if (dto.getTrainerId() != null) {
            Trainer trainer = trainerRepository.findById(dto.getTrainerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", dto.getTrainerId()));
            session.setTrainer(trainer);
        }

        if (dto.getCoTrainerIds() != null) {
            List<Trainer> coTrainers = dto.getCoTrainerIds().stream()
                    .map(trainerId -> trainerRepository.findById(trainerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", trainerId)))
                    .collect(Collectors.toList());
            session.setCoTrainers(coTrainers);
        }

        if (dto.getOrganizingStructureId() != null) {
            AdministrativeStructure structure = structureRepository.findById(dto.getOrganizingStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("AdministrativeStructure", "id", dto.getOrganizingStructureId()));
            session.setOrganizingStructure(structure);
        }

        sessionMapper.updateEntity(dto, session);
        session.setUpdatedBy(auditUtil.getCurrentUser());
        session.setUpdatedAt(java.time.LocalDateTime.now());

        // Recalculate costs if needed
        recalculateSessionCosts(session);

        TrainingSession updated = sessionRepository.save(session);
        log.info("Training session updated: {}", id);

        return sessionMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public TrainingSessionDTO getSessionById(Long id) {
        TrainingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSession", "id", id));
        return sessionMapper.toDTO(session);
    }

    @Transactional(readOnly = true)
    public Page<TrainingSessionDTO> getAllSessions(Pageable pageable) {
        return sessionRepository.findAll(pageable)
                .map(sessionMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<TrainingSessionDTO> getPlannedSessions() {
        return sessionRepository.findPlannedSessions().stream()
                .map(sessionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainingSessionDTO> getOpenSessions() {
        return sessionRepository.findOpenSessions().stream()
                .map(sessionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainingSessionDTO> getInProgressSessions() {
        return sessionRepository.findInProgressSessions().stream()
                .map(sessionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainingSessionDTO> getCompletedSessions() {
        return sessionRepository.findCompletedSessions().stream()
                .map(sessionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TrainingSessionDTO openEnrollments(Long id) {
        log.info("Opening enrollments for session ID: {}", id);
        TrainingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSession", "id", id));
        
        session.setStatus(TrainingSession.SessionStatus.OPEN);
        session.setUpdatedBy(auditUtil.getCurrentUser());
        session.setUpdatedAt(java.time.LocalDateTime.now());
        
        return sessionMapper.toDTO(sessionRepository.save(session));
    }

    public TrainingSessionDTO startSession(Long id) {
        log.info("Starting session ID: {}", id);
        TrainingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSession", "id", id));

        if (!session.canStart()) {
            throw new BusinessException("La session ne peut pas démarrer : nombre minimum de participants non atteint");
        }

        session.setStatus(TrainingSession.SessionStatus.IN_PROGRESS);
        session.setUpdatedBy(auditUtil.getCurrentUser());
        session.setUpdatedAt(java.time.LocalDateTime.now());

        return sessionMapper.toDTO(sessionRepository.save(session));
    }

    public TrainingSessionDTO completeSession(Long id) {
        log.info("Completing session ID: {}", id);
        TrainingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSession", "id", id));

        session.setStatus(TrainingSession.SessionStatus.COMPLETED);
        session.setUpdatedBy(auditUtil.getCurrentUser());
        session.setUpdatedAt(java.time.LocalDateTime.now());

        TrainingSession saved = sessionRepository.save(session);

        // Synchronize with ProfessionalTraining
        synchronizeWithProfessionalTraining(saved);

        return sessionMapper.toDTO(saved);
    }

    public TrainingSessionDTO cancelSession(Long id, String reason) {
        log.info("Cancelling session ID: {}", id);
        TrainingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSession", "id", id));

        session.setStatus(TrainingSession.SessionStatus.CANCELLED);
        if (reason != null) {
            session.setNotes((session.getNotes() != null ? session.getNotes() + "\n" : "") + 
                           "Annulation: " + reason);
        }
        session.setUpdatedBy(auditUtil.getCurrentUser());
        session.setUpdatedAt(java.time.LocalDateTime.now());

        return sessionMapper.toDTO(sessionRepository.save(session));
    }

    /**
     * Recalculate session costs
     */
    private void recalculateSessionCosts(TrainingSession session) {
        BigDecimal totalCost = costRepository.calculateTotalCostBySessionId(session.getId());
        if (totalCost != null) {
            // Mettre à jour actualCost (nouveau champ)
            session.setActualCost(totalCost);
            session.updateActualCost(); // Appeler la méthode pour s'assurer de la cohérence
            
            // Garder totalCost pour compatibilité (déprécié)
            session.setTotalCost(totalCost);
            
            int enrolledCount = session.getEnrolledCount();
            if (enrolledCount > 0) {
                session.setCostPerParticipant(totalCost.divide(BigDecimal.valueOf(enrolledCount), 2, BigDecimal.ROUND_HALF_UP));
            }
            sessionRepository.save(session);
        }
    }

    /**
     * Synchronize completed session with ProfessionalTraining
     */
    private void synchronizeWithProfessionalTraining(TrainingSession session) {
        session.getEnrollments().stream()
                .filter(e -> e.getStatus() == TrainingEnrollment.EnrollmentStatus.ATTENDED)
                .forEach(enrollment -> {
                    ProfessionalTraining training = ProfessionalTraining.builder()
                            .personnel(enrollment.getPersonnel())
                            .trainingField(session.getTraining().getTrainingField())
                            .trainer(session.getTrainer().getFullName())
                            .startDate(session.getStartDate())
                            .endDate(session.getEndDate())
                            .trainingLocation(session.getLocation())
                            .description(session.getDescription())
                            .certificateObtained(enrollment.getCertificateNumber())
                            .status(ProfessionalTraining.TrainingStatus.COMPLETED)
                            .build();
                    training.setCreatedBy(auditUtil.getCurrentUser());
                    training.setCreatedDate(LocalDate.now());
                    professionalTrainingRepository.save(training);
                    log.info("Created ProfessionalTraining for personnel {} from session {}", 
                            enrollment.getPersonnel().getId(), session.getId());
                });
    }

    public void deleteSession(Long id) {
        log.info("Soft deleting training session with ID: {}", id);
        TrainingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSession", "id", id));
        session.softDelete(auditUtil.getCurrentUser());
        sessionRepository.save(session);
        log.info("Training session soft deleted: {}", id);
    }
}



