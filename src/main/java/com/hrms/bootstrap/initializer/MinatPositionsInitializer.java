package com.hrms.bootstrap.initializer;

import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.AdministrativeStructure.StructureType;
import com.hrms.entity.Position;
import com.hrms.entity.PositionTemplate;
import com.hrms.entity.PositionTemplate.ApplicableStructureType;
import com.hrms.repository.AdministrativeStructureRepository;
import com.hrms.repository.PositionRepository;
import com.hrms.repository.PositionTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Initialise les postes spécifiques pour chaque structure du MINAT
 * Crée automatiquement les postes de direction selon la hiérarchie :
 * - 1 Ministre pour MINAT
 * - 1 Directeur pour chaque Direction
 * - 1 Chef de Division pour chaque Division
 * - 1 Sous-directeur pour chaque Sous-direction
 * - 1 Chef de cellule pour chaque Cellule
 */
@Slf4j
@Component
@Profile("dev")
@Order(4) // S'exécute après les structures et les templates
@RequiredArgsConstructor
public class MinatPositionsInitializer implements CommandLineRunner {

    private final AdministrativeStructureRepository structureRepository;
    private final PositionRepository positionRepository;
    private final PositionTemplateRepository templateRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Vérifier si les postes sont déjà créés
        if (positionRepository.count() > 0) {
            log.info("Positions already initialized, skipping...");
            return;
        }

        log.info("Initializing MINAT positions for each structure...");

