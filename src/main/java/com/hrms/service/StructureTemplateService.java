package com.hrms.service;

import com.hrms.bootstrap.loader.data.PositionData;
import com.hrms.bootstrap.loader.data.SubStructureData;
import com.hrms.entity.*;
import com.hrms.repository.AdministrativeStructureRepository;
import com.hrms.repository.OrganizationalPositionTemplateRepository;
import com.hrms.repository.OrganizationalTemplateRepository;
import com.hrms.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de gestion des templates organisationnels.
 * Responsable de l'instanciation des templates pour créer les structures
 * et postes dans les Gouvernorats, Préfectures et Sous-Préfectures.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StructureTemplateService {

    private final OrganizationalTemplateRepository templateRepository;
    private final OrganizationalPositionTemplateRepository positionTemplateRepository;
    private final AdministrativeStructureRepository structureRepository;
    private final PositionRepository positionRepository;

    /**
     * Instancie le template de Gouvernorat pour une région donnée
     */
    @Transactional
    public void instantiateGovernorateTemplate(Region region, AdministrativeStructure gouvernorat) {
        log.info("Instantiating Gouvernorat template for region: {}", region.getName());

        // 1. Charger le template
        OrganizationalTemplate template = templateRepository
            .findByCode("TPL-GOUV")
            .orElseThrow(() -> new RuntimeException("Gouvernorat template (TPL-GOUV) not found"));

        // 2. Lier le gouvernorat au template
        gouvernorat.setOrganizationalTemplateId(template.getId());
        structureRepository.save(gouvernorat);

        // 3. Créer le poste de Gouverneur (top level)
        createTopLevelPositions(template, gouvernorat, region.getCode());

        // 4. Créer les sous-structures et leurs postes
        List<OrganizationalPositionTemplate> positionTemplates =
            positionTemplateRepository.findByOrganizationalTemplateAndActiveTrue(template);

        instantiateSubStructuresAndPositions(template, gouvernorat, positionTemplates, region.getCode());

        log.info("Gouvernorat template instantiated successfully for region: {} ({} positions created)",
            region.getName(), countPositionsInStructure(gouvernorat));
    }

    /**
     * Instancie le template de Préfecture pour un département donné
     */
    @Transactional
    public void instantiatePrefectureTemplate(Department department, AdministrativeStructure prefecture) {
        log.info("Instantiating Prefecture template for department: {}", department.getName());

        OrganizationalTemplate template = templateRepository
            .findByCode("TPL-PREF")
            .orElseThrow(() -> new RuntimeException("Prefecture template (TPL-PREF) not found"));

        prefecture.setOrganizationalTemplateId(template.getId());
        structureRepository.save(prefecture);

        createTopLevelPositions(template, prefecture, department.getCode());

        List<OrganizationalPositionTemplate> positionTemplates =
            positionTemplateRepository.findByOrganizationalTemplateAndActiveTrue(template);

        instantiateSubStructuresAndPositions(template, prefecture, positionTemplates, department.getCode());

        log.info("Prefecture template instantiated successfully for department: {}", department.getName());
    }

    /**
     * Instancie le template de Sous-Préfecture pour un arrondissement donné
     */
    @Transactional
    public void instantiateSousPrefectureTemplate(Arrondissement arrondissement,
                                                  AdministrativeStructure sousPrefecture) {
        log.info("Instantiating Sous-Prefecture template for arrondissement: {}", arrondissement.getName());

        OrganizationalTemplate template = templateRepository
            .findByCode("TPL-SPREF")
            .orElseThrow(() -> new RuntimeException("Sous-Prefecture template (TPL-SPREF) not found"));

        sousPrefecture.setOrganizationalTemplateId(template.getId());
        structureRepository.save(sousPrefecture);

        createTopLevelPositions(template, sousPrefecture, arrondissement.getCode());

        List<OrganizationalPositionTemplate> positionTemplates =
            positionTemplateRepository.findByOrganizationalTemplateAndActiveTrue(template);

        instantiateSubStructuresAndPositions(template, sousPrefecture, positionTemplates,
            arrondissement.getCode());

        log.info("Sous-Prefecture template instantiated successfully for arrondissement: {}",
            arrondissement.getName());
    }

    /**
     * Crée les postes de niveau supérieur (Gouverneur, Préfet, Sous-Préfet)
     */
    private void createTopLevelPositions(OrganizationalTemplate template,
                                        AdministrativeStructure structure,
                                        String geoCode) {
        // Récupérer les postes de niveau 0 (postes de tête)
        List<OrganizationalPositionTemplate> topLevelTemplates =
            positionTemplateRepository.findByTemplateAndLevel(template, 0);

        for (OrganizationalPositionTemplate positionTemplate : topLevelTemplates) {
            for (int i = 0; i < positionTemplate.getCount(); i++) {
                createPositionFromTemplate(positionTemplate, structure, null, geoCode, i + 1);
            }
        }
    }

    /**
     * Instancie les sous-structures et leurs postes
     */
    private void instantiateSubStructuresAndPositions(OrganizationalTemplate template,
                                                      AdministrativeStructure parentStructure,
                                                      List<OrganizationalPositionTemplate> positionTemplates,
                                                      String geoCode) {
        // Grouper les position templates par sub_structure_code
        Map<String, List<OrganizationalPositionTemplate>> positionsBySubStructure = new HashMap<>();
        for (OrganizationalPositionTemplate pt : positionTemplates) {
            String subStructureCode = pt.getSubStructureCode();
            if (subStructureCode != null && !subStructureCode.isEmpty()) {
                positionsBySubStructure.computeIfAbsent(subStructureCode, k -> new java.util.ArrayList<>()).add(pt);
            }
        }

        // Créer les sous-structures principales (niveau 2)
        Map<String, AdministrativeStructure> createdStructures = new HashMap<>();

        for (String subStructureCode : positionsBySubStructure.keySet()) {
            // Ignorer les codes qui sont des sous-services (contiennent plus de 2 tirets après GOUV)
            if (isMainSubStructure(subStructureCode)) {
                AdministrativeStructure subStructure = createSubStructure(
                    subStructureCode,
                    parentStructure,
                    template,
                    geoCode
                );
                createdStructures.put(subStructureCode, subStructure);

                // Créer les postes de cette sous-structure
                List<OrganizationalPositionTemplate> positions = positionsBySubStructure.get(subStructureCode);
                for (OrganizationalPositionTemplate pt : positions) {
                    for (int i = 0; i < pt.getCount(); i++) {
                        createPositionFromTemplate(pt, subStructure, parentStructure, geoCode, i + 1);
                    }
                }
            }
        }

        // Créer les sous-services (niveau 3+)
        for (String subStructureCode : positionsBySubStructure.keySet()) {
            if (!isMainSubStructure(subStructureCode)) {
                // Trouver la structure parente
                String parentCode = getParentSubStructureCode(subStructureCode);
                AdministrativeStructure parentSubStructure = createdStructures.get(parentCode);

                if (parentSubStructure != null) {
                    AdministrativeStructure subService = createSubStructure(
                        subStructureCode,
                        parentSubStructure,
                        template,
                        geoCode
                    );
                    createdStructures.put(subStructureCode, subService);

                    // Créer les postes de ce sous-service
                    List<OrganizationalPositionTemplate> positions = positionsBySubStructure.get(subStructureCode);
                    for (OrganizationalPositionTemplate pt : positions) {
                        for (int i = 0; i < pt.getCount(); i++) {
                            createPositionFromTemplate(pt, subService, parentSubStructure, geoCode, i + 1);
                        }
                    }
                }
            }
        }
    }

    /**
     * Vérifie si un code correspond à une structure principale (niveau 2)
     * GOUV-SP = true, GOUV-CABINET-SCOM = false
     */
    private boolean isMainSubStructure(String code) {
        String[] parts = code.split("-");
        return parts.length == 2; // GOUV-SP, GOUV-CABINET, GOUV-IRSR
    }

    /**
     * Extrait le code de la structure parente
     * GOUV-CABINET-SCOM -> GOUV-CABINET
     */
    private String getParentSubStructureCode(String code) {
        String[] parts = code.split("-");
        if (parts.length > 2) {
            return parts[0] + "-" + parts[1];
        }
        return null;
    }

    /**
     * Crée une sous-structure depuis un code de template
     */
    private AdministrativeStructure createSubStructure(String templateCode,
                                                       AdministrativeStructure parent,
                                                       OrganizationalTemplate template,
                                                       String geoCode) {
        // Générer un nom lisible depuis le code
        String name = generateStructureName(templateCode);

        // Générer un code unique: GOUV-CE-SP (Gouvernorat Centre - Secrétariat Particulier)
        String uniqueCode = parent.getCode() + "-" + extractSuffixFromTemplateCode(templateCode);

        // Déterminer le type de structure
        AdministrativeStructure.StructureType structureType = determineStructureType(templateCode);

        AdministrativeStructure subStructure = AdministrativeStructure.builder()
            .code(uniqueCode)
            .name(name)
            .type(structureType)
            .parentStructure(parent)
            .organizationalTemplateId(template.getId())
            .subStructureTemplateCode(templateCode)
            .city(parent.getCity())
            .active(true)
            .occupiedPositions(0)
            .vacantPositions(0)
            .build();

        subStructure.setCreatedBy("system");
        subStructure.setCreatedDate(LocalDate.now());

        return structureRepository.save(subStructure);
    }

    /**
     * Génère un nom de structure depuis le code template
     */
    private String generateStructureName(String templateCode) {
        Map<String, String> names = new HashMap<>();
        // Gouvernorat
        names.put("GOUV-SP", "Secrétariat Particulier");
        names.put("GOUV-CABINET", "Cabinet du Gouverneur");
        names.put("GOUV-CABINET-SCOM", "Service de la Communication");
        names.put("GOUV-CABINET-SPROT", "Service du Protocole");
        names.put("GOUV-CABINET-SSEC", "Service de la Sécurité");
        names.put("GOUV-IRSR", "Inspection Régionale des Services Régionaux");
        names.put("GOUV-SGSG", "Secrétariat Général des Services du Gouverneur");
        names.put("GOUV-SGSG-SACL", "Service de l'Accueil, du Courrier et de Liaison");
        names.put("GOUV-SGSG-SDAT", "Service de la Documentation, des Archives et de la Traduction");
        names.put("GOUV-SGSG-SAFL", "Service des Affaires Financières et de la Logistique");
        names.put("GOUV-SGSG-DAAJ", "Division des Affaires Administratives et Juridiques");
        names.put("GOUV-SGSG-DAESC", "Division des Affaires Économiques, Sociales et Culturelles");
        names.put("GOUV-SGSG-DPOA", "Division de la Police et de l'Organisation Administrative");
        names.put("GOUV-SGSG-DDR", "Division du Développement Régional");

        // Préfecture
        names.put("PREF-SP", "Secrétariat Particulier");
        names.put("PREF-SAG", "Service des Affaires Générales");
        names.put("PREF-SAAJP", "Service des Affaires Administratives, Juridiques et Politiques");
        names.put("PREF-SAEF", "Service des Affaires Économiques et Financières");
        names.put("PREF-SASC", "Service des Affaires Sociales et Culturelles");
        names.put("PREF-SDL", "Service du Développement Local");

        // Sous-Préfecture
        names.put("SPREF-SP", "Secrétariat Particulier");
        names.put("SPREF-BAG", "Bureau des Affaires Générales");
        names.put("SPREF-BAAJP", "Bureau des Affaires Administratives, Juridiques et Politiques");
        names.put("SPREF-BAEFDL", "Bureau des Affaires Économiques, Financières et du Développement Local");

        return names.getOrDefault(templateCode, templateCode);
    }

    /**
     * Extrait le suffixe du code template (GOUV-SP -> SP)
     */
    private String extractSuffixFromTemplateCode(String templateCode) {
        return templateCode.substring(templateCode.indexOf('-') + 1);
    }

    /**
     * Détermine le type de structure depuis le code
     */
    private AdministrativeStructure.StructureType determineStructureType(String templateCode) {
        if (templateCode.contains("-SCOM") || templateCode.contains("-SPROT") ||
            templateCode.contains("-SSEC") || templateCode.contains("-SAA")) {
            return AdministrativeStructure.StructureType.SERVICE;
        } else if (templateCode.endsWith("-SP") || templateCode.endsWith("-CABINET") ||
                   templateCode.endsWith("-IRSR")) {
            return AdministrativeStructure.StructureType.SERVICE;
        }
        return AdministrativeStructure.StructureType.DIVISION;
    }

    /**
     * Crée un poste depuis un template
     */
    private Position createPositionFromTemplate(OrganizationalPositionTemplate template,
                                               AdministrativeStructure structure,
                                               AdministrativeStructure topStructure,
                                               String geoCode,
                                               int index) {
        // Générer code unique: POS-GOUV-CE-GOUVERNEUR
        String code = "POS-" + structure.getCode() + "-" +
                     template.getCode().substring(template.getCode().lastIndexOf('-') + 1);

        if (index > 1) {
            code += "-" + index;
        }

        // Générer le titre avec numérotation si count > 1
        String title = template.getTitle();
        if (template.getCount() != null && template.getCount() > 1) {
            title = template.getTitle() + " N°" + index;
        }

        Position position = Position.builder()
            .code(code)
            .title(title)
            .structure(structure)
            .organizationalPositionTemplateId(template.getId())
            .requiredGrade(template.getRequiredGrade())
            .requiredCorps(template.getRequiredCorps())
            .minExperienceYears(template.getMinimumExperienceYears())
            .status(Position.PositionStatus.VACANT)
            .isNominative(template.getIsNominative() != null && template.getIsNominative())
            .isManagerial(template.getIsManagerial() != null && template.getIsManagerial())
            .build();

        position.setCreatedBy("system");
        position.setCreatedDate(LocalDate.now());

        position = positionRepository.save(position);

        // Mettre à jour le compteur de postes vacants de la structure
        structure.setVacantPositions((structure.getVacantPositions() != null ? structure.getVacantPositions() : 0) + 1);
        structure.setTotalPositions((structure.getTotalPositions() != null ? structure.getTotalPositions() : 0) + 1);
        structureRepository.save(structure);

        return position;
    }

    /**
     * Compte le nombre de postes dans une structure et ses sous-structures
     */
    private long countPositionsInStructure(AdministrativeStructure structure) {
        return positionRepository.countByStructure(structure);
    }
}
