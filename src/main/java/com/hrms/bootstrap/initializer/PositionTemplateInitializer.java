package com.hrms.bootstrap.initializer;

import com.hrms.entity.PositionTemplate;
import com.hrms.entity.PositionTemplate.ApplicableStructureType;
import com.hrms.repository.PositionTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Initialise les modèles de postes prédéfinis
 * Ces postes peuvent être créés automatiquement ou manuellement dans les structures appropriées
 */
@Slf4j
@Component
@Profile("dev")
@Order(3) // S'exécute après les structures
@RequiredArgsConstructor
public class PositionTemplateInitializer implements CommandLineRunner {

    private final PositionTemplateRepository templateRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (templateRepository.count() > 0) {
            log.info("Position templates already initialized, skipping...");
            return;
        }

        log.info("Initializing position templates...");

        try {
            // POSTES MINISTÉRIELS (Uniquement au MINAT)
            initializeMinisterialPositions();

            // POSTES DES GOUVERNORATS
            initializeGouvernoratPositions();

            // POSTES DES PRÉFECTURES
            initializePrefecturePositions();

            // POSTES DES SOUS-PRÉFECTURES
            initializeSousPrefecturePositions();

            // POSTES DES DIRECTIONS
            initializeDirectionPositions();

            // POSTES DES SERVICES
            initializeServicePositions();

            // POSTES COMMUNS (Tous types de structures)
            initializeCommonPositions();

            log.info("Position templates initialized successfully!");

        } catch (Exception e) {
            log.error("Error initializing position templates", e);
            throw e;
        }
    }

    private void initializeMinisterialPositions() {
        // Ministre
        createTemplate("POST-MINISTRE", "Ministre de l'Administration Territoriale",
                "Autorité suprême du Ministère",
                "Ministre", "A", null, null,
                true, true, true,
                ApplicableStructureType.MINISTERE_ONLY);

        // Secrétaire Général
        createTemplate("POST-SG", "Secrétaire Général",
                "Responsable de la coordination administrative du Ministère",
                "Secrétaire Général", "A", null, null,
                true, true, true,
                ApplicableStructureType.MINISTERE_ONLY);

        // Chef de Secrétariat Particulier
        createTemplate("POST-CSP", "Chef de Secrétariat Particulier",
                "Responsable du Secrétariat Particulier du Ministre",
                "Chef de Secrétariat", "A", null, null,
                true, true, true,
                ApplicableStructureType.MINISTERE_ONLY);

        // Conseiller Technique
        createTemplate("POST-CT", "Conseiller Technique",
                "Conseiller technique auprès du Ministre (3 postes)",
                "Conseiller", "A", null, null,
                true, false, false,
                ApplicableStructureType.MINISTERE_ONLY);

        // Inspecteur Général
        createTemplate("POST-IG", "Inspecteur Général",
                "Responsable d'une Inspection Générale",
                "Inspecteur Général", "A", null, null,
                true, true, true,
                ApplicableStructureType.MINISTERE_ONLY);

        // Inspecteur
        createTemplate("POST-INSP", "Inspecteur",
                "Inspecteur rattaché à un Inspecteur Général",
                "Inspecteur", "A", null, null,
                false, false, false,
                ApplicableStructureType.MINISTERE_ONLY);

        // Directeur
        createTemplate("POST-DIR", "Directeur",
                "Responsable d'une Direction",
                "Directeur", "A", null, null,
                true, true, true,
                ApplicableStructureType.DIRECTION_ONLY);

        // Chef de Division
        createTemplate("POST-CHEDIV", "Chef de Division",
                "Responsable d'une Division",
                "Chef de Division", "A", null, null,
                true, true, true,
                ApplicableStructureType.DIRECTION_ONLY);
    }

    private void initializeGouvernoratPositions() {
        // Gouverneur
        createTemplate("POST-GOUV", "Gouverneur de Région",
                "Représentant de l'État dans la Région, Autorité territoriale suprême",
                "Gouverneur", "A", null, "Corps préfectoral",
                true, true, true,
                ApplicableStructureType.GOUVERNORAT_ONLY);

        // Secrétaire Général du Gouvernorat
        createTemplate("POST-SG-GOUV", "Secrétaire Général du Gouvernorat",
                "Second du Gouverneur, responsable de l'administration du Gouvernorat",
                "Secrétaire Général", "A", null, "Corps préfectoral",
                true, true, true,
                ApplicableStructureType.GOUVERNORAT_ONLY);

        // Chef de Cabinet du Gouverneur
        createTemplate("POST-CDC-GOUV", "Chef de Cabinet du Gouverneur",
                "Responsable du Cabinet du Gouverneur",
                "Chef de Cabinet", "A", null, null,
                true, true, true,
                ApplicableStructureType.GOUVERNORAT_ONLY);

        // Chargé de Mission
        createTemplate("POST-CM-GOUV", "Chargé de Mission",
                "Chargé de mission auprès du Gouverneur",
                "Chargé de Mission", "A", null, null,
                true, false, false,
                ApplicableStructureType.GOUVERNORAT_ONLY);

        // Chef de Service
        createTemplate("POST-CS-GOUV", "Chef de Service du Gouvernorat",
                "Responsable d'un service au sein du Gouvernorat",
                "Chef de Service", "A", null, null,
                true, false, false,
                ApplicableStructureType.GOUVERNORAT_ONLY);
    }

    private void initializePrefecturePositions() {
        // Préfet
        createTemplate("POST-PREF", "Préfet de Département",
                "Représentant de l'État dans le Département",
                "Préfet", "A", null, "Corps préfectoral",
                true, true, true,
                ApplicableStructureType.PREFECTURE_ONLY);

        // Secrétaire Général de Préfecture
        createTemplate("POST-SG-PREF", "Secrétaire Général de Préfecture",
                "Second du Préfet, responsable de l'administration de la Préfecture",
                "Secrétaire Général", "A", null, "Corps préfectoral",
                true, true, true,
                ApplicableStructureType.PREFECTURE_ONLY);

        // Chef de Cabinet du Préfet
        createTemplate("POST-CDC-PREF", "Chef de Cabinet du Préfet",
                "Responsable du Cabinet du Préfet",
                "Chef de Cabinet", "A", null, null,
                true, true, true,
                ApplicableStructureType.PREFECTURE_ONLY);

        // Chef de Service Préfectoral
        createTemplate("POST-CS-PREF", "Chef de Service Préfectoral",
                "Responsable d'un service au sein de la Préfecture",
                "Chef de Service", "A", null, null,
                true, false, false,
                ApplicableStructureType.PREFECTURE_ONLY);
    }

    private void initializeSousPrefecturePositions() {
        // Sous-Préfet
        createTemplate("POST-SPREF", "Sous-Préfet d'Arrondissement",
                "Représentant de l'État dans l'Arrondissement",
                "Sous-Préfet", "A", null, "Corps préfectoral",
                true, true, true,
                ApplicableStructureType.SOUS_PREFECTURE_ONLY);

        // Adjoint au Sous-Préfet
        createTemplate("POST-ASPREF", "Adjoint au Sous-Préfet",
                "Second du Sous-Préfet",
                "Adjoint", "A", null, "Corps préfectoral",
                true, true, false,
                ApplicableStructureType.SOUS_PREFECTURE_ONLY);

        // Chef de Poste Administratif
        createTemplate("POST-CPA", "Chef de Poste Administratif",
                "Responsable d'un poste administratif",
                "Chef de Poste", "B", null, null,
                true, false, false,
                ApplicableStructureType.SOUS_PREFECTURE_ONLY);
    }

    private void initializeDirectionPositions() {
        // Sous-Directeur
        createTemplate("POST-SDIR", "Sous-Directeur",
                "Responsable d'une Sous-Direction",
                "Sous-Directeur", "A", null, null,
                true, true, true,
                ApplicableStructureType.DIRECTION_ONLY);

        // Chef de Cellule
        createTemplate("POST-CCELL", "Chef de Cellule",
                "Responsable d'une Cellule",
                "Chef de Cellule", "A", null, null,
                true, true, false,
                ApplicableStructureType.DIRECTION_ONLY);

        // Chargé d'Études
        createTemplate("POST-CE", "Chargé d'Études",
                "Chargé d'études au sein d'une Direction",
                "Chargé d'Études", "A", null, null,
                false, false, false,
                ApplicableStructureType.DIRECTION_ONLY);

        // Chargé d'Études Assistant
        createTemplate("POST-CEA", "Chargé d'Études Assistant",
                "Assistant chargé d'études",
                "Chargé d'Études Assistant", "A", null, null,
                false, false, false,
                ApplicableStructureType.DIRECTION_ONLY);
    }

    private void initializeServicePositions() {
        // Chef de Service
        createTemplate("POST-CS", "Chef de Service",
                "Responsable d'un Service",
                "Chef de Service", "A", null, null,
                true, true, true,
                ApplicableStructureType.SERVICE_ONLY);

        // Adjoint Chef de Service
        createTemplate("POST-ACS", "Adjoint Chef de Service",
                "Second du Chef de Service",
                "Adjoint", "A", null, null,
                true, false, false,
                ApplicableStructureType.SERVICE_ONLY);
    }

    private void initializeCommonPositions() {
        // Secrétaire
        createTemplate("POST-SEC", "Secrétaire",
                "Secrétaire administratif",
                "Secrétaire", "B", null, null,
                false, false, false,
                ApplicableStructureType.ALL_STRUCTURES);

        // Agent Administratif
        createTemplate("POST-AA", "Agent Administratif",
                "Agent administratif",
                "Agent", "C", null, null,
                false, false, false,
                ApplicableStructureType.ALL_STRUCTURES);

        // Chauffeur
        createTemplate("POST-CHAUF", "Chauffeur",
                "Chauffeur de service",
                "Chauffeur", "C", null, null,
                false, false, false,
                ApplicableStructureType.ALL_STRUCTURES);

        // Planton
        createTemplate("POST-PLANT", "Planton",
                "Agent de liaison",
                "Planton", "C", null, null,
                false, false, false,
                ApplicableStructureType.ALL_STRUCTURES);

        // Gardien
        createTemplate("POST-GARD", "Gardien",
                "Gardien de sécurité",
                "Gardien", "C", null, null,
                false, false, false,
                ApplicableStructureType.ALL_STRUCTURES);

        // Agent d'Entretien
        createTemplate("POST-AE", "Agent d'Entretien",
                "Agent d'entretien et de propreté",
                "Agent d'Entretien", "C", null, null,
                false, false, false,
                ApplicableStructureType.ALL_STRUCTURES);

        // Informaticien
        createTemplate("POST-INFO", "Informaticien",
                "Informaticien de service",
                "Informaticien", "A", null, "Corps technique",
                false, false, false,
                ApplicableStructureType.ALL_STRUCTURES);

        // Comptable
        createTemplate("POST-COMPTA", "Comptable",
                "Comptable de service",
                "Comptable", "A", null, "Corps financier",
                false, false, false,
                ApplicableStructureType.ALL_STRUCTURES);
    }

    private void createTemplate(String code, String title, String description,
                                String rank, String category, String requiredGrade,
                                String requiredCorps, boolean isManagerial,
                                boolean isNominative, boolean isUnique,
                                ApplicableStructureType applicableType) {

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
                .isUniquePerStructure(isUnique)
                .applicableStructureType(applicableType)
                .autoCreate(isNominative) // Les postes nominatifs sont créés automatiquement
                .active(true)
                .build();

        template.setCreatedBy("system");
        template.setCreatedDate(LocalDate.now());

        templateRepository.save(template);
        log.debug("Created position template: {}", code);
    }
}

