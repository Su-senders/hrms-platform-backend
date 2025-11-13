package com.hrms.service;

import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.Position;
import com.hrms.entity.PositionTemplate;
import com.hrms.repository.PositionRepository;
import com.hrms.repository.PositionTemplateRepository;
import com.hrms.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour gérer les modèles de postes et créer des postes à partir de ces modèles
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PositionTemplateService {

    private final PositionTemplateRepository templateRepository;
    private final PositionRepository positionRepository;
    private final AuditUtil auditUtil;

    /**
     * Create positions from templates for a structure
     * Only creates positions that don't already exist
     */
    public List<Position> createPositionsFromTemplates(AdministrativeStructure structure) {
        log.info("Creating positions from templates for structure: {}", structure.getName());

        // Get applicable templates for this structure type
        List<PositionTemplate> templates = templateRepository.findApplicableTemplates(
                structure.getType().name()
        );

        log.info("Found {} applicable templates for structure type: {}",
                templates.size(), structure.getType());

        return templates.stream()
                .filter(template -> shouldCreatePosition(template, structure))
                .map(template -> createPositionFromTemplate(template, structure))
                .collect(Collectors.toList());
    }

    /**
     * Create positions that should be auto-created for a structure
     */
    public List<Position> createAutoPositions(AdministrativeStructure structure) {
        log.info("Creating auto positions for structure: {}", structure.getName());

        List<PositionTemplate> autoTemplates = templateRepository.findAutoCreateTemplates();

        return autoTemplates.stream()
                .filter(template -> template.isApplicableTo(structure.getType()))
                .filter(template -> shouldCreatePosition(template, structure))
                .map(template -> createPositionFromTemplate(template, structure))
                .collect(Collectors.toList());
    }

    /**
     * Create a single position from a template
     */
    public Position createPositionFromTemplate(Long templateId, Long structureId) {
        PositionTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        // Would need to fetch structure here
        // This is a simplified version
        return null; // Implement with proper structure fetching
    }

    /**
     * Get all templates applicable to a structure type
     */
    @Transactional(readOnly = true)
    public List<PositionTemplate> getApplicableTemplates(AdministrativeStructure.StructureType structureType) {
        return templateRepository.findApplicableTemplates(structureType.name());
    }

    /**
     * Get all active templates
     */
    @Transactional(readOnly = true)
    public List<PositionTemplate> getAllActiveTemplates() {
        return templateRepository.findByActiveTrue();
    }

    /**
     * Check if a position should be created from a template
     */
    private boolean shouldCreatePosition(PositionTemplate template, AdministrativeStructure structure) {
        // Check if template is applicable to this structure type
        if (!template.isApplicableTo(structure.getType())) {
            return false;
        }

        // If it's unique per structure, check if it already exists
        if (template.getIsUniquePerStructure()) {
            String positionCode = generatePositionCode(template, structure);
            return !positionRepository.existsByCode(positionCode);
        }

        return true;
    }

    /**
     * Create a position from a template
     */
    private Position createPositionFromTemplate(PositionTemplate template, AdministrativeStructure structure) {
        String positionCode = generatePositionCode(template, structure);

        Position position = Position.builder()
                .code(positionCode)
                .title(template.getTitle())
                .description(template.getDescription())
                .structure(structure)
                .rank(template.getRank())
                .category(template.getCategory())
                .requiredGrade(template.getRequiredGrade())
                .requiredCorps(template.getRequiredCorps())
                .status(Position.PositionStatus.VACANT)
                .responsibilities(template.getResponsibilities())
                .requiredQualifications(template.getRequiredQualifications())
                .minExperienceYears(template.getMinExperienceYears())
                .isManagerial(template.getIsManagerial())
                .active(true)
                .build();

        position.setCreatedBy(auditUtil.getCurrentUser());
        position.setCreatedDate(LocalDate.now());

        Position saved = positionRepository.save(position);
        log.info("Created position: {} in structure: {}", saved.getTitle(), structure.getName());

        return saved;
    }

    /**
     * Generate a unique position code
     */
    private String generatePositionCode(PositionTemplate template, AdministrativeStructure structure) {
        // Format: STRUCTURE_CODE-TEMPLATE_CODE-SEQ
        String baseCode = structure.getCode() + "-" + template.getCode();

        // If unique, no sequence needed
        if (template.getIsUniquePerStructure()) {
            return baseCode;
        }

        // Find next sequence number
        long count = positionRepository.findByStructureId(
                structure.getId(),
                org.springframework.data.domain.Pageable.unpaged()
        ).getTotalElements();

        return baseCode + "-" + String.format("%03d", count + 1);
    }

    /**
     * Bulk create positions for multiple structures
     */
    @Transactional
    public void createPositionsForAllStructures(List<AdministrativeStructure> structures) {
        log.info("Creating positions for {} structures", structures.size());

        structures.forEach(structure -> {
            try {
                List<Position> created = createAutoPositions(structure);
                log.info("Created {} positions for structure: {}", created.size(), structure.getName());
            } catch (Exception e) {
                log.error("Error creating positions for structure: {}", structure.getName(), e);
            }
        });

        log.info("Finished creating positions for all structures");
    }

    /**
     * Get position statistics by template
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Long> getPositionStatisticsByTemplate() {
        // This would require additional repository methods
        // Simplified version
        return new java.util.HashMap<>();
    }
}
