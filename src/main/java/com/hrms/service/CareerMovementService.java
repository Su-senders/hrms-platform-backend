package com.hrms.service;

import com.hrms.dto.CareerMovementCreateDTO;
import com.hrms.dto.CareerMovementDTO;
import com.hrms.dto.CareerMovementUpdateDTO;
import com.hrms.entity.*;
import com.hrms.exception.InvalidOperationException;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.CareerMovementMapper;
import com.hrms.repository.*;
import com.hrms.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CareerMovementService {

    private final CareerMovementRepository careerMovementRepository;
    private final PersonnelRepository personnelRepository;
    private final PositionRepository positionRepository;
    private final AdministrativeStructureRepository structureRepository;
    private final AuditLogRepository auditLogRepository;
    private final CareerMovementMapper careerMovementMapper;
    private final AuditUtil auditUtil;

    /**
     * Create career movement
     */
    public CareerMovementDTO createMovement(CareerMovementCreateDTO dto) {
        log.info("Creating career movement for personnel ID: {}", dto.getPersonnelId());

        CareerMovement movement = careerMovementMapper.toEntity(dto);

        // Set personnel
        Personnel personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", dto.getPersonnelId()));
        movement.setPersonnel(personnel);

        // Set source position/structure
        if (dto.getSourcePositionId() != null) {
            Position sourcePosition = positionRepository.findById(dto.getSourcePositionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Position", "id", dto.getSourcePositionId()));
            movement.setSourcePosition(sourcePosition);
        }

        if (dto.getSourceStructureId() != null) {
            AdministrativeStructure sourceStructure = structureRepository.findById(dto.getSourceStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", dto.getSourceStructureId()));
            movement.setSourceStructure(sourceStructure);
        }

        // Set destination position/structure
        if (dto.getDestinationPositionId() != null) {
            Position destPosition = positionRepository.findById(dto.getDestinationPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Position", "id", dto.getDestinationPositionId()));

            boolean allowCumul = Boolean.TRUE.equals(dto.getIsOfficialCumul());

            // Validation 1: Vérifier la disponibilité du poste
            if (!destPosition.isAvailableForAssignment() && !allowCumul) {
                throw new InvalidOperationException(
                    String.format("Le poste '%s' (%s) n'est pas disponible pour affectation (statut: %s). " +
                                  "Pour affecter un personnel sur un poste occupé, une autorisation de cumul est requise (isOfficialCumul = true).",
                                  destPosition.getCode(),
                                  destPosition.getTitle(),
                                  destPosition.getStatus())
                );
            }

            // Validation 2: Vérifier que le personnel peut être affecté (cumul)
            if (personnel.hasCurrentPosition() && !allowCumul) {
                throw new InvalidOperationException(
                    String.format("Le personnel %s est déjà affecté au poste '%s' (%s). " +
                                  "Un cumul de poste nécessite une autorisation spéciale (isOfficialCumul = true).",
                                  personnel.getMatricule() != null ? personnel.getMatricule() : "E.C.I",
                                  personnel.getCurrentPosition().getCode(),
                                  personnel.getCurrentPosition().getTitle())
                );
            }

            // Validation 3: Vérifier que le type de mouvement est compatible avec le cumul
            if (allowCumul) {
                CareerMovement.MovementType type = dto.getMovementType();
                if (type == CareerMovement.MovementType.RETRAITE ||
                    type == CareerMovement.MovementType.DECES ||
                    type == CareerMovement.MovementType.SUSPENSION ||
                    type == CareerMovement.MovementType.REVOCATION ||
                    type == CareerMovement.MovementType.DEMISSION ||
                    type == CareerMovement.MovementType.DISPONIBILITE) {

                    throw new InvalidOperationException(
                        String.format("Le type de mouvement '%s' n'est pas compatible avec un cumul de poste. " +
                                      "Les mouvements de type RETRAITE, DECES, SUSPENSION, REVOCATION, DEMISSION et DISPONIBILITE " +
                                      "ne peuvent pas être effectués avec un cumul de poste.",
                                      type)
                    );
                }
            }

            movement.setDestinationPosition(destPosition);
        }

        if (dto.getDestinationStructureId() != null) {
            AdministrativeStructure destStructure = structureRepository.findById(dto.getDestinationStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", dto.getDestinationStructureId()));
            movement.setDestinationStructure(destStructure);
        }

        movement.setCreatedBy(auditUtil.getCurrentUser());
        movement.setCreatedDate(LocalDate.now());

        CareerMovement saved = careerMovementRepository.save(movement);

        // Create audit log
        createAuditLog(saved, AuditLog.Action.CREATE);

        log.info("Career movement created with ID: {}", saved.getId());
        return careerMovementMapper.toDTO(saved);
    }

    /**
     * Update career movement
     */
    public CareerMovementDTO updateMovement(Long id, CareerMovementUpdateDTO dto) {
        log.info("Updating career movement with ID: {}", id);

        CareerMovement movement = careerMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CareerMovement", "id", id));

        // Only allow update if not yet executed
        if (movement.getStatus() == CareerMovement.MovementStatus.EXECUTED) {
            throw new InvalidOperationException("Un mouvement exécuté ne peut pas être modifié");
        }

        careerMovementMapper.updateEntity(dto, movement);
        movement.setUpdatedBy(auditUtil.getCurrentUser());
        movement.setUpdatedDate(LocalDate.now());

        CareerMovement updated = careerMovementRepository.save(movement);

        createAuditLog(updated, AuditLog.Action.UPDATE);

        log.info("Career movement updated successfully: {}", id);
        return careerMovementMapper.toDTO(updated);
    }

    /**
     * Approve career movement
     */
    public CareerMovementDTO approveMovement(Long id) {
        log.info("Approving career movement with ID: {}", id);

        CareerMovement movement = careerMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CareerMovement", "id", id));

        movement.approve(auditUtil.getCurrentUser());
        CareerMovement approved = careerMovementRepository.save(movement);

        createAuditLog(approved, AuditLog.Action.APPROVE);

        log.info("Career movement approved successfully: {}", id);
        return careerMovementMapper.toDTO(approved);
    }

    /**
     * Execute career movement (applies changes to personnel and positions)
     */
    public CareerMovementDTO executeMovement(Long id) {
        log.info("Executing career movement with ID: {}", id);

        CareerMovement movement = careerMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CareerMovement", "id", id));

        try {
            movement.execute();
            CareerMovement executed = careerMovementRepository.save(movement);

            // Save changes to personnel and positions
            personnelRepository.save(movement.getPersonnel());

            if (movement.getSourcePosition() != null) {
                positionRepository.save(movement.getSourcePosition());
            }

            if (movement.getDestinationPosition() != null) {
                positionRepository.save(movement.getDestinationPosition());
            }

            createAuditLog(executed, AuditLog.Action.EXECUTE);

            log.info("Career movement executed successfully: {}", id);
            return careerMovementMapper.toDTO(executed);

        } catch (IllegalStateException e) {
            log.error("Failed to execute movement: {}", e.getMessage());
            throw new InvalidOperationException(e.getMessage());
        }
    }

    /**
     * Cancel career movement
     */
    public CareerMovementDTO cancelMovement(Long id) {
        log.info("Cancelling career movement with ID: {}", id);

        CareerMovement movement = careerMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CareerMovement", "id", id));

        try {
            movement.cancel();
            CareerMovement cancelled = careerMovementRepository.save(movement);

            createAuditLog(cancelled, AuditLog.Action.CANCEL);

            log.info("Career movement cancelled successfully: {}", id);
            return careerMovementMapper.toDTO(cancelled);

        } catch (IllegalStateException e) {
            log.error("Failed to cancel movement: {}", e.getMessage());
            throw new InvalidOperationException(e.getMessage());
        }
    }

    /**
     * Get movement by ID
     */
    @Transactional(readOnly = true)
    public CareerMovementDTO getMovementById(Long id) {
        CareerMovement movement = careerMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CareerMovement", "id", id));
        return careerMovementMapper.toDTO(movement);
    }

    /**
     * Get movements by personnel
     */
    @Transactional(readOnly = true)
    public Page<CareerMovementDTO> getMovementsByPersonnel(Long personnelId, Pageable pageable) {
        return careerMovementRepository.findByPersonnelId(personnelId, pageable)
                .map(careerMovementMapper::toDTO);
    }

    /**
     * Get movements by type
     */
    @Transactional(readOnly = true)
    public Page<CareerMovementDTO> getMovementsByType(String type, Pageable pageable) {
        CareerMovement.MovementType movementType = CareerMovement.MovementType.valueOf(type.toUpperCase());
        return careerMovementRepository.findByMovementTypeAndDeletedFalse(movementType, pageable)
                .map(careerMovementMapper::toDTO);
    }

    /**
     * Get movements by status
     */
    @Transactional(readOnly = true)
    public Page<CareerMovementDTO> getMovementsByStatus(String status, Pageable pageable) {
        CareerMovement.MovementStatus movementStatus = CareerMovement.MovementStatus.valueOf(status.toUpperCase());
        return careerMovementRepository.findByStatusAndDeletedFalse(movementStatus, pageable)
                .map(careerMovementMapper::toDTO);
    }

    /**
     * Get all movements
     */
    @Transactional(readOnly = true)
    public Page<CareerMovementDTO> getAllMovements(Pageable pageable) {
        return careerMovementRepository.findAll(pageable).map(careerMovementMapper::toDTO);
    }

    /**
     * Delete movement
     */
    public void deleteMovement(Long id) {
        log.info("Soft deleting career movement with ID: {}", id);

        CareerMovement movement = careerMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CareerMovement", "id", id));

        if (movement.getStatus() == CareerMovement.MovementStatus.EXECUTED) {
            throw new InvalidOperationException("Un mouvement exécuté ne peut pas être supprimé");
        }

        movement.setDeleted(true);
        movement.setDeletedAt(LocalDateTime.now());
        movement.setDeletedBy(auditUtil.getCurrentUser());

        careerMovementRepository.save(movement);

        createAuditLog(movement, AuditLog.Action.DELETE);

        log.info("Career movement soft deleted successfully: {}", id);
    }

    /**
     * Create audit log entry
     */
    private void createAuditLog(CareerMovement movement, AuditLog.Action action) {
        AuditLog auditLog = AuditLog.builder()
                .entityType("CareerMovement")
                .entityId(movement.getId())
                .action(action)
                .performedBy(auditUtil.getCurrentUser())
                .performedAt(LocalDateTime.now())
                .newValues("Movement Type: " + movement.getMovementType() + ", Status: " + movement.getStatus())
                .build();

        auditLogRepository.save(auditLog);
    }
}
