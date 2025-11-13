package com.hrms.service;

import com.hrms.dto.*;
import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.Personnel;
import com.hrms.entity.Position;
import com.hrms.exception.BusinessException;
import com.hrms.exception.DuplicateResourceException;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.PositionMapper;
import com.hrms.repository.AdministrativeStructureRepository;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.PositionRepository;
import com.hrms.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PositionService {

    private final PositionRepository positionRepository;
    private final AdministrativeStructureRepository structureRepository;
    private final PersonnelRepository personnelRepository;
    private final PositionMapper positionMapper;
    private final AuditUtil auditUtil;

    /**
     * Create new position
     */
    public PositionDTO createPosition(PositionCreateDTO dto) {
        log.info("Creating position with code: {}", dto.getCode());

        // Check for duplicate code
        if (positionRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Position", "code", dto.getCode());
        }

        Position position = positionMapper.toEntity(dto);

        // Set structure
        AdministrativeStructure structure = structureRepository.findById(dto.getStructureId())
                .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", dto.getStructureId()));
        position.setStructure(structure);

        position.setCreatedBy(auditUtil.getCurrentUser());
        position.setCreatedDate(LocalDate.now());

        Position saved = positionRepository.save(position);

        // Update structure position statistics
        updateStructureStatistics(structure.getId());

        log.info("Position created successfully with ID: {}", saved.getId());
        return positionMapper.toDTO(saved);
    }

    /**
     * Update position
     */
    public PositionDTO updatePosition(Long id, PositionUpdateDTO dto) {
        log.info("Updating position with ID: {}", id);

        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", id));

        // Check for duplicate code if changed
        if (dto.getCode() != null && !dto.getCode().equals(position.getCode())) {
            if (positionRepository.existsByCode(dto.getCode())) {
                throw new DuplicateResourceException("Position", "code", dto.getCode());
            }
        }

        // Update structure if changed
        if (dto.getStructureId() != null && !dto.getStructureId().equals(position.getStructure().getId())) {
            AdministrativeStructure newStructure = structureRepository.findById(dto.getStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", dto.getStructureId()));

            Long oldStructureId = position.getStructure().getId();
            position.setStructure(newStructure);

            // Update both old and new structure statistics
            updateStructureStatistics(oldStructureId);
            updateStructureStatistics(newStructure.getId());
        }

        positionMapper.updateEntity(dto, position);
        position.setUpdatedBy(auditUtil.getCurrentUser());
        position.setUpdatedDate(LocalDate.now());

        Position updated = positionRepository.save(position);
        log.info("Position updated successfully: {}", id);

        return positionMapper.toDTO(updated);
    }

    /**
     * Get position by ID
     */
    @Transactional(readOnly = true)
    public PositionDTO getPositionById(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", id));
        return positionMapper.toDTO(position);
    }

    /**
     * Get position by code
     */
    @Transactional(readOnly = true)
    public PositionDTO getPositionByCode(String code) {
        Position position = positionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "code", code));
        return positionMapper.toDTO(position);
    }

    /**
     * Search positions
     */
    @Transactional(readOnly = true)
    public Page<PositionDTO> searchPositions(PositionSearchDTO searchDTO, Pageable pageable) {
        log.info("Searching positions with criteria: {}", searchDTO);

        Page<Position> positionPage;

        if (searchDTO.getSearchTerm() != null && !searchDTO.getSearchTerm().isEmpty()) {
            positionPage = positionRepository.searchPositions(searchDTO.getSearchTerm(), pageable);
        } else {
            Position.PositionStatus status = searchDTO.getStatus() != null ?
                    Position.PositionStatus.valueOf(searchDTO.getStatus().toUpperCase()) : null;

            positionPage = positionRepository.advancedSearch(
                    searchDTO.getCode(),
                    searchDTO.getTitle(),
                    searchDTO.getRank(),
                    searchDTO.getCategory(),
                    searchDTO.getStructureId(),
                    status,
                    pageable
            );
        }

        return positionPage.map(positionMapper::toDTO);
    }

    /**
     * Get all positions
     */
    @Transactional(readOnly = true)
    public Page<PositionDTO> getAllPositions(Pageable pageable) {
        return positionRepository.findAll(pageable).map(positionMapper::toDTO);
    }

    /**
     * Get vacant positions
     */
    @Transactional(readOnly = true)
    public List<PositionDTO> getVacantPositions() {
        log.info("Fetching vacant positions");
        return positionRepository.findVacantPositions().stream()
                .map(positionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get vacant positions by structure
     */
    @Transactional(readOnly = true)
    public List<PositionDTO> getVacantPositionsByStructure(Long structureId) {
        return positionRepository.findVacantPositionsByStructure(structureId).stream()
                .map(positionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get occupied positions
     */
    @Transactional(readOnly = true)
    public List<PositionDTO> getOccupiedPositions() {
        log.info("Fetching occupied positions");
        return positionRepository.findOccupiedPositions().stream()
                .map(positionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get positions by status
     */
    @Transactional(readOnly = true)
    public Page<PositionDTO> getPositionsByStatus(String status, Pageable pageable) {
        Position.PositionStatus enumStatus = Position.PositionStatus.valueOf(status.toUpperCase());
        return positionRepository.findByStatusAndDeletedFalse(enumStatus, pageable)
                .map(positionMapper::toDTO);
    }

    /**
     * Get positions by rank
     */
    @Transactional(readOnly = true)
    public Page<PositionDTO> getPositionsByRank(String rank, Pageable pageable) {
        return positionRepository.findByRankAndDeletedFalse(rank, pageable)
                .map(positionMapper::toDTO);
    }

    /**
     * Get positions by structure
     */
    @Transactional(readOnly = true)
    public Page<PositionDTO> getPositionsByStructure(Long structureId, Pageable pageable) {
        return positionRepository.findByStructureId(structureId, pageable)
                .map(positionMapper::toDTO);
    }

    /**
     * Assign personnel to position
     */
    public PositionDTO assignPersonnelToPosition(Long positionId, Long personnelId) {
        log.info("Assigning personnel {} to position {}", personnelId, positionId);

        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", positionId));

        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", personnelId));

        if (!position.isAvailableForAssignment()) {
            throw new BusinessException("Le poste n'est pas disponible pour affectation");
        }

        if (!personnel.canBeAssignedToPosition()) {
            throw new BusinessException("Le personnel ne peut pas être affecté à ce poste");
        }

        // Release old position if exists
        if (personnel.getCurrentPosition() != null && !personnel.getOfficialCumul()) {
            Position oldPosition = personnel.getCurrentPosition();
            oldPosition.releasePersonnel();
            positionRepository.save(oldPosition);
        }

        // Assign to new position
        position.assignPersonnel(personnel);
        personnel.setCurrentPosition(position);
        personnel.setServiceStartDate(LocalDate.now());

        positionRepository.save(position);
        personnelRepository.save(personnel);

        // Update structure statistics
        updateStructureStatistics(position.getStructure().getId());

        log.info("Personnel assigned successfully to position");
        return positionMapper.toDTO(position);
    }

    /**
     * Release personnel from position
     */
    public PositionDTO releasePersonnelFromPosition(Long positionId) {
        log.info("Releasing personnel from position {}", positionId);

        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", positionId));

        if (position.getCurrentPersonnel() == null) {
            throw new BusinessException("Aucun personnel n'est affecté à ce poste");
        }

        Personnel personnel = position.getCurrentPersonnel();
        position.releasePersonnel();
        personnel.setCurrentPosition(null);

        positionRepository.save(position);
        personnelRepository.save(personnel);

        // Update structure statistics
        updateStructureStatistics(position.getStructure().getId());

        log.info("Personnel released successfully from position");
        return positionMapper.toDTO(position);
    }

    /**
     * Soft delete position
     */
    public void deletePosition(Long id) {
        log.info("Soft deleting position with ID: {}", id);

        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", id));

        // Check if position is occupied
        if (position.getCurrentPersonnel() != null) {
            throw new BusinessException("Impossible de supprimer un poste occupé. Veuillez d'abord libérer le poste.");
        }

        position.setDeleted(true);
        position.setDeletedAt(LocalDateTime.now());
        position.setDeletedBy(auditUtil.getCurrentUser());
        position.setActive(false);

        positionRepository.save(position);

        // Update structure statistics
        updateStructureStatistics(position.getStructure().getId());

        log.info("Position soft deleted successfully: {}", id);
    }

    /**
     * Get position statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPositionStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total", positionRepository.count());
        stats.put("vacant", positionRepository.findVacantPositions().size());
        stats.put("occupied", positionRepository.findOccupiedPositions().size());

        // By status
        Map<String, Long> byStatus = new HashMap<>();
        positionRepository.countByStatus().forEach(obj -> {
            byStatus.put(obj[0].toString(), (Long) obj[1]);
        });
        stats.put("byStatus", byStatus);

        return stats;
    }

    /**
     * Update structure position statistics
     */
    private void updateStructureStatistics(Long structureId) {
        long total = positionRepository.countByStructureId(structureId);
        long occupied = positionRepository.countOccupiedByStructureId(structureId);

        AdministrativeStructure structure = structureRepository.findById(structureId).orElse(null);
        if (structure != null) {
            structure.updatePositionStatistics((int) total, (int) occupied);
            structureRepository.save(structure);
        }
    }
}
