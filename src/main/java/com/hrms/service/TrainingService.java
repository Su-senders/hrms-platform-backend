package com.hrms.service;

import com.hrms.dto.TrainingCreateDTO;
import com.hrms.dto.TrainingDTO;
import com.hrms.dto.TrainingUpdateDTO;
import com.hrms.entity.Training;
import com.hrms.exception.DuplicateResourceException;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.TrainingMapper;
import com.hrms.repository.TrainingRepository;
import com.hrms.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;
    private final AuditUtil auditUtil;

    public TrainingDTO createTraining(TrainingCreateDTO dto) {
        log.info("Creating training with code: {}", dto.getCode());

        // Check for duplicate code
        if (trainingRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Training", "code", dto.getCode());
        }

        Training training = trainingMapper.toEntity(dto);
        training.setCreatedBy(auditUtil.getCurrentUser());
        training.setCreatedAt(java.time.LocalDateTime.now());

        Training saved = trainingRepository.save(training);
        log.info("Training created with ID: {}", saved.getId());

        return trainingMapper.toDTO(saved);
    }

    public TrainingDTO updateTraining(Long id, TrainingUpdateDTO dto) {
        log.info("Updating training with ID: {}", id);

        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training", "id", id));

        trainingMapper.updateEntity(dto, training);
        training.setUpdatedBy(auditUtil.getCurrentUser());
        training.setUpdatedAt(java.time.LocalDateTime.now());

        Training updated = trainingRepository.save(training);
        log.info("Training updated: {}", id);

        return trainingMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public TrainingDTO getTrainingById(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training", "id", id));
        return trainingMapper.toDTO(training);
    }

    @Transactional(readOnly = true)
    public Page<TrainingDTO> getAllTrainings(Pageable pageable) {
        return trainingRepository.findAll(pageable)
                .map(trainingMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<TrainingDTO> getAllActiveTrainings() {
        return trainingRepository.findByActiveTrue().stream()
                .map(trainingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TrainingDTO> searchTrainings(String searchTerm, Pageable pageable) {
        return trainingRepository.searchTrainings(searchTerm, pageable)
                .map(trainingMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<TrainingDTO> advancedSearch(String category, String trainingField, Pageable pageable) {
        Training.TrainingCategory trainingCategory = category != null ?
                Training.TrainingCategory.valueOf(category.toUpperCase()) : null;
        return trainingRepository.advancedSearch(trainingCategory, trainingField, pageable)
                .map(trainingMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<TrainingDTO> getTrainingsByCategory(String category) {
        Training.TrainingCategory trainingCategory = Training.TrainingCategory.valueOf(category.toUpperCase());
        return trainingRepository.findByCategory(trainingCategory).stream()
                .map(trainingMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteTraining(Long id) {
        log.info("Soft deleting training with ID: {}", id);

        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training", "id", id));

        training.softDelete(auditUtil.getCurrentUser());
        trainingRepository.save(training);
        log.info("Training soft deleted: {}", id);
    }
}




