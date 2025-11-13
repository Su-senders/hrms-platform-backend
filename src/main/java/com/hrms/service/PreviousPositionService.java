package com.hrms.service;

import com.hrms.dto.PreviousPositionDTO;
import com.hrms.dto.PreviousPositionCreateDTO;
import com.hrms.dto.PreviousPositionUpdateDTO;
import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.Grade;
import com.hrms.entity.Personnel;
import com.hrms.entity.PreviousPosition;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.PreviousPositionMapper;
import com.hrms.repository.AdministrativeStructureRepository;
import com.hrms.repository.GradeRepository;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.PreviousPositionRepository;
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
public class PreviousPositionService {

    private final PreviousPositionRepository positionRepository;
    private final PersonnelRepository personnelRepository;
    private final AdministrativeStructureRepository structureRepository;
    private final GradeRepository gradeRepository;
    private final PreviousPositionMapper positionMapper;
    private final AuditUtil auditUtil;

    public PreviousPositionDTO createPreviousPosition(PreviousPositionCreateDTO dto) {
        log.info("Creating previous position for personnel ID: {}", dto.getPersonnelId());

        Personnel personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", dto.getPersonnelId()));

        PreviousPosition position = positionMapper.toEntity(dto);
        position.setPersonnel(personnel);

        // Set structure if provided
        if (dto.getStructureId() != null) {
            AdministrativeStructure structure = structureRepository.findById(dto.getStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("AdministrativeStructure", "id", dto.getStructureId()));
            position.setStructure(structure);
        }

        // Set grade if provided
        if (dto.getGradeId() != null) {
            Grade grade = gradeRepository.findById(dto.getGradeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", dto.getGradeId()));
            position.setGrade(grade);
        }

        position.setCreatedBy(auditUtil.getCurrentUser());
        position.setCreatedDate(LocalDate.now());

        PreviousPosition saved = positionRepository.save(position);
        log.info("Previous position created with ID: {}", saved.getId());

        return positionMapper.toDTO(saved);
    }

    public PreviousPositionDTO updatePreviousPosition(Long id, PreviousPositionUpdateDTO dto) {
        log.info("Updating previous position with ID: {}", id);

        PreviousPosition position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PreviousPosition", "id", id));

        positionMapper.updateEntity(dto, position);

        // Update structure if provided
        if (dto.getStructureId() != null) {
            AdministrativeStructure structure = structureRepository.findById(dto.getStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("AdministrativeStructure", "id", dto.getStructureId()));
            position.setStructure(structure);
        }

        // Update grade if provided
        if (dto.getGradeId() != null) {
            Grade grade = gradeRepository.findById(dto.getGradeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", dto.getGradeId()));
            position.setGrade(grade);
        }

        position.setUpdatedBy(auditUtil.getCurrentUser());
        position.setUpdatedDate(LocalDate.now());

        PreviousPosition updated = positionRepository.save(position);
        log.info("Previous position updated: {}", id);

        return positionMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public PreviousPositionDTO getPreviousPositionById(Long id) {
        PreviousPosition position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PreviousPosition", "id", id));
        return positionMapper.toDTO(position);
    }

    @Transactional(readOnly = true)
    public List<PreviousPositionDTO> getPreviousPositionsByPersonnel(Long personnelId) {
        log.info("Fetching previous positions for personnel ID: {}", personnelId);
        return positionRepository.findByPersonnelId(personnelId).stream()
                .map(positionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PreviousPositionDTO> getPreviousPositionsByPersonnel(Long personnelId, Pageable pageable) {
        return positionRepository.findByPersonnelId(personnelId, pageable)
                .map(positionMapper::toDTO);
    }

    /**
     * Obtenir les postes antérieurs dans les 3 dernières années
     */
    @Transactional(readOnly = true)
    public List<PreviousPositionDTO> getLastThreeYearsPositions(Long personnelId) {
        LocalDate threeYearsAgo = LocalDate.now().minusYears(3);
        return positionRepository.findLastThreeYearsByPersonnelId(personnelId, threeYearsAgo).stream()
                .map(positionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deletePreviousPosition(Long id) {
        log.info("Soft deleting previous position with ID: {}", id);

        PreviousPosition position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PreviousPosition", "id", id));

        position.setDeleted(true);
        position.setDeletedAt(java.time.LocalDateTime.now());
        position.setDeletedBy(auditUtil.getCurrentUser());

        positionRepository.save(position);
        log.info("Previous position soft deleted: {}", id);
    }
}

