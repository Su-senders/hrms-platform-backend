package com.hrms.service;

import com.hrms.dto.TrainerCreateDTO;
import com.hrms.dto.TrainerDTO;
import com.hrms.dto.TrainerUpdateDTO;
import com.hrms.entity.Personnel;
import com.hrms.entity.Trainer;
import com.hrms.exception.DuplicateResourceException;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.TrainerMapper;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.TrainerRepository;
import com.hrms.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final PersonnelRepository personnelRepository;
    private final TrainerMapper trainerMapper;
    private final AuditUtil auditUtil;

    public TrainerDTO createTrainer(TrainerCreateDTO dto) {
        log.info("Creating trainer with code: {}", dto.getCode());

        // Check for duplicate code
        if (trainerRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Trainer", "code", dto.getCode());
        }

        // If internal trainer, check personnel exists
        Personnel personnel = null;
        if (dto.getPersonnelId() != null) {
            personnel = personnelRepository.findById(dto.getPersonnelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", dto.getPersonnelId()));
            
            // Check if this personnel is already a trainer
            if (trainerRepository.findByPersonnelId(dto.getPersonnelId()).isPresent()) {
                throw new DuplicateResourceException("Trainer", "personnelId", dto.getPersonnelId().toString());
            }
        }

        Trainer trainer = trainerMapper.toEntity(dto);
        trainer.setPersonnel(personnel);
        trainer.setCreatedBy(auditUtil.getCurrentUser());
        trainer.setCreatedAt(java.time.LocalDateTime.now());

        Trainer saved = trainerRepository.save(trainer);
        log.info("Trainer created with ID: {}", saved.getId());

        return trainerMapper.toDTO(saved);
    }

    public TrainerDTO updateTrainer(Long id, TrainerUpdateDTO dto) {
        log.info("Updating trainer with ID: {}", id);

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", id));

        // Update personnel if changed
        if (dto.getPersonnelId() != null && !dto.getPersonnelId().equals(
                trainer.getPersonnel() != null ? trainer.getPersonnel().getId() : null)) {
            Personnel personnel = personnelRepository.findById(dto.getPersonnelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", dto.getPersonnelId()));
            trainer.setPersonnel(personnel);
        }

        trainerMapper.updateEntity(dto, trainer);
        trainer.setUpdatedBy(auditUtil.getCurrentUser());
        trainer.setUpdatedAt(java.time.LocalDateTime.now());

        Trainer updated = trainerRepository.save(trainer);
        log.info("Trainer updated: {}", id);

        return trainerMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public TrainerDTO getTrainerById(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", id));
        return trainerMapper.toDTO(trainer);
    }

    @Transactional(readOnly = true)
    public Page<TrainerDTO> getAllTrainers(Pageable pageable) {
        return trainerRepository.findAll(pageable)
                .map(trainerMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<TrainerDTO> getAllActiveTrainers() {
        return trainerRepository.findByActiveTrue().stream()
                .map(trainerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TrainerDTO> searchTrainers(String searchTerm, Pageable pageable) {
        return trainerRepository.searchTrainers(searchTerm, pageable)
                .map(trainerMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<TrainerDTO> advancedSearch(String type, String specialization, Pageable pageable) {
        Trainer.TrainerType trainerType = type != null ? Trainer.TrainerType.valueOf(type.toUpperCase()) : null;
        return trainerRepository.advancedSearch(trainerType, specialization, pageable)
                .map(trainerMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<TrainerDTO> getTrainersByType(String type) {
        Trainer.TrainerType trainerType = Trainer.TrainerType.valueOf(type.toUpperCase());
        return trainerRepository.findByTypeAndActiveTrue(trainerType).stream()
                .map(trainerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isTrainerAvailable(Long trainerId, LocalDate startDate, LocalDate endDate) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", trainerId));
        return trainer.isAvailable(startDate, endDate);
    }

    public void deleteTrainer(Long id) {
        log.info("Soft deleting trainer with ID: {}", id);

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", id));

        trainer.softDelete(auditUtil.getCurrentUser());
        trainerRepository.save(trainer);
        log.info("Trainer soft deleted: {}", id);
    }
}




