package com.hrms.bootstrap.initializer;

import com.hrms.bootstrap.loader.data.AdministrativeStructureLoader;
import com.hrms.bootstrap.loader.data.OrganizationData;
import com.hrms.bootstrap.loader.data.StructureData;
import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.AdministrativeStructure.StructureType;
import com.hrms.repository.AdministrativeStructureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Initialise la structure organisationnelle complète du MINAT depuis les fichiers JSON
 * Les données sont chargées depuis: src/main/resources/data/administrative/minat/structure.json
 * 
 * S'exécute automatiquement au démarrage si la base est vide
 */
@Slf4j
@Component
@Profile("dev") // Active uniquement en mode dev
@Order(1) // S'exécute en premier
@RequiredArgsConstructor
public class MinatStructureInitializer implements CommandLineRunner {

    private final AdministrativeStructureRepository structureRepository;
    private final AdministrativeStructureLoader dataLoader;

    @Override
    @Transactional
    public void run(String... args) {
        // Check if structures already exist
        if (structureRepository.count() > 0) {
            log.info("Structures already initialized, skipping...");
            return;
        }

        log.info("Initializing MINAT organizational structure from JSON...");

        try {
            // Charger la structure depuis le fichier JSON
            OrganizationData organization = dataLoader.loadMinatStructure().getOrganization();

            // Créer la structure racine (MINAT)
            AdministrativeStructure minat = createStructure(
                    organization.getCode(),
                    organization.getName(),
                    StructureType.valueOf(organization.getType()),
                    null,
                    organization.getDescription()
            );

            // Créer récursivement toutes les sous-structures
            if (organization.getStructures() != null) {
                for (StructureData structureData : organization.getStructures()) {
                    createStructureRecursive(structureData, minat);
                }
            }

            log.info("MINAT organizational structure initialized successfully from JSON!");

        } catch (Exception e) {
            log.error("Error initializing MINAT structure", e);
            throw e;
        }
    }

    /**
     * Crée récursivement une structure et ses sous-structures
     */
    private AdministrativeStructure createStructureRecursive(StructureData structureData, AdministrativeStructure parent) {
        AdministrativeStructure structure = createStructure(
                structureData.getCode(),
                structureData.getName(),
                StructureType.valueOf(structureData.getType()),
                parent,
                structureData.getDescription()
        );

        // Créer récursivement les sous-structures
        if (structureData.getStructures() != null && !structureData.getStructures().isEmpty()) {
            for (StructureData subStructureData : structureData.getStructures()) {
                createStructureRecursive(subStructureData, structure);
            }
        }

        return structure;
    }

    /**
     * Crée une structure administrative
     */
    private AdministrativeStructure createStructure(String code, String name, StructureType type,
                                                   AdministrativeStructure parent, String description) {
        AdministrativeStructure structure = AdministrativeStructure.builder()
                .code(code)
                .name(name)
                .type(type)
                .parentStructure(parent)
                .description(description)
                .active(true)
                .occupiedPositions(0)
                .vacantPositions(0)
                .build();

        structure.setCreatedBy("system");
        structure.setCreatedDate(LocalDate.now());

        return structureRepository.save(structure);
    }
}

