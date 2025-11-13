package com.hrms.service;

import com.hrms.dto.AdministrativeStructureCreateDTO;
import com.hrms.dto.AdministrativeStructureDTO;
import com.hrms.dto.AdministrativeStructureUpdateDTO;
import com.hrms.entity.AdministrativeStructure;
import com.hrms.exception.DuplicateResourceException;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.AdministrativeStructureMapper;
import com.hrms.repository.AdministrativeStructureRepository;
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
public class AdministrativeStructureService {

    private final AdministrativeStructureRepository structureRepository;
    private final AdministrativeStructureMapper structureMapper;
    private final AuditUtil auditUtil;

    /**
     * Create new structure
     */
    public AdministrativeStructureDTO createStructure(AdministrativeStructureCreateDTO dto) {
        log.info("Creating administrative structure with code: {}", dto.getCode());

        // Check for duplicate code
        if (structureRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Structure", "code", dto.getCode());
        }

        AdministrativeStructure structure = structureMapper.toEntity(dto);

        // Set parent structure if specified
        if (dto.getParentStructureId() != null) {
            AdministrativeStructure parent = structureRepository.findById(dto.getParentStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", dto.getParentStructureId()));
            structure.setParentStructure(parent);
        }

        structure.setCreatedBy(auditUtil.getCurrentUser());
        structure.setCreatedDate(LocalDate.now());

        AdministrativeStructure saved = structureRepository.save(structure);
        log.info("Structure created successfully with ID: {}", saved.getId());

        return structureMapper.toDTO(saved);
    }

    /**
     * Update structure
     */
    public AdministrativeStructureDTO updateStructure(Long id, AdministrativeStructureUpdateDTO dto) {
        log.info("Updating structure with ID: {}", id);

        AdministrativeStructure structure = structureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", id));

        // Check for duplicate code if changed
        if (dto.getCode() != null && !dto.getCode().equals(structure.getCode())) {
            if (structureRepository.existsByCode(dto.getCode())) {
                throw new DuplicateResourceException("Structure", "code", dto.getCode());
            }
        }

        // Update parent if changed
        if (dto.getParentStructureId() != null) {
            if (dto.getParentStructureId().equals(id)) {
                throw new IllegalArgumentException("Une structure ne peut pas être son propre parent");
            }

            AdministrativeStructure parent = structureRepository.findById(dto.getParentStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", dto.getParentStructureId()));
            structure.setParentStructure(parent);
        }

        structureMapper.updateEntity(dto, structure);
        structure.setUpdatedBy(auditUtil.getCurrentUser());
        structure.setUpdatedDate(LocalDate.now());

        AdministrativeStructure updated = structureRepository.save(structure);
        log.info("Structure updated successfully: {}", id);

        return structureMapper.toDTO(updated);
    }

    /**
     * Get structure by ID
     */
    @Transactional(readOnly = true)
    public AdministrativeStructureDTO getStructureById(Long id) {
        AdministrativeStructure structure = structureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", id));
        return structureMapper.toDTO(structure);
    }

    /**
     * Get structure by code
     */
    @Transactional(readOnly = true)
    public AdministrativeStructureDTO getStructureByCode(String code) {
        AdministrativeStructure structure = structureRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Structure", "code", code));
        return structureMapper.toDTO(structure);
    }

    /**
     * Get all structures
     */
    @Transactional(readOnly = true)
    public Page<AdministrativeStructureDTO> getAllStructures(Pageable pageable) {
        return structureRepository.findAll(pageable).map(structureMapper::toDTO);
    }

    /**
     * Get all active structures
     */
    @Transactional(readOnly = true)
    public List<AdministrativeStructureDTO> getAllActiveStructures() {
        return structureRepository.findByActiveTrue().stream()
                .map(structureMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get structures by type
     */
    @Transactional(readOnly = true)
    public List<AdministrativeStructureDTO> getStructuresByType(String type) {
        AdministrativeStructure.StructureType structureType =
                AdministrativeStructure.StructureType.valueOf(type.toUpperCase());
        return structureRepository.findByTypeAndActiveTrue(structureType).stream()
                .map(structureMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get structures by level
     */
    @Transactional(readOnly = true)
    public List<AdministrativeStructureDTO> getStructuresByLevel(Integer level) {
        return structureRepository.findByLevelAndActiveTrue(level).stream()
                .map(structureMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get root structures (no parent)
     */
    @Transactional(readOnly = true)
    public List<AdministrativeStructureDTO> getRootStructures() {
        return structureRepository.findRootStructures().stream()
                .map(structureMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get children of a structure
     */
    @Transactional(readOnly = true)
    public List<AdministrativeStructureDTO> getChildren(Long parentId) {
        return structureRepository.findChildren(parentId).stream()
                .map(structureMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get structures by parent
     */
    @Transactional(readOnly = true)
    public List<AdministrativeStructureDTO> getStructuresByParent(Long parentId) {
        return structureRepository.findByParentStructureId(parentId).stream()
                .map(structureMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search structures
     */
    @Transactional(readOnly = true)
    public Page<AdministrativeStructureDTO> searchStructures(String searchTerm, Pageable pageable) {
        return structureRepository.searchStructures(searchTerm, pageable)
                .map(structureMapper::toDTO);
    }

    /**
     * Delete structure (soft delete)
     */
    public void deleteStructure(Long id) {
        log.info("Deleting structure with ID: {}", id);

        AdministrativeStructure structure = structureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", id));

        // Check if structure has children
        List<AdministrativeStructure> children = structureRepository.findChildren(id);
        if (!children.isEmpty()) {
            throw new IllegalStateException(
                "Impossible de supprimer une structure qui a des sous-structures"
            );
        }

        structure.setDeleted(true);
        structure.setDeletedAt(LocalDateTime.now());
        structure.setDeletedBy(auditUtil.getCurrentUser());
        structure.setActive(false);

        structureRepository.save(structure);
        log.info("Structure deleted successfully: {}", id);
    }

    /**
     * Get structure statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStructureStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total count
        stats.put("total", structureRepository.count());

        // By type
        Map<String, Long> byType = new HashMap<>();
        structureRepository.countByType().forEach(obj -> {
            byType.put(obj[0].toString(), (Long) obj[1]);
        });
        stats.put("byType", byType);

        // By level
        Map<String, Long> byLevel = new HashMap<>();
        structureRepository.countByLevel().forEach(obj -> {
            byLevel.put("Level " + obj[0], (Long) obj[1]);
        });
        stats.put("byLevel", byLevel);

        return stats;
    }

    /**
     * Get structure hierarchy tree (for a given structure and all its descendants)
     */
    @Transactional(readOnly = true)
    public List<AdministrativeStructureDTO> getHierarchyTree(Long structureId) {
        return structureRepository.findAllDescendants(structureId).stream()
                .map(structureMapper::toDTO)
                .collect(Collectors.toList());
    }
}
