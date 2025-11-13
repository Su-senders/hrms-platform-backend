package com.hrms.service;

import com.hrms.dto.*;
import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.Personnel;
import com.hrms.entity.Position;
import com.hrms.entity.Region;
import com.hrms.entity.Department;
import com.hrms.entity.Arrondissement;
import com.hrms.exception.BusinessException;
import com.hrms.exception.DuplicateResourceException;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.PersonnelMapper;
import com.hrms.repository.AdministrativeStructureRepository;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.PositionRepository;
import com.hrms.repository.RegionRepository;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.ArrondissementRepository;
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
public class PersonnelService {

    private final PersonnelRepository personnelRepository;
    private final PositionRepository positionRepository;
    private final AdministrativeStructureRepository structureRepository;
    private final RegionRepository regionRepository;
    private final DepartmentRepository departmentRepository;
    private final ArrondissementRepository arrondissementRepository;
    private final PersonnelMapper personnelMapper;
    private final AuditUtil auditUtil;

    // Nouveaux services de validation et calculs
    private final GeographicValidationService geographicValidationService;
    private final DateValidationService dateValidationService;
    private final SeniorityCalculationService seniorityCalculationService;
    private final AssignmentHistoryService assignmentHistoryService;

    /**
     * Create new personnel with duplicate check
     */
    public PersonnelDTO createPersonnel(PersonnelCreateDTO dto) {
        log.info("Creating personnel with matricule: {}", dto.getMatricule());

        // Check for duplicates
        checkDuplicates(dto.getMatricule(), dto.getCniNumber(), dto.getFirstName(),
                       dto.getLastName(), dto.getDateOfBirth(), null);

        // Valider la cohérence géographique
        geographicValidationService.validateGeographicCoherence(
            dto.getRegionOrigineId(),
            dto.getDepartmentOrigineId(),
            dto.getArrondissementOrigineId()
        );

        Personnel personnel = personnelMapper.toEntity(dto);

        // Valider la cohérence des dates
        dateValidationService.validatePersonnelDates(personnel);

        // Set structure
        if (dto.getStructureId() != null) {
            AdministrativeStructure structure = structureRepository.findById(dto.getStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", dto.getStructureId()));
            personnel.setStructure(structure);
        }

        // Set position if specified
        if (dto.getCurrentPositionId() != null) {
            Position position = positionRepository.findById(dto.getCurrentPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Position", "id", dto.getCurrentPositionId()));

            if (!position.isAvailableForAssignment()) {
                throw new BusinessException("Le poste n'est pas disponible pour affectation");
            }

            // Check if personnel can be assigned to position
            if (personnel.getCurrentPosition() != null && !personnel.getOfficialCumul()) {
                throw new BusinessException("Le personnel a déjà un poste. Le cumul officiel est requis.");
            }

            position.assignPersonnel(personnel);
            positionRepository.save(position);
            personnel.setCurrentPosition(position);
        }

        // Set origines géographiques
        if (dto.getRegionOrigineId() != null) {
            Region region = regionRepository.findById(dto.getRegionOrigineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Region", "id", dto.getRegionOrigineId()));
            personnel.setRegionOrigine(region);
        }

        if (dto.getDepartmentOrigineId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentOrigineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", dto.getDepartmentOrigineId()));
            personnel.setDepartmentOrigine(department);
        }

        if (dto.getArrondissementOrigineId() != null) {
            Arrondissement arrondissement = arrondissementRepository.findById(dto.getArrondissementOrigineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Arrondissement", "id", dto.getArrondissementOrigineId()));
            personnel.setArrondissementOrigine(arrondissement);
        }

        personnel.setCreatedBy(auditUtil.getCurrentUser());
        personnel.setCreatedDate(LocalDate.now());

        Personnel saved = personnelRepository.save(personnel);
        log.info("Personnel created successfully with ID: {}", saved.getId());

        // Enregistrer l'affectation initiale dans l'historique
        if (saved.getCurrentPosition() != null || saved.getStructure() != null) {
            try {
                assignmentHistoryService.recordAssignment(
                    saved,
                    null, // Pas de poste précédent
                    saved.getCurrentPosition(),
                    null, // Pas de structure précédente
                    saved.getStructure(),
                    saved.getAppointmentDate() != null ? saved.getAppointmentDate() : LocalDate.now(),
                    com.hrms.entity.AssignmentHistory.MovementType.AFFECTATION,
                    "Affectation initiale"
                );
                log.info("Affectation initiale enregistrée dans l'historique");
            } catch (Exception e) {
                log.warn("Impossible d'enregistrer l'affectation initiale dans l'historique: {}", e.getMessage());
            }
        }

        return personnelMapper.toDTO(saved);
    }

    /**
     * Update personnel
     */
    public PersonnelDTO updatePersonnel(Long id, PersonnelUpdateDTO dto) {
        log.info("Updating personnel with ID: {}", id);

        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", id));

        // Check for duplicates if matricule or CNI changed
        if (dto.getMatricule() != null || dto.getCniNumber() != null) {
            checkDuplicates(dto.getMatricule(), dto.getCniNumber(), dto.getFirstName(),
                           dto.getLastName(), dto.getDateOfBirth(), id);
        }

        // Update structure if changed
        if (dto.getStructureId() != null && !dto.getStructureId().equals(personnel.getStructure().getId())) {
            AdministrativeStructure structure = structureRepository.findById(dto.getStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", dto.getStructureId()));
            personnel.setStructure(structure);
        }

        // Update position if changed
        if (dto.getCurrentPositionId() != null) {
            Long currentPosId = personnel.getCurrentPosition() != null ?
                               personnel.getCurrentPosition().getId() : null;

            if (!dto.getCurrentPositionId().equals(currentPosId)) {
                Position newPosition = positionRepository.findById(dto.getCurrentPositionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Position", "id", dto.getCurrentPositionId()));

                if (!newPosition.isAvailableForAssignment()) {
                    throw new BusinessException("Le poste n'est pas disponible pour affectation");
                }

                // Release old position
                if (personnel.getCurrentPosition() != null) {
                    Position oldPosition = personnel.getCurrentPosition();
                    oldPosition.releasePersonnel();
                    positionRepository.save(oldPosition);
                }

                newPosition.assignPersonnel(personnel);
                positionRepository.save(newPosition);
                personnel.setCurrentPosition(newPosition);
            }
        }

        personnelMapper.updateEntity(dto, personnel);
        personnel.setUpdatedBy(auditUtil.getCurrentUser());
        personnel.setUpdatedDate(LocalDate.now());

        Personnel updated = personnelRepository.save(personnel);
        log.info("Personnel updated successfully: {}", id);

        return personnelMapper.toDTO(updated);
    }

    /**
     * Get personnel by ID
     */
    @Transactional(readOnly = true)
    public PersonnelDTO getPersonnelById(Long id) {
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", id));
        return personnelMapper.toDTO(personnel);
    }

    /**
     * Get personnel by matricule
     */
    @Transactional(readOnly = true)
    public PersonnelDTO getPersonnelByMatricule(String matricule) {
        Personnel personnel = personnelRepository.findByMatricule(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "matricule", matricule));
        return personnelMapper.toDTO(personnel);
    }

    /**
     * Search personnel with multiple criteria
     */
    @Transactional(readOnly = true)
    public Page<PersonnelDTO> searchPersonnel(PersonnelSearchDTO searchDTO, Pageable pageable) {
        log.info("Searching personnel with criteria: {}", searchDTO);

        Page<Personnel> personnelPage;

        // If only search term is provided, use simple search
        if (searchDTO.getSearchTerm() != null && !searchDTO.getSearchTerm().isEmpty()) {
            personnelPage = personnelRepository.searchByNameOrMatricule(searchDTO.getSearchTerm(), pageable);
        }
        // Otherwise use advanced search
        else {
            Personnel.PersonnelSituation situation = searchDTO.getSituation() != null ?
                    Personnel.PersonnelSituation.valueOf(searchDTO.getSituation().toUpperCase()) : null;

            // Convert grade and corps strings to IDs if provided
            Long gradeId = null;
            if (searchDTO.getGrade() != null && !searchDTO.getGrade().isEmpty()) {
                try {
                    gradeId = Long.parseLong(searchDTO.getGrade());
                } catch (NumberFormatException e) {
                    // If not a number, try to find by name or code
                    log.debug("Grade '{}' is not a number, treating as name/code", searchDTO.getGrade());
                    // For now, we'll skip grade search if it's not a valid ID
                    // TODO: Add lookup by name/code if needed
                }
            }

            Long corpsId = null;
            if (searchDTO.getCorps() != null && !searchDTO.getCorps().isEmpty()) {
                try {
                    corpsId = Long.parseLong(searchDTO.getCorps());
                } catch (NumberFormatException e) {
                    // If not a number, try to find by name or code
                    log.debug("Corps '{}' is not a number, treating as name/code", searchDTO.getCorps());
                    // For now, we'll skip corps search if it's not a valid ID
                    // TODO: Add lookup by name/code if needed
                }
            }

            personnelPage = personnelRepository.advancedSearch(
                    searchDTO.getMatricule(),
                    searchDTO.getFirstName(),
                    searchDTO.getLastName(),
                    searchDTO.getPositionId(),
                    gradeId,
                    corpsId,
                    searchDTO.getStructureId(),
                    situation,
                    null, // status
                    null, // regionOrigineId
                    null, // departmentOrigineId
                    pageable
            );
        }

        return personnelPage.map(personnelMapper::toDTO);
    }

    /**
     * Get all personnel (paginated)
     */
    @Transactional(readOnly = true)
    public Page<PersonnelDTO> getAllPersonnel(Pageable pageable) {
        return personnelRepository.findAll(pageable).map(personnelMapper::toDTO);
    }

    /**
     * Get personnel by situation
     */
    @Transactional(readOnly = true)
    public Page<PersonnelDTO> getPersonnelBySituation(String situation, Pageable pageable) {
        Personnel.PersonnelSituation enumSituation = Personnel.PersonnelSituation.valueOf(situation.toUpperCase());
        return personnelRepository.findBySituationAndDeletedFalse(enumSituation, pageable)
                .map(personnelMapper::toDTO);
    }

    /**
     * Get personnel by grade
     */
    @Transactional(readOnly = true)
    public Page<PersonnelDTO> getPersonnelByGrade(String grade, Pageable pageable) {
        return personnelRepository.findByGradeAndDeletedFalse(grade, pageable)
                .map(personnelMapper::toDTO);
    }

    /**
     * Get personnel by structure
     */
    @Transactional(readOnly = true)
    public Page<PersonnelDTO> getPersonnelByStructure(Long structureId, Pageable pageable) {
        return personnelRepository.findByStructureId(structureId, pageable)
                .map(personnelMapper::toDTO);
    }

    /**
     * Get retirable personnel for current year
     */
    @Transactional(readOnly = true)
    public List<PersonnelDTO> getRetirableThisYear() {
        log.info("Fetching retirable personnel for current year");
        return personnelRepository.findRetirableThisYear().stream()
                .map(personnelMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get retirable personnel for next year
     */
    @Transactional(readOnly = true)
    public List<PersonnelDTO> getRetirableNextYear() {
        log.info("Fetching retirable personnel for next year");
        return personnelRepository.findRetirableNextYear().stream()
                .map(personnelMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Soft delete personnel (keeps data)
     */
    public void deletePersonnel(Long id) {
        log.info("Soft deleting personnel with ID: {}", id);

        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", id));

        // Release position if assigned
        if (personnel.getCurrentPosition() != null) {
            Position position = personnel.getCurrentPosition();
            position.releasePersonnel();
            positionRepository.save(position);
            personnel.setCurrentPosition(null);
        }

        personnel.setDeleted(true);
        personnel.setDeletedAt(LocalDateTime.now());
        personnel.setDeletedBy(auditUtil.getCurrentUser());

        personnelRepository.save(personnel);
        log.info("Personnel soft deleted successfully: {}", id);
    }

    /**
     * Check for duplicate personnel
     */
    private void checkDuplicates(String matricule, String cniNumber, String firstName,
                                 String lastName, LocalDate dateOfBirth, Long excludeId) {
        // Check matricule
        if (matricule != null && personnelRepository.existsByMatricule(matricule)) {
            if (excludeId == null) {
                throw new DuplicateResourceException("Personnel", "matricule", matricule);
            } else {
                Personnel existing = personnelRepository.findByMatricule(matricule).orElse(null);
                if (existing != null && !existing.getId().equals(excludeId)) {
                    throw new DuplicateResourceException("Personnel", "matricule", matricule);
                }
            }
        }

        // Check for duplicate person (same name + date of birth or same CNI)
        if (personnelRepository.checkDuplicate(matricule, cniNumber, firstName, lastName, dateOfBirth)) {
            if (excludeId == null) {
                throw new DuplicateResourceException("Un personnel avec ces informations existe déjà");
            }
        }
    }

    /**
     * Get personnel statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPersonnelStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total count
        stats.put("total", personnelRepository.count());

        // By situation
        Map<String, Long> bySituation = new HashMap<>();
        for (Personnel.PersonnelSituation situation : Personnel.PersonnelSituation.values()) {
            long count = personnelRepository.countBySituationAndDeletedFalse(situation);
            bySituation.put(situation.name(), count);
        }
        stats.put("bySituation", bySituation);

        // By grade
        Map<String, Long> byGrade = new HashMap<>();
        personnelRepository.countByGrade().forEach(obj -> {
            byGrade.put((String) obj[0], (Long) obj[1]);
        });
        stats.put("byGrade", byGrade);

        // By corps
        Map<String, Long> byCorps = new HashMap<>();
        personnelRepository.countByCorps().forEach(obj -> {
            byCorps.put((String) obj[0], (Long) obj[1]);
        });
        stats.put("byCorps", byCorps);

        // Retirable
        stats.put("retirableThisYear", personnelRepository.findRetirableThisYear().size());
        stats.put("retirableNextYear", personnelRepository.findRetirableNextYear().size());

        return stats;
    }

    /**
     * Check if personnel can be assigned to position
     */
    @Transactional(readOnly = true)
    public boolean canAssignToPosition(Long personnelId, Long positionId) {
        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", personnelId));

        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", positionId));

        return personnel.canBeAssignedToPosition() && position.isAvailableForAssignment();
    }

    // ==================== NOUVELLES MÉTHODES AVEC SERVICES ENRICHIS ====================

    /**
     * Obtient les détails d'ancienneté d'un personnel
     */
    @Transactional(readOnly = true)
    public SeniorityDetailsDTO getPersonnelSeniority(Long personnelId) {
        log.info("Calcul de l'ancienneté pour le personnel {}", personnelId);
        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", personnelId));

        return seniorityCalculationService.calculateSeniorityDetails(personnel);
    }

    /**
     * Obtient l'historique des affectations d'un personnel
     */
    @Transactional(readOnly = true)
    public List<com.hrms.entity.AssignmentHistory> getPersonnelAssignmentHistory(Long personnelId) {
        log.info("Récupération de l'historique des affectations pour le personnel {}", personnelId);
        return assignmentHistoryService.getPersonnelAssignmentHistory(personnelId);
    }

    /**
     * Valide la cohérence géographique d'un personnel
     */
    public void validateGeography(Long regionId, Long departmentId, Long arrondissementId) {
        geographicValidationService.validateGeographicCoherence(regionId, departmentId, arrondissementId);
    }
}
