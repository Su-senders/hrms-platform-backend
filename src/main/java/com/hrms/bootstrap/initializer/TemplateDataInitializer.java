package com.hrms.bootstrap.initializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrms.bootstrap.loader.data.PositionData;
import com.hrms.bootstrap.loader.data.SubStructureData;
import com.hrms.bootstrap.loader.data.TemplateData;
import com.hrms.bootstrap.loader.data.TemplateDataLoader;
import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.OrganizationalPositionTemplate;
import com.hrms.entity.OrganizationalTemplate;
import com.hrms.repository.OrganizationalPositionTemplateRepository;
import com.hrms.repository.OrganizationalTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Initialise les templates organisationnels depuis les fichiers JSON.
 * Doit s'exécuter AVANT GeographicDataInitializer pour que les templates
 * soient disponibles lors de la création des structures territoriales.
 */
@Slf4j
@Component
@Profile("dev")
@Order(2) // Après MinatStructureInitializer (1), avant GeographicDataInitializer (3)
@RequiredArgsConstructor
public class TemplateDataInitializer implements CommandLineRunner {

    private final OrganizationalTemplateRepository templateRepository;
    private final OrganizationalPositionTemplateRepository positionTemplateRepository;
    private final TemplateDataLoader dataLoader;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void run(String... args) {
        // Vérifier si les templates existent déjà
        if (templateRepository.count() > 0) {
            log.info("Organizational templates already initialized, skipping...");
            return;
        }

        log.info("Initializing organizational templates from JSON...");

        try {
            // Charger et créer le template de Gouvernorat
            TemplateData gouvernoratData = dataLoader.loadGovernorateTemplate();
            if (gouvernoratData != null) {
                createOrganizationalTemplate(gouvernoratData);
                log.info("Gouvernorat template created successfully");
            }

            // Charger et créer le template de Préfecture (si disponible)
            TemplateData prefectureData = dataLoader.loadPrefectureTemplate();
            if (prefectureData != null) {
                createOrganizationalTemplate(prefectureData);
                log.info("Prefecture template created successfully");
            }

            // Charger et créer le template de Sous-Préfecture (si disponible)
            TemplateData sousPrefectureData = dataLoader.loadSousPrefectureTemplate();
            if (sousPrefectureData != null) {
                createOrganizationalTemplate(sousPrefectureData);
                log.info("Sous-Prefecture template created successfully");
            }

            long templateCount = templateRepository.count();
            long positionTemplateCount = positionTemplateRepository.count();

            log.info("Organizational templates initialized successfully! Created {} templates with {} position templates",
                templateCount, positionTemplateCount);

        } catch (Exception e) {
            log.error("Error initializing organizational templates", e);
            throw e;
        }
    }

    /**
     * Crée un template organisationnel et ses templates de postes
     */
    private void createOrganizationalTemplate(TemplateData data) {
        log.info("Creating organizational template: {}", data.getCode());

        // 1. Créer le template organisationnel
        OrganizationalTemplate template = OrganizationalTemplate.builder()
            .code(data.getCode())
            .name(data.getName())
            .appliesTo(AdministrativeStructure.StructureType.valueOf(data.getAppliesTo()))
            .description(data.getDescription())
            .version(data.getVersion() != null ? data.getVersion() : 1)
            .active(true)
            .build();

        // Sérialiser la définition de structure en JSON
        try {
            String structureDefinition = objectMapper.writeValueAsString(data);
            template.setStructureDefinition(structureDefinition);

            if (data.getMetadata() != null) {
                String metadata = objectMapper.writeValueAsString(data.getMetadata());
                template.setMetadata(metadata);
            }
        } catch (Exception e) {
            log.error("Error serializing template structure", e);
        }

        template.setCreatedBy("system");
        template.setCreatedDate(LocalDate.now());

        template = templateRepository.save(template);

        // 2. Créer les templates de postes top-level (Gouverneur, Préfet, etc.)
        if (data.getTopLevelPositions() != null) {
            for (PositionData positionData : data.getTopLevelPositions()) {
                createPositionTemplate(template, positionData, null);
            }
        }

        // 3. Créer les templates de postes pour chaque sous-structure
        if (data.getSubStructures() != null) {
            for (SubStructureData subStructure : data.getSubStructures()) {
                createPositionTemplatesForSubStructure(template, subStructure);
            }
        }

        log.info("Created organizational template {} with {} position templates",
            template.getCode(),
            positionTemplateRepository.countByOrganizationalTemplate(template));
    }

    /**
     * Crée récursivement les templates de postes pour une sous-structure
     */
    private void createPositionTemplatesForSubStructure(OrganizationalTemplate template,
                                                        SubStructureData subStructure) {
        // Créer les templates de postes de cette sous-structure
        if (subStructure.getPositions() != null) {
            for (PositionData positionData : subStructure.getPositions()) {
                createPositionTemplate(template, positionData, subStructure.getCode());
            }
        }

        // Traiter récursivement les sous-services
        if (subStructure.getSubServices() != null) {
            for (SubStructureData subService : subStructure.getSubServices()) {
                createPositionTemplatesForSubStructure(template, subService);
            }
        }
    }

    /**
     * Crée un template de poste
     */
    private void createPositionTemplate(OrganizationalTemplate template,
                                       PositionData positionData,
                                       String subStructureCode) {
        OrganizationalPositionTemplate positionTemplate = OrganizationalPositionTemplate.builder()
            .code(positionData.getCode())
            .title(positionData.getTitle())
            .organizationalTemplate(template)
            .subStructureCode(subStructureCode)
            .level(positionData.getLevel())
            .parentPositionCode(positionData.getParentPositionCode())
            .count(positionData.getCount() != null ? positionData.getCount() : 1)
            .requiredGrade(positionData.getRequiredGrade())
            .requiredCorps(positionData.getRequiredCorps())
            .minimumExperienceYears(positionData.getMinimumExperienceYears())
            .isNominative(positionData.getIsNominative() != null && positionData.getIsNominative())
            .isManagerial(positionData.getIsManagerial() != null && positionData.getIsManagerial())
            .description(positionData.getDescription())
            .responsibilities(positionData.getResponsibilities())
            .requiredQualifications(positionData.getRequiredQualifications())
            .active(true)
            .build();

        // Déterminer la catégorie
        if (positionData.getCategory() != null) {
            try {
                positionTemplate.setCategory(
                    OrganizationalPositionTemplate.PositionCategory.valueOf(positionData.getCategory())
                );
            } catch (IllegalArgumentException e) {
                log.warn("Invalid category '{}' for position {}, defaulting to ADMINISTRATIF",
                    positionData.getCategory(), positionData.getCode());
                positionTemplate.setCategory(OrganizationalPositionTemplate.PositionCategory.ADMINISTRATIF);
            }
        }

        positionTemplate.setCreatedBy("system");
        positionTemplate.setCreatedDate(LocalDate.now());

        positionTemplateRepository.save(positionTemplate);
    }

    /**
     * Compte le nombre de position templates pour un template donné
     * (méthode helper pour éviter une requête séparée)
     */
    private long countByOrganizationalTemplate(OrganizationalTemplate template) {
        return positionTemplateRepository.findByOrganizationalTemplate(template).size();
    }
}

