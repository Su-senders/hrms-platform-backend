package com.hrms.service;

import com.hrms.dto.TrainingEnrollmentCreateDTO;
import com.hrms.dto.TrainingEnrollmentDTO;
import com.hrms.dto.TrainingEnrollmentUpdateDTO;
import com.hrms.entity.Personnel;
import com.hrms.entity.TrainingEnrollment;
import com.hrms.entity.TrainingSession;
import com.hrms.exception.BusinessException;
import com.hrms.exception.DuplicateResourceException;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.TrainingEnrollmentMapper;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.TrainingEnrollmentRepository;
import com.hrms.repository.TrainingSessionRepository;
import com.hrms.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainingEnrollmentService {

    private final TrainingEnrollmentRepository enrollmentRepository;
    private final TrainingSessionRepository sessionRepository;
    private final PersonnelRepository personnelRepository;
    private final TrainingEnrollmentMapper enrollmentMapper;
    private final AuditUtil auditUtil;
    private final TrainingHistoryService trainingHistoryService;

    public TrainingEnrollmentDTO createEnrollment(TrainingEnrollmentCreateDTO dto) {
        log.info("Creating enrollment for session {} and personnel {}", dto.getSessionId(), dto.getPersonnelId());

        TrainingSession session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSession", "id", dto.getSessionId()));

        Personnel personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", dto.getPersonnelId()));

        // Check if already enrolled
        TrainingEnrollment existing = enrollmentRepository.findBySessionIdAndPersonnelId(
                dto.getSessionId(), dto.getPersonnelId());
        if (existing != null && !existing.isDeleted()) {
            throw new DuplicateResourceException("TrainingEnrollment", 
                    "sessionId/personnelId", dto.getSessionId() + "/" + dto.getPersonnelId());
        }

        // Check if session is open for enrollment
        if (!session.isEnrollmentOpen() && session.getStatus() != TrainingSession.SessionStatus.OPEN) {
            throw new BusinessException("Les inscriptions ne sont pas ouvertes pour cette session");
        }

        // Check if session is full
        if (session.isFull()) {
            throw new BusinessException("La session est complète");
        }

        TrainingEnrollment enrollment = enrollmentMapper.toEntity(dto);
        enrollment.setSession(session);
        enrollment.setPersonnel(personnel);
        enrollment.setStatus(TrainingEnrollment.EnrollmentStatus.PENDING);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setCreatedBy(auditUtil.getCurrentUser());
        enrollment.setCreatedAt(java.time.LocalDateTime.now());

        TrainingEnrollment saved = enrollmentRepository.save(enrollment);
        log.info("Enrollment created with ID: {}", saved.getId());

        return enrollmentMapper.toDTO(saved);
    }

    public TrainingEnrollmentDTO approveEnrollment(Long id, String approver) {
        log.info("Approving enrollment ID: {}", id);
        TrainingEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingEnrollment", "id", id));

        if (enrollment.getStatus() != TrainingEnrollment.EnrollmentStatus.PENDING) {
            throw new BusinessException("Seules les inscriptions en attente peuvent être approuvées");
        }

        enrollment.approve(approver);
        enrollment.setUpdatedBy(auditUtil.getCurrentUser());
        enrollment.setUpdatedAt(java.time.LocalDateTime.now());

        return enrollmentMapper.toDTO(enrollmentRepository.save(enrollment));
    }

    public TrainingEnrollmentDTO rejectEnrollment(Long id, String reason) {
        log.info("Rejecting enrollment ID: {}", id);
        TrainingEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingEnrollment", "id", id));

        enrollment.reject(reason);
        enrollment.setUpdatedBy(auditUtil.getCurrentUser());
        enrollment.setUpdatedAt(java.time.LocalDateTime.now());

        return enrollmentMapper.toDTO(enrollmentRepository.save(enrollment));
    }

    public TrainingEnrollmentDTO markAsAttended(Long id) {
        log.info("Marking enrollment ID: {} as attended", id);
        TrainingEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingEnrollment", "id", id));

        enrollment.markAsAttended();
        enrollment.setUpdatedBy(auditUtil.getCurrentUser());
        enrollment.setUpdatedAt(java.time.LocalDateTime.now());

        TrainingEnrollment saved = enrollmentRepository.save(enrollment);

        // Synchroniser automatiquement avec ProfessionalTraining
        try {
            trainingHistoryService.synchronizeProfessionalTraining(id);
            log.info("ProfessionalTraining synchronisé avec succès pour l'inscription ID: {}", id);
        } catch (Exception e) {
            log.error("Erreur lors de la synchronisation ProfessionalTraining pour l'inscription ID: {}. Raison: {}",
                    id, e.getMessage());
            // Ne pas bloquer le processus principal si la synchronisation échoue
        }

        return enrollmentMapper.toDTO(saved);
    }

    public TrainingEnrollmentDTO markAsAbsent(Long id) {
        log.info("Marking enrollment ID: {} as absent", id);
        TrainingEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingEnrollment", "id", id));

        enrollment.markAsAbsent();
        enrollment.setUpdatedBy(auditUtil.getCurrentUser());
        enrollment.setUpdatedAt(java.time.LocalDateTime.now());

        return enrollmentMapper.toDTO(enrollmentRepository.save(enrollment));
    }

    public TrainingEnrollmentDTO issueCertificate(Long id, String certificateNumber) {
        log.info("Issuing certificate for enrollment ID: {}", id);
        TrainingEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingEnrollment", "id", id));

        if (certificateNumber == null || certificateNumber.isEmpty()) {
            certificateNumber = "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        enrollment.issueCertificate(certificateNumber);
        enrollment.setUpdatedBy(auditUtil.getCurrentUser());
        enrollment.setUpdatedAt(java.time.LocalDateTime.now());

        return enrollmentMapper.toDTO(enrollmentRepository.save(enrollment));
    }

    public TrainingEnrollmentDTO updateEnrollment(Long id, TrainingEnrollmentUpdateDTO dto) {
        log.info("Updating enrollment ID: {}", id);
        TrainingEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingEnrollment", "id", id));

        enrollmentMapper.updateEntity(dto, enrollment);
        enrollment.setUpdatedBy(auditUtil.getCurrentUser());
        enrollment.setUpdatedAt(java.time.LocalDateTime.now());

        return enrollmentMapper.toDTO(enrollmentRepository.save(enrollment));
    }

    @Transactional(readOnly = true)
    public TrainingEnrollmentDTO getEnrollmentById(Long id) {
        TrainingEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingEnrollment", "id", id));
        return enrollmentMapper.toDTO(enrollment);
    }

    @Transactional(readOnly = true)
    public List<TrainingEnrollmentDTO> getEnrollmentsBySession(Long sessionId) {
        return enrollmentRepository.findBySessionId(sessionId).stream()
                .map(enrollmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TrainingEnrollmentDTO> getEnrollmentsBySession(Long sessionId, Pageable pageable) {
        return enrollmentRepository.findBySessionId(sessionId, pageable)
                .map(enrollmentMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<TrainingEnrollmentDTO> getEnrollmentsByPersonnel(Long personnelId) {
        return enrollmentRepository.findByPersonnelId(personnelId).stream()
                .map(enrollmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TrainingEnrollmentDTO> getEnrollmentsByPersonnel(Long personnelId, Pageable pageable) {
        return enrollmentRepository.findByPersonnelId(personnelId, pageable)
                .map(enrollmentMapper::toDTO);
    }

    public void deleteEnrollment(Long id) {
        log.info("Soft deleting enrollment with ID: {}", id);
        TrainingEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingEnrollment", "id", id));
        enrollment.softDelete(auditUtil.getCurrentUser());
        enrollmentRepository.save(enrollment);
        log.info("Enrollment soft deleted: {}", id);
    }
}