        try {
            // Récupérer toutes les structures du MINAT
            AdministrativeStructure minat = structureRepository.findByCode("MINAT")
                    .orElseThrow(() -> new RuntimeException("MINAT structure not found"));

            // Créer les postes pour toutes les structures récursivement
            createPositionsRecursive(minat);

            // Créer le poste de Ministre (après création du Cabinet)
            createMinisterPosition(minat);

            log.info("MINAT positions initialized successfully!");

        } catch (Exception e) {
            log.error("Error initializing MINAT positions", e);
            throw e;
        }
    }

    /**
     * Crée récursivement les postes pour une structure et ses sous-structures
     */
    private void createPositionsRecursive(AdministrativeStructure structure) {
        // Créer le poste de direction pour cette structure selon son type
        createPositionForStructure(structure);

        // Créer les postes multiples selon la description
        createMultiplePositionsFromDescription(structure);

        // Récursivement créer les postes pour les sous-structures
        List<AdministrativeStructure> children = structureRepository.findByParentStructureId(structure.getId());
        if (children != null && !children.isEmpty()) {
            for (AdministrativeStructure child : children) {
                createPositionsRecursive(child);
            }
        }
    }

    /**
     * Crée le poste approprié pour une structure selon son type
     */
    private void createPositionForStructure(AdministrativeStructure structure) {
        StructureType type = structure.getType();
        String structureCode = structure.getCode();

        // Ne pas créer de poste pour MINAT (déjà créé)
        if (structureCode.equals("MINAT")) {
            return;
        }

        PositionTemplate template = null;
        String positionTitle = null;

        // Déterminer le poste selon le type et le code de la structure
        if (type == StructureType.DIRECTION) {
            // Vérifier si c'est le Secrétariat Général
            if (structureCode.equals("MINAT-SG")) {
                template = getOrCreateTemplate("POST-SG", "Secrétaire Général",
                        "Responsable de la coordination administrative du Ministère",
                        "Secrétaire Général", true, true);
            }
            // Vérifier si c'est le Cabinet du Ministre
            else if (structureCode.equals("MINAT-CABINET")) {
                template = getOrCreateTemplate("POST-CHEF-CABINET", "Chef de Cabinet",
                        "Chef de Cabinet du Ministre",
                        "Chef de Cabinet", true, true);
            }
            // Vérifier si c'est le Secrétariat Particulier (ancien ou dans le Cabinet)
            else if (structureCode.equals("MINAT-SP") || structureCode.equals("MINAT-CABINET-SP")) {
                template = getOrCreateTemplate("POST-CHEF-SP", "Chef de Secrétariat Particulier",
                        "Chef de Secrétariat Particulier",
                        "Chef de Service", true, true);
            }
            // Vérifier si c'est l'Inspection Générale principale
            else if (structureCode.equals("MINAT-IG")) {
                // Pas de poste pour l'IG principale, seulement pour les sous-structures
                return;
            }
            // Vérifier si c'est une Inspection Générale spécifique (IGAT, IGQE, IGS)
            else if (structureCode.matches("MINAT-IG-.*")) {
                template = getOrCreateTemplate("POST-IG", "Inspecteur Général",
                        "Inspecteur Général",
                        "Inspecteur Général", true, true);
            }
            // Vérifier si c'est une Division (code commence par MINAT-SG-D)
            else if (structureCode.matches("MINAT-SG-D[A-Z]+")) {
                template = getOrCreateTemplate("POST-CHEF-DIVISION", "Chef de Division",
                        "Chef de Division",
                        "Chef de Division", true, true);
            }
            // Vérifier si c'est une Sous-direction (code contient -SD)
            else if (structureCode.contains("-SD")) {
                template = getOrCreateTemplate("POST-SOUS-DIRECTEUR", "Sous-Directeur",
                        "Sous-Directeur",
                        "Sous-Directeur", true, true);
            }
            // Sinon, c'est une Direction principale
            else if (structureCode.startsWith("MINAT-D")) {
                template = getOrCreateTemplate("POST-DIRECTEUR", "Directeur",
                        "Directeur de Direction",
                        "Directeur", true, true);
            }
        } else if (type == StructureType.SERVICE) {
            // Vérifier si c'est une Cellule (code contient -C)
            if (structureCode.matches("MINAT-.*-C[A-Z]+")) {
                template = getOrCreateTemplate("POST-CHEF-CELLULE", "Chef de Cellule",
                        "Chef de Cellule",
                        "Chef de Service", true, true);
            }
            // Sinon, c'est un Service
            else if (structureCode.matches("MINAT-.*-S[A-Z]+")) {
                template = getOrCreateTemplate("POST-CHEF-SERVICE", "Chef de Service",
                        "Chef de Service",
                        "Chef de Service", true, true);
            }
        }

        // Créer le poste si un template a été trouvé
        if (template != null) {
            createPositionFromTemplate(template, structure);
        }
    }

    /**
     * Crée le poste de Ministre pour MINAT et le Cabinet
     */
    private void createMinisterPosition(AdministrativeStructure minat) {
        PositionTemplate template = getOrCreateTemplate("POST-MINISTRE", "Ministre",
                "Ministre de l'Administration Territoriale",
                "Ministre", true, true);

        // Créer le poste au niveau MINAT
        createPositionFromTemplate(template, minat);

        // Créer également le poste dans le Cabinet du Ministre si celui-ci existe
        structureRepository.findByCode("MINAT-CABINET").ifPresent(cabinet -> {
            createPositionFromTemplate(template, cabinet);
        });
    }

    /**
     * Crée les postes multiples selon la description de la structure
     */
    private void createMultiplePositionsFromDescription(AdministrativeStructure structure) {
        String description = structure.getDescription();
        String structureCode = structure.getCode();

        if (description == null || description.isEmpty()) {
            return;
        }

        // Gérer le Cabinet du Ministre
        if (structureCode.equals("MINAT-CABINET")) {
            // Le poste de Ministre est créé au niveau MINAT, mais on peut aussi le créer ici si nécessaire
            // 4 Secrétaires
            createNumberedPositions(structure, "Secrétaire", 4, false, false);
            // 2 Chargés de la Comptabilité
            createNumberedPositions(structure, "Chargé de la Comptabilité", 2, false, false);
            // 2 Chauffeurs
            createNumberedPositions(structure, "Chauffeur", 2, false, false);
            // 5 Personnel d'appui
            createNumberedPositions(structure, "Personnel d'appui", 5, false, false);
            // 5 Personnel d'escorte
            createNumberedPositions(structure, "Personnel d'escorte", 5, false, false);
            return;
        }

        // Gérer les Conseillers Techniques (3 postes)
        if (structureCode.equals("MINAT-CT") && description.contains("3 Conseillers Techniques")) {
            createNumberedPositions(structure, "Conseiller Technique", 3, false, true);
            return;
        }

        // Gérer les Inspecteurs (3 par Inspection Générale)
        // L'Inspecteur Général est déjà créé par createPositionForStructure
        if (structureCode.matches("MINAT-IG-.*") && description.contains("3 Inspecteurs")) {
            createNumberedPositions(structure, "Inspecteur", 3, false, false);
            return;
        }

        // Gérer les Chargés d'Études Assistants
        if (description.contains("Chargés d'Études Assistants") || description.contains("Chargé d'Études Assistants")) {
            int count = extractNumberFromDescription(description);
            if (count > 0) {
                createNumberedPositions(structure, "Chargé d'Études Assistant", count, false, false);
            }
        }
    }

    /**
     * Extrait le nombre depuis une description
     * Exemples: "3 Chargés d'Études Assistants" -> 3, "2 Chargés d'Études Assistants" -> 2
     */
    private int extractNumberFromDescription(String description) {
        // Pattern pour trouver un nombre au début ou dans la description
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)\\s+(Chargés?|Inspecteurs?|Conseillers?)");
        java.util.regex.Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    /**
     * Crée plusieurs postes numérotés pour une structure
     */
    private void createNumberedPositions(AdministrativeStructure structure, String baseTitle, 
                                        int count, boolean isManagerial, boolean isNominative) {
        // Générer un code de template propre
        String templateCode = "POST-" + baseTitle.toUpperCase()
                .replace(" ", "-")
                .replace("'", "")
                .replace("É", "E")
                .replace("é", "e");
        
        PositionTemplate template = getOrCreateTemplateForMultiple(
                templateCode,
                baseTitle,
                isManagerial,
                isNominative
        );

        for (int i = 1; i <= count; i++) {
            String numberedTitle = baseTitle + " N°" + i;
            String positionCode = structure.getCode() + "-" + template.getCode() + "-" + i;

            if (positionRepository.existsByCode(positionCode)) {
                log.debug("Position {} already exists", positionCode);
                continue;
            }

            Position position = Position.builder()
                    .code(positionCode)
                    .title(numberedTitle)
                    .description(template.getDescription())
                    .structure(structure)
                    .rank(template.getRank())
                    .category(template.getCategory())
                    .requiredGrade(template.getRequiredGrade())
                    .requiredCorps(template.getRequiredCorps())
                    .status(Position.PositionStatus.VACANT)
                    .isManagerial(isManagerial)
                    .active(true)
                    .build();

            position.setCreatedBy("system");
            position.setCreatedDate(LocalDate.now());

            Position saved = positionRepository.save(position);
            log.info("Created position: {} for structure: {}", saved.getTitle(), structure.getName());
        }
    }

    /**
     * Obtient ou crée un template pour les postes multiples
     */
    private PositionTemplate getOrCreateTemplateForMultiple(String code, String title,
                                                           boolean isManagerial, boolean isNominative) {
        return templateRepository.findByCode(code)
                .orElseGet(() -> {
                    String description = title;
                    String rank = title;
                    String category = "A";
                    String requiredGrade = isNominative ? "Hors échelle" : "Grade A";
                    String requiredCorps = isManagerial ? "Corps administratif" : 
                                          title.contains("Inspecteur") ? "Corps d'inspection" : "Corps administratif";

                    PositionTemplate template = PositionTemplate.builder()
                            .code(code)
                            .title(title)
                            .description(description)
                            .rank(rank)
                            .category(category)
                            .requiredGrade(requiredGrade)
                            .requiredCorps(requiredCorps)
                            .isManagerial(isManagerial)
                            .isNominative(isNominative)
                            .isUniquePerStructure(false) // Postes multiples
                            .autoCreate(true)
                            .applicableStructureType(ApplicableStructureType.ALL_STRUCTURES)
                            .active(true)
                            .build();

                    template.setCreatedBy("system");
                    template.setCreatedDate(LocalDate.now());

                    return templateRepository.save(template);
                });
    }

    /**
     * Obtient ou crée un template de poste
     */
    private PositionTemplate getOrCreateTemplate(String code, String title, String description,
                                                String rank, boolean isManagerial, boolean isNominative) {
        return templateRepository.findByCode(code)
                .orElseGet(() -> {
                    PositionTemplate template = PositionTemplate.builder()
                            .code(code)
                            .title(title)
                            .description(description)
                            .rank(rank)
                            .category("A")
                            .requiredGrade("Hors échelle")
                            .requiredCorps("Corps administratif")
                            .isManagerial(isManagerial)
                            .isNominative(isNominative)
                            .isUniquePerStructure(true)
                            .autoCreate(true)
                            .applicableStructureType(ApplicableStructureType.DIRECTION_ONLY)
                            .active(true)
                            .build();

                    template.setCreatedBy("system");
                    template.setCreatedDate(LocalDate.now());

                    return templateRepository.save(template);
                });
    }

    /**
     * Crée un poste à partir d'un template
     */
    private Position createPositionFromTemplate(PositionTemplate template, AdministrativeStructure structure) {
        // Vérifier si le poste existe déjà
        String positionCode = structure.getCode() + "-" + template.getCode();
        if (positionRepository.existsByCode(positionCode)) {
            log.debug("Position {} already exists for structure {}", template.getTitle(), structure.getCode());
            return null;
        }

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
                .isManagerial(template.getIsManagerial())
                .active(true)
                .build();

        position.setCreatedBy("system");
        position.setCreatedDate(LocalDate.now());

        Position saved = positionRepository.save(position);
        log.info("Created position: {} for structure: {}", saved.getTitle(), structure.getName());

        return saved;
    }
}

