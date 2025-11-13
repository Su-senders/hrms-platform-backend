package com.hrms.bootstrap.initializer;

import com.hrms.bootstrap.loader.data.ArrondissementData;
import com.hrms.bootstrap.loader.data.DepartmentData;
import com.hrms.bootstrap.loader.data.RegionData;
import com.hrms.bootstrap.loader.data.TerritorialDataLoader;
import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.Arrondissement;
import com.hrms.entity.Department;
import com.hrms.entity.Region;
import com.hrms.repository.AdministrativeStructureRepository;
import com.hrms.repository.ArrondissementRepository;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Initialise les entités géographiques du Cameroun:
 * - 10 Régions
 * - 58 Départements
 * - ~360 Arrondissements
 *
 * Puis crée les structures administratives associées:
 * - Gouvernorats (liés aux régions)
 * - Préfectures (liées aux départements)
 * - Sous-Préfectures (liées aux arrondissements)
 */
@Slf4j
@Component
@Profile("dev")
@Order(3) // S'exécute après MinatStructureInitializer (1) et TemplateDataInitializer (2)
@RequiredArgsConstructor
public class GeographicDataInitializer implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final DepartmentRepository departmentRepository;
    private final ArrondissementRepository arrondissementRepository;
    private final AdministrativeStructureRepository structureRepository;
    private final TerritorialDataLoader dataLoader;
    private final com.hrms.service.StructureTemplateService templateService;

    @Override
    @Transactional
    public void run(String... args) {
        // Check if geographic data already exists
        if (regionRepository.count() > 0) {
            log.info("Geographic data already initialized, skipping...");
            return;
        }

        log.info("Initializing Cameroon geographic data...");

        try {
            AdministrativeStructure minat = structureRepository.findByCode("MINAT")
                    .orElseThrow(() -> new RuntimeException("MINAT structure not found"));

            // Charger toutes les régions depuis le fichier JSON
            List<RegionData> regions = dataLoader.loadRegions();

            for (RegionData regionData : regions) {
                initializeRegion(minat, regionData);
            }

            log.info("Cameroon geographic data initialized successfully!");
            long totalRegions = regionRepository.count();
            long totalDepartments = departmentRepository.count();
            long totalArrondissements = arrondissementRepository.count();
            log.info("Created: {} régions, {} départements, {} arrondissements",
                    totalRegions, totalDepartments, totalArrondissements);

        } catch (Exception e) {
            log.error("Error initializing geographic data", e);
            throw e;
        }
    }

    /**
     * Initialise une région complète avec ses départements et arrondissements,
     * puis crée les structures administratives associées
     */
    private void initializeRegion(AdministrativeStructure minat, RegionData regionData) {
        log.info("Initializing region: {} ({})", regionData.getName(), regionData.getCode());

        // 1. Créer l'entité géographique Region
        Region region = createRegion(regionData);

        // 2. Créer la structure administrative Gouvernorat liée à cette région
        AdministrativeStructure gouvernorat = createGouvernorat(minat, region, regionData);

        // 3. Créer les départements de cette région
        if (regionData.getDepartments() != null) {
            for (DepartmentData departmentData : regionData.getDepartments()) {
                initializeDepartment(gouvernorat, region, departmentData, regionData.getCode());
            }
        }
    }

    /**
     * Créer une entité géographique Region
     */
    private Region createRegion(RegionData regionData) {
        Region region = Region.builder()
                .code(regionData.getCode().replace("GOUV-", "")) // Nettoyer le code (GOUV-AD -> AD)
                .name(regionData.getRegion())
                .chefLieu(regionData.getChefLieu())
                .description("Région " + regionData.getRegion() + " - Chef-lieu: " + regionData.getChefLieu())
                .active(true)
                .build();

        region.setCreatedBy("system");
        region.setCreatedDate(LocalDate.now());

        return regionRepository.save(region);
    }

    /**
     * Créer un Gouvernorat (structure administrative) lié à une région géographique
     * Utilise le template organisationnel pour créer automatiquement les sous-structures et postes
     */
    private AdministrativeStructure createGouvernorat(AdministrativeStructure parent,
                                                      Region region, RegionData regionData) {
        AdministrativeStructure gouvernorat = AdministrativeStructure.builder()
                .code(regionData.getCode()) // GOUV-AD, GOUV-CE, etc.
                .name(regionData.getName()) // "Gouvernorat de la Région de l'Adamaoua"
                .type(AdministrativeStructure.StructureType.GOUVERNORAT)
                .parentStructure(parent)
                .region(region) // LIEN VERS L'ENTITÉ GÉOGRAPHIQUE
                .city(regionData.getChefLieu())
                .description("Gouvernorat de la région " + region.getName() + " - Chef-lieu: " + region.getChefLieu())
                .active(true)
                .occupiedPositions(0)
                .vacantPositions(0)
                .build();

        gouvernorat.setCreatedBy("system");
        gouvernorat.setCreatedDate(LocalDate.now());

        gouvernorat = structureRepository.save(gouvernorat);

        // Instancier le template de Gouvernorat pour créer les sous-structures et postes
        try {
            templateService.instantiateGovernorateTemplate(region, gouvernorat);
            log.info("Template instantiated for Gouvernorat: {}", gouvernorat.getCode());
        } catch (Exception e) {
            log.error("Error instantiating template for Gouvernorat {}: {}",
                gouvernorat.getCode(), e.getMessage());
            // Continue sans template si erreur
        }

        return gouvernorat;
    }

    /**
     * Initialise un département avec ses arrondissements et leurs structures administratives
     */
    private void initializeDepartment(AdministrativeStructure gouvernorat, Region region,
                                     DepartmentData departmentData, String regionCode) {
        // 1. Créer l'entité géographique Department
        Department department = createDepartment(region, departmentData, regionCode);

        // 2. Créer la structure administrative Préfecture liée à ce département
        AdministrativeStructure prefecture = createPrefecture(gouvernorat, department, departmentData);

        // 2.1 Instancier le template de Préfecture pour créer les sous-structures et postes
        try {
            templateService.instantiatePrefectureTemplate(department, prefecture);
            log.info("Template instantiated for Prefecture: {}", prefecture.getCode());
        } catch (Exception e) {
            log.error("Error instantiating template for Prefecture {}: {}",
                prefecture.getCode(), e.getMessage());
            // Continue sans template si erreur
        }

        // 3. Charger les arrondissements depuis le fichier JSON
        String cleanRegionCode = regionCode.replace("GOUV-", "");
        List<ArrondissementData> arrondissements = dataLoader.loadArrondissementsForDepartment(
            cleanRegionCode,
            departmentData.getName()
        );

        // Si pas d'arrondissements dans le fichier, créer au moins celui du chef-lieu
        if (arrondissements.isEmpty()) {
            log.warn("No arrondissements found for department {} in region {}, creating default",
                departmentData.getName(), cleanRegionCode);
            arrondissements = List.of(
                new ArrondissementData(departmentData.getChefLieu() + " 1er", departmentData.getChefLieu())
            );
        }

        // 4. Créer les arrondissements et leurs sous-préfectures
        for (ArrondissementData arrondissementData : arrondissements) {
            initializeArrondissement(prefecture, department, arrondissementData, cleanRegionCode);
        }

        log.debug("Created Department: {} with {} arrondissements",
            departmentData.getName(), arrondissements.size());
    }

    /**
     * Créer une entité géographique Department
     */
    private Department createDepartment(Region region, DepartmentData departmentData, String regionCode) {
        String cleanRegionCode = regionCode.replace("GOUV-", "");
        String code = cleanRegionCode + "-" + generateDepartmentCode(departmentData.getName());

        Department department = Department.builder()
                .code(code)
                .name(departmentData.getName())
                .chefLieu(departmentData.getChefLieu())
                .region(region) // LIEN VERS LA RÉGION
                .description("Département " + departmentData.getName() + " - Chef-lieu: " + departmentData.getChefLieu())
                .active(true)
                .build();

        department.setCreatedBy("system");
        department.setCreatedDate(LocalDate.now());

        return departmentRepository.save(department);
    }

    /**
     * Créer une Préfecture (structure administrative) liée à un département géographique
     */
    private AdministrativeStructure createPrefecture(AdministrativeStructure gouvernorat,
                                                     Department department,
                                                     DepartmentData departmentData) {
        String code = "PREF-" + department.getCode();

        AdministrativeStructure prefecture = AdministrativeStructure.builder()
                .code(code)
                .name("Préfecture du " + department.getName())
                .type(AdministrativeStructure.StructureType.PREFECTURE)
                .parentStructure(gouvernorat)
                .department(department) // LIEN VERS L'ENTITÉ GÉOGRAPHIQUE
                .city(departmentData.getChefLieu())
                .description("Préfecture du département " + department.getName() +
                           " - Chef-lieu: " + departmentData.getChefLieu())
                .active(true)
                .occupiedPositions(0)
                .vacantPositions(0)
                .build();

        prefecture.setCreatedBy("system");
        prefecture.setCreatedDate(LocalDate.now());

        return structureRepository.save(prefecture);
    }

    /**
     * Initialise un arrondissement et sa sous-préfecture
     */
    private void initializeArrondissement(AdministrativeStructure prefecture,
                                         Department department,
                                         ArrondissementData arrondissementData,
                                         String regionCode) {
        // 1. Créer l'entité géographique Arrondissement
        Arrondissement arrondissement = createArrondissement(department, arrondissementData, regionCode);

        // 2. Créer la structure administrative Sous-Préfecture liée à cet arrondissement
        createSousPrefecture(prefecture, arrondissement, arrondissementData);
    }

    /**
     * Créer une entité géographique Arrondissement
     */
    private Arrondissement createArrondissement(Department department,
                                               ArrondissementData arrondissementData,
                                               String regionCode) {
        String code = regionCode + "-" +
                     generateDepartmentCode(department.getName()) + "-" +
                     generateArrondissementCode(arrondissementData.getName());

        Arrondissement arrondissement = Arrondissement.builder()
                .code(code)
                .name(arrondissementData.getName())
                .chefLieu(arrondissementData.getChefLieu())
                .department(department) // LIEN VERS LE DÉPARTEMENT
                .type(determineArrondissementType(arrondissementData.getName()))
                .description("Arrondissement " + arrondissementData.getName() +
                           " - Chef-lieu: " + arrondissementData.getChefLieu())
                .active(true)
                .build();

        arrondissement.setCreatedBy("system");
        arrondissement.setCreatedDate(LocalDate.now());

        return arrondissementRepository.save(arrondissement);
    }

    /**
     * Créer une Sous-Préfecture (structure administrative) liée à un arrondissement géographique
     */
    private void createSousPrefecture(AdministrativeStructure prefecture,
                                     Arrondissement arrondissement,
                                     ArrondissementData arrondissementData) {
        String code = "SPREF-" + arrondissement.getCode();

        AdministrativeStructure sousPrefecture = AdministrativeStructure.builder()
                .code(code)
                .name("Sous-Préfecture de " + arrondissement.getName())
                .type(AdministrativeStructure.StructureType.SOUS_PREFECTURE)
                .parentStructure(prefecture)
                .arrondissement(arrondissement) // LIEN VERS L'ENTITÉ GÉOGRAPHIQUE
                .city(arrondissementData.getChefLieu())
                .description("Sous-Préfecture de l'arrondissement " + arrondissement.getName() +
                           " - Chef-lieu: " + arrondissementData.getChefLieu())
                .active(true)
                .occupiedPositions(0)
                .vacantPositions(0)
                .build();

        sousPrefecture.setCreatedBy("system");
        sousPrefecture.setCreatedDate(LocalDate.now());

        sousPrefecture = structureRepository.save(sousPrefecture);

        // Instancier le template de Sous-Préfecture pour créer les sous-structures et postes
        try {
            templateService.instantiateSousPrefectureTemplate(arrondissement, sousPrefecture);
            log.info("Template instantiated for Sous-Prefecture: {}", sousPrefecture.getCode());
        } catch (Exception e) {
            log.error("Error instantiating template for Sous-Prefecture {}: {}",
                sousPrefecture.getCode(), e.getMessage());
            // Continue sans template si erreur
        }
    }

    /**
     * Générer un code court pour un département
     */
    private String generateDepartmentCode(String departmentName) {
        return departmentName.replaceAll("[^A-Z]", "")
                .substring(0, Math.min(4, departmentName.replaceAll("[^A-Z]", "").length()));
    }

    /**
     * Générer un code court pour un arrondissement
     */
    private String generateArrondissementCode(String arrondissementName) {
        return arrondissementName.replaceAll("[^A-Z0-9]", "")
                .substring(0, Math.min(6, arrondissementName.replaceAll("[^A-Z0-9]", "").length()));
    }

    /**
     * Déterminer le type d'arrondissement
     */
    private Arrondissement.ArrondissementType determineArrondissementType(String name) {
        if (name.contains("1er") || name.contains("2ème") || name.contains("3ème") ||
            name.contains("4ème") || name.contains("5ème") || name.contains("6ème") ||
            name.contains("7ème")) {
            return Arrondissement.ArrondissementType.URBAIN;
        }
        return Arrondissement.ArrondissementType.NORMAL;
    }
}

