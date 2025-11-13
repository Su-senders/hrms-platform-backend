package com.hrms.service;

import com.hrms.dto.ProfessionalTrainingDTO;
import com.hrms.dto.ProfessionalTrainingCreateDTO;
import com.hrms.dto.ProfessionalTrainingUpdateDTO;
import com.hrms.entity.Personnel;
import com.hrms.entity.ProfessionalTraining;
import com.hrms.entity.ProfessionalTraining.TrainingStatus;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.ProfessionalTrainingMapper;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.ProfessionalTrainingRepository;
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
public class ProfessionalTrainingService {

    private final ProfessionalTrainingRepository trainingRepository;
    private final PersonnelRepository personnelRepository;
    private final ProfessionalTrainingMapper trainingMapper;
    private final AuditUtil auditUtil;

    public ProfessionalTrainingDTO createTraining(ProfessionalTrainingCreateDTO dto) {
        log.info("Creating professional training for personnel ID: {}", dto.getPersonnelId());

        Personnel personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", dto.getPersonnelId()));

        ProfessionalTraining training = trainingMapper.toEntity(dto);
        training.setPersonnel(personnel);
        training.setCreatedBy(auditUtil.getCurrentUser());
        training.setCreatedDate(LocalDate.now());

        ProfessionalTraining saved = trainingRepository.save(training);
        log.info("Professional training created with ID: {}", saved.getId());

        return trainingMapper.toDTO(saved);
    }

    public ProfessionalTrainingDTO updateTraining(Long id, ProfessionalTrainingUpdateDTO dto) {
        log.info("Updating professional training with ID: {}", id);

        ProfessionalTraining training = trainingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProfessionalTraining", "id", id));

        trainingMapper.updateEntity(dto, training);
        training.setUpdatedBy(auditUtil.getCurrentUser());
        training.setUpdatedDate(LocalDate.now());

        ProfessionalTraining updated = trainingRepository.save(training);
        log.info("Professional training updated: {}", id);

        return trainingMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public ProfessionalTrainingDTO getTrainingById(Long id) {
        ProfessionalTraining training = trainingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProfessionalTraining", "id", id));
        return trainingMapper.toDTO(training);
    }

    @Transactional(readOnly = true)
    public List<ProfessionalTrainingDTO> getTrainingsByPersonnel(Long personnelId) {
        log.info("Fetching trainings for personnel ID: {}", personnelId);
        return trainingRepository.findByPersonnelId(personnelId).stream()
                .map(trainingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProfessionalTrainingDTO> getTrainingsByPersonnel(Long personnelId, Pageable pageable) {
        return trainingRepository.findByPersonnelId(personnelId, pageable)
                .map(trainingMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<ProfessionalTrainingDTO> getInProgressTrainings() {
        return trainingRepository.findInProgressTrainings().stream()
                .map(trainingMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteTraining(Long id) {
        log.info("Soft deleting professional training with ID: {}", id);

        ProfessionalTraining training = trainingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProfessionalTraining", "id", id));

        training.setDeleted(true);
        training.setDeletedAt(java.time.LocalDateTime.now());
        training.setDeletedBy(auditUtil.getCurrentUser());

        trainingRepository.save(training);
        log.info("Professional training soft deleted: {}", id);
    }
}

