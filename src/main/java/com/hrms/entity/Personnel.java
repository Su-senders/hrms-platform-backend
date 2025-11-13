package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.Period;

/**
 * Personnel entity for MINAT employees
 * Version 2.0 - Avec corps/grades et origines géographiques
 *
 * Cette entité permet de créer un profil de carrière complet incluant:
 * - Informations personnelles complètes
 * - Origines géographiques (région, département, arrondissement)
 * - Grade et corps de métier (relations vers entités)
 * - Historique de carrière
 * - Documents associés
 */
@Entity
@Table(name = "personnel", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"matricule"}),
    @UniqueConstraint(columnNames = {"cni_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Personnel extends BaseEntity {

    // ==================== SECTION A: IDENTIFICATION DU PERSONNEL ====================

    /**
     * Matricule unique du personnel
     * Format suggéré: MINAT-YYYY-XXXXX
     *
     * IMPORTANT: Peut être NULL pour les personnels E.C.I (En Cours d'Intégration)
     * Un personnel sans matricule est automatiquement considéré comme E.C.I
     */
    @Column(name = "matricule", unique = true, nullable = true, length = 50)
    private String matricule;

    // --- A.1 ÉTAT CIVIL ---

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "place_of_birth", nullable = false, length = 200)
    private String placeOfBirth;

    /**
     * Nationalité du personnel
     * Par défaut: Camerounaise
     */
    @Column(name = "nationality", length = 100)
    private String nationality = "Camerounaise";

    /**
     * Mode d'acquisition de la nationalité
     * ORIGINE: Nationalité d'origine (né camerounais)
     * NATURALISATION: Nationalité acquise par naturalisation
     */
    @Column(name = "nationality_type")
    @Enumerated(EnumType.STRING)
    private NationalityType nationalityType = NationalityType.ORIGINE;

    /**
     * Date de naturalisation (si nationalité par naturalisation)
     */
    @Column(name = "naturalization_date")
    private LocalDate naturalizationDate;

    @Column(name = "marital_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    // --- A.2 UNITÉ ADMINISTRATIVE D'ORIGINE ---

    /**
     * Région d'origine du personnel (région de naissance/d'attache)
     * Distinct de la région d'affectation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_origine_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Region regionOrigine;

    /**
     * Département d'origine du personnel
     * Doit appartenir à la région d'origine
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_origine_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Department departmentOrigine;

    /**
     * Arrondissement d'origine du personnel
     * Doit appartenir au département d'origine
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrondissement_origine_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Arrondissement arrondissementOrigine;

    /**
     * Village d'origine
     */
    @Column(name = "village_origine", length = 200)
    private String villageOrigine;

    /**
     * Tribu d'origine
     */
    @Column(name = "tribu_origine", length = 200)
    private String tribuOrigine;

    // --- A.3 FILIATION ---

    /**
     * Nom complet du père
     */
    @Column(name = "father_name", length = 200)
    private String fatherName;

    /**
     * Nom complet de la mère
     */
    @Column(name = "mother_name", length = 200)
    private String motherName;

    // ==================== DOCUMENTS D'IDENTITÉ ====================

    @Column(name = "cni_number", unique = true, length = 50)
    private String cniNumber;

    @Column(name = "cni_issue_date")
    private LocalDate cniIssueDate;

    @Column(name = "cni_expiry_date")
    private LocalDate cniExpiryDate;

    // ==================== COORDONNÉES PERSONNELLES ====================

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "region_residence", length = 100)
    private String regionResidence; // Région de résidence actuelle

    // ==================== SECTION C.4: EMPLOYEUR - COORDONNÉES PROFESSIONNELLES ====================

    /**
     * Lieu d'affectation précis au sein de la structure
     * Ex: "Bureau 205, Bâtiment A", "Annexe Centrale", "Délégation Régionale"
     */
    @Column(name = "work_location", length = 200)
    private String workLocation;

    /**
     * Ville du lieu de travail
     * Peut être différente de structure.city si le personnel travaille dans une antenne
     */
    @Column(name = "work_city", length = 100)
    private String workCity;

    /**
     * Téléphone de bureau
     * Ligne directe au bureau (distinct du mobile personnel)
     */
    @Column(name = "office_phone", length = 20)
    private String officePhone;

    /**
     * Numéro de fax du bureau
     */
    @Column(name = "office_fax", length = 20)
    private String officeFax;

    /**
     * Email professionnel
     * Email officiel fourni par l'administration (distinct de l'email personnel)
     */
    @Column(name = "professional_email", length = 200)
    private String professionalEmail;

    // ==================== SECTION B: QUALIFICATIONS ====================

    // --- B.1 DIPLÔME DE RECRUTEMENT (Titre scolaire/universitaire au moment du recrutement) ---

    /**
     * Intitulé du diplôme de recrutement
     * Ex: "Licence en Droit", "BEPC", "Baccalauréat série C"
     */
    @Column(name = "recruitment_diploma_title", length = 300)
    private String recruitmentDiplomaTitle;

    /**
     * Type de diplôme de recrutement
     * Ex: BEPC, PROBATOIRE, BACCALAUREAT, LICENCE, MASTER, DOCTORAT, etc.
     */
    @Column(name = "recruitment_diploma_type", length = 100)
    @Enumerated(EnumType.STRING)
    private DiplomaType recruitmentDiplomaType;

    /**
     * Date d'obtention du diplôme de recrutement
     */
    @Column(name = "recruitment_diploma_date")
    private LocalDate recruitmentDiplomaDate;

    /**
     * Lieu d'obtention du diplôme de recrutement
     * Ex: "Université de Yaoundé I", "Lycée Leclerc", etc.
     */
    @Column(name = "recruitment_diploma_place", length = 300)
    private String recruitmentDiplomaPlace;

    /**
     * Niveau d'instruction du diplôme de recrutement
     * Ex: PRIMAIRE, SECONDAIRE_1ER_CYCLE, SECONDAIRE_2ND_CYCLE, SUPERIEUR_1ER_CYCLE, etc.
     */
    @Column(name = "recruitment_education_level")
    @Enumerated(EnumType.STRING)
    private EducationLevel recruitmentEducationLevel;

    /**
     * Spécialité du diplôme de recrutement
     * Ex: "Droit Public", "Mathématiques", "Informatique", "Administration"
     */
    @Column(name = "recruitment_diploma_specialty", length = 200)
    private String recruitmentDiplomaSpecialty;

    /**
     * Domaine d'étude du diplôme de recrutement
     * Ex: DROIT, SCIENCES, LETTRES, ECONOMIE, GESTION, INFORMATIQUE, etc.
     */
    @Column(name = "recruitment_study_field")
    @Enumerated(EnumType.STRING)
    private StudyField recruitmentStudyField;

    // --- B.2 DIPLÔME LE PLUS ÉLEVÉ (Titre le plus élevé obtenu, peut être différent du diplôme de recrutement) ---

    /**
     * Intitulé du diplôme le plus élevé
     * Ex: "Master en Administration Publique", "Doctorat en Sciences Politiques"
     */
    @Column(name = "highest_diploma_title", length = 300)
    private String highestDiplomaTitle;

    /**
     * Type de diplôme le plus élevé
     */
    @Column(name = "highest_diploma_type", length = 100)
    @Enumerated(EnumType.STRING)
    private DiplomaType highestDiplomaType;

    /**
     * Date d'obtention du diplôme le plus élevé
     */
    @Column(name = "highest_diploma_date")
    private LocalDate highestDiplomaDate;

    /**
     * Lieu d'obtention du diplôme le plus élevé
     */
    @Column(name = "highest_diploma_place", length = 300)
    private String highestDiplomaPlace;

    /**
     * Niveau d'instruction du diplôme le plus élevé
     */
    @Column(name = "highest_education_level")
    @Enumerated(EnumType.STRING)
    private EducationLevel highestEducationLevel;

    /**
     * Spécialité du diplôme le plus élevé
     */
    @Column(name = "highest_diploma_specialty", length = 200)
    private String highestDiplomaSpecialty;

    /**
     * Domaine d'étude du diplôme le plus élevé
     */
    @Column(name = "highest_study_field")
    @Enumerated(EnumType.STRING)
    private StudyField highestStudyField;

    // ==================== SECTION C: CARRIÈRE - SITUATION AU RECRUTEMENT ====================

    /**
     * Numéro d'acte de recrutement
     * Ex: "N° 2020/001/MINAT", "Décret N° 2020/456"
     */
    @Column(name = "recruitment_act_number", length = 100)
    private String recruitmentActNumber;

    /**
     * Nature de l'acte de recrutement
     * Ex: DECRET, ARRETE, DECISION, CONTRAT_TRAVAIL
     */
    @Column(name = "recruitment_act_nature")
    @Enumerated(EnumType.STRING)
    private RecruitmentActNature recruitmentActNature;

    /**
     * Date de signature de l'acte de recrutement
     */
    @Column(name = "recruitment_act_signature_date")
    private LocalDate recruitmentActSignatureDate;

    /**
     * Signataire de l'acte de recrutement
     * Ex: "Ministre de l'Administration Territoriale", "Délégué Régional"
     */
    @Column(name = "recruitment_act_signatory", length = 200)
    private String recruitmentActSignatory;

    /**
     * Date de prise d'effet de l'acte de recrutement
     */
    @Column(name = "recruitment_act_effective_date")
    private LocalDate recruitmentActEffectiveDate;

    /**
     * Mode de recrutement
     * Ex: SUR_CONCOURS, SUR_TITRE, SPECIAL
     */
    @Column(name = "recruitment_mode")
    @Enumerated(EnumType.STRING)
    private RecruitmentMode recruitmentMode;

    /**
     * Profession au moment du recrutement
     * Ex: "Administrateur Civil", "Secrétaire d'Administration"
     */
    @Column(name = "recruitment_profession", length = 200)
    private String recruitmentProfession;

    /**
     * Catégorie au moment du recrutement
     * Ex: CATEGORIE_A, CATEGORIE_B, CATEGORIE_C, CATEGORIE_D
     */
    @Column(name = "recruitment_category")
    @Enumerated(EnumType.STRING)
    private PersonnelCategory recruitmentCategory;

    /**
     * Administration d'origine (avant le recrutement au MINAT)
     * Ex: "Ministère des Finances", "Secteur Privé", "Primo-recrutement"
     */
    @Column(name = "origin_administration", length = 300)
    private String originAdministration;

    /**
     * Ancienneté dans l'administration publique (en années)
     * Calculée automatiquement à partir de la date d'embauche (hire_date)
     * Peut être NULL pour les personnels E.C.I sans hire_date
     */
    @Column(name = "years_in_public_service")
    private Integer yearsInPublicService;

    // ==================== SECTION C.3: CARRIÈRE - SITUATION ACTUELLE ====================

    // --- C.3.1 TEXTE ACCORDANT LA SITUATION ACTUELLE ---

    /**
     * Numéro du texte accordant la situation actuelle
     * Ex: "N° 2022/045/MINAT", "Contrat N° 2022/CT-123"
     */
    @Column(name = "current_act_number", length = 100)
    private String currentActNumber;

    /**
     * Nature du texte accordant la situation actuelle
     * Ex: ACTE_INTEGRATION, AVANCE_GRADE, CONTRAT_TRAVAIL, etc.
     */
    @Column(name = "current_act_nature")
    @Enumerated(EnumType.STRING)
    private CurrentActNature currentActNature;

    /**
     * Date de signature du texte accordant
     */
    @Column(name = "current_act_signature_date")
    private LocalDate currentActSignatureDate;

    /**
     * Signataire du texte accordant
     * Ex: "Ministre de l'Administration Territoriale"
     */
    @Column(name = "current_act_signatory", length = 200)
    private String currentActSignatory;

    /**
     * Date de prise d'effet du texte accordant
     */
    @Column(name = "current_act_effective_date")
    private LocalDate currentActEffectiveDate;

    // --- C.3.2 STATUT ET CLASSIFICATION ---

    /**
     * Type de personnel (statut administratif)
     * Ex: FONCTIONNAIRE, CONTRACTUEL, DECISIONNAIRE
     */
    @Column(name = "personnel_type")
    @Enumerated(EnumType.STRING)
    private PersonnelType personnelType;

    /**
     * Classe du personnel
     * Ex: "Classe Exceptionnelle", "Classe Normale", "Hors Classe"
     */
    @Column(name = "classe", length = 100)
    private String classe;

    // --- C.3.3 ACTE DE NOMINATION AU POSTE ACTUEL ---

    /**
     * Numéro de l'acte de nomination au poste actuel
     * Ex: "Décret N° 2023/123", "Arrêté N° 2023/456/MINAT"
     */
    @Column(name = "appointment_act_number", length = 100)
    private String appointmentActNumber;

    /**
     * Nature de l'acte de nomination
     * Ex: DECRET, ARRETE, DECISION
     */
    @Column(name = "appointment_act_nature")
    @Enumerated(EnumType.STRING)
    private AppointmentActNature appointmentActNature;

    /**
     * Date de nomination au poste actuel
     */
    @Column(name = "appointment_act_date")
    private LocalDate appointmentActDate;

    // --- C.3.4 AUTRES FONCTIONS ACTUELLES ---

    /**
     * Autre fonction actuelle 1
     * Ex: "PCA", "Président de Commission", "Membre du Conseil"
     */
    @Column(name = "current_other_function_1", length = 200)
    private String currentOtherFunction1;

    /**
     * Autre fonction actuelle 2
     */
    @Column(name = "current_other_function_2", length = 200)
    private String currentOtherFunction2;

    /**
     * Autre fonction actuelle 3
     */
    @Column(name = "current_other_function_3", length = 200)
    private String currentOtherFunction3;

    // ==================== INFORMATIONS PROFESSIONNELLES ====================

    /**
     * Date d'embauche dans l'administration publique
     */
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    /**
     * Date de prise de service au poste actuel
     */
    @Column(name = "service_start_date")
    private LocalDate serviceStartDate;

    /**
     * Grade actuel du personnel (relation vers l'entité Grade)
     * Remplace l'ancien champ String "grade"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_grade_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Grade currentGrade;

    /**
     * Échelon dans le grade actuel
     * Ex: Échelon 1, 2, 3, etc.
     */
    @Column(name = "echelon")
    private Integer echelon;

    /**
     * Indice de rémunération
     */
    @Column(name = "indice")
    private Integer indice;

    // ==================== AFFECTATION ACTUELLE ====================

    /**
     * Poste actuellement occupé par le personnel
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_position_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Position currentPosition;

    /**
     * Structure administrative d'affectation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AdministrativeStructure structure;

    // ==================== STATUTS ====================

    /**
     * Statut global du personnel
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PersonnelStatus status = PersonnelStatus.ACTIVE;

    /**
     * Situation détaillée du personnel
     */
    @Column(name = "situation", nullable = false)
    @Enumerated(EnumType.STRING)
    private PersonnelSituation situation = PersonnelSituation.EN_FONCTION;

    /**
     * Indique si le personnel est En Cours d'Intégration (E.C.I)
     * Ce flag est automatiquement géré:
     * - TRUE si matricule == null
     * - FALSE si matricule != null
     *
     * Un personnel E.C.I peut être affecté à un poste même sans matricule
     */
    @Column(name = "is_eci", nullable = false)
    private Boolean isECI = false;

    // ==================== RETRAITE ====================

    /**
     * Date de départ à la retraite (calculée automatiquement)
     */
    @Column(name = "retirement_date")
    private LocalDate retirementDate;

    /**
     * Âge de départ à la retraite (par défaut 60 ans)
     */
    @Column(name = "retirement_age")
    private Integer retirementAge = 60;

    @Column(name = "is_retirable_this_year")
    private Boolean isRetirableThisYear = false;

    @Column(name = "is_retirable_next_year")
    private Boolean isRetirableNextYear = false;

    // ==================== INFORMATIONS BANCAIRES ====================

    @Column(name = "bank_name", length = 200)
    private String bankName;

    @Column(name = "bank_account_number", length = 100)
    private String bankAccountNumber;

    @Column(name = "bank_branch", length = 200)
    private String bankBranch;

    // ==================== AUTRES ====================

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    /**
     * Cumul officiel autorisé (permet d'occuper plusieurs postes)
     */
    @Column(name = "official_cumul")
    private Boolean officialCumul = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ==================== SECTION B: RELATIONS (Stages, Congés, Postes Antérieurs) ====================

    /**
     * Stages professionnels du personnel
     */
    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private java.util.List<ProfessionalTraining> professionalTrainings;

    /**
     * Mises en congés du personnel
     */
    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private java.util.List<PersonnelLeave> personnelLeaves;

    /**
     * Postes de travail occupés antérieurement
     */
    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private java.util.List<PreviousPosition> previousPositions;

    // ==================== ENUMS ====================

    public enum Gender {
        HOMME,
        FEMME
    }

    public enum NationalityType {
        ORIGINE,        // Nationalité d'origine (né camerounais)
        NATURALISATION  // Nationalité acquise par naturalisation
    }

    public enum MaritalStatus {
        CELIBATAIRE,
        MARIE,
        DIVORCE,
        VEUF
    }

    /**
     * Types de diplômes (système éducatif camerounais)
     */
    public enum DiplomaType {
        // Primaire
        CEP,                    // Certificat d'Études Primaires

        // Secondaire 1er cycle
        BEPC,                   // Brevet d'Études du Premier Cycle
        CAP,                    // Certificat d'Aptitude Professionnelle

        // Secondaire 2nd cycle
        PROBATOIRE,             // Probatoire (Bac -1)
        BACCALAUREAT,           // Baccalauréat
        BT,                     // Brevet de Technicien
        BTS,                    // Brevet de Technicien Supérieur

        // Supérieur 1er cycle
        DEUG,                   // Diplôme d'Études Universitaires Générales
        DUT,                    // Diplôme Universitaire de Technologie
        LICENCE,                // Licence (Bac+3)
        LICENCE_PROFESSIONNELLE,// Licence Professionnelle

        // Supérieur 2ème cycle
        MAITRISE,               // Maîtrise (Bac+4)
        MASTER,                 // Master (Bac+5)
        DESS,                   // Diplôme d'Études Supérieures Spécialisées
        DEA,                    // Diplôme d'Études Approfondies

        // Supérieur 3ème cycle
        DOCTORAT,               // Doctorat (Bac+8)
        PHD,                    // PhD

        // Professionnel
        DIPLOME_INGENIEUR,      // Diplôme d'Ingénieur
        DIPLOME_GRANDE_ECOLE,   // Diplôme de Grande École

        // Autre
        AUTRE                   // Autre diplôme
    }

    /**
     * Niveaux d'instruction
     */
    public enum EducationLevel {
        AUCUN,                          // Aucune instruction formelle
        PRIMAIRE,                       // Niveau primaire (CEP)
        SECONDAIRE_1ER_CYCLE,           // Collège (BEPC, CAP)
        SECONDAIRE_2ND_CYCLE,           // Lycée (Probatoire, Baccalauréat, BT)
        SUPERIEUR_1ER_CYCLE,            // Bac+1 à Bac+3 (DEUG, DUT, Licence)
        SUPERIEUR_2EME_CYCLE,           // Bac+4 à Bac+5 (Maîtrise, Master, DESS, DEA)
        SUPERIEUR_3EME_CYCLE,           // Bac+8 et plus (Doctorat, PhD)
        PROFESSIONNEL_SUPERIEUR         // Formation professionnelle supérieure
    }

    /**
     * Domaines d'étude
     */
    public enum StudyField {
        // Sciences Juridiques et Politiques
        DROIT,                          // Droit (public, privé, etc.)
        SCIENCES_POLITIQUES,            // Sciences politiques
        RELATIONS_INTERNATIONALES,      // Relations internationales

        // Sciences Économiques et Gestion
        ECONOMIE,                       // Sciences économiques
        GESTION,                        // Gestion, Management
        COMMERCE,                       // Commerce
        COMPTABILITE,                   // Comptabilité
        FINANCE,                        // Finance

        // Sciences et Technologies
        MATHEMATIQUES,                  // Mathématiques
        PHYSIQUE,                       // Physique
        CHIMIE,                         // Chimie
        BIOLOGIE,                       // Biologie
        INFORMATIQUE,                   // Informatique
        TELECOMMUNICATIONS,             // Télécommunications
        GENIE_CIVIL,                    // Génie civil
        GENIE_ELECTRIQUE,               // Génie électrique
        GENIE_MECANIQUE,                // Génie mécanique

        // Lettres et Sciences Humaines
        LETTRES,                        // Lettres modernes, classiques
        PHILOSOPHIE,                    // Philosophie
        HISTOIRE,                       // Histoire
        GEOGRAPHIE,                     // Géographie
        SOCIOLOGIE,                     // Sociologie
        PSYCHOLOGIE,                    // Psychologie
        ANTHROPOLOGIE,                  // Anthropologie

        // Langues
        LANGUES_ETRANGERES,             // Langues étrangères
        TRADUCTION,                     // Traduction

        // Éducation
        SCIENCES_EDUCATION,             // Sciences de l'éducation
        ENSEIGNEMENT,                   // Enseignement

        // Santé
        MEDECINE,                       // Médecine
        PHARMACIE,                      // Pharmacie
        SCIENCES_INFIRMIERES,           // Sciences infirmières

        // Agriculture
        AGRICULTURE,                    // Agriculture
        AGRONOMIE,                      // Agronomie
        ELEVAGE,                        // Élevage

        // Administration
        ADMINISTRATION_PUBLIQUE,        // Administration publique
        ADMINISTRATION_GENERALE,        // Administration générale

        // Communication
        JOURNALISME,                    // Journalisme
        COMMUNICATION,                  // Communication

        // Arts
        ARTS,                           // Arts plastiques, beaux-arts
        MUSIQUE,                        // Musique
        ARTS_SPECTACLE,                 // Arts du spectacle

        // Autre
        AUTRE                           // Autre domaine
    }

    public enum PersonnelStatus {
        ACTIVE,      // Personnel actif
        INACTIVE,    // Personnel inactif
        SUSPENDED,   // Personnel suspendu
        RETIRED,     // Personnel retraité
        DECEASED     // Personnel décédé
    }

    public enum PersonnelSituation {
        EN_FONCTION,             // En service actif
        EN_STAGE,                // En période de stage
        EN_FORMATION,            // En formation
        EN_DETACHEMENT,          // Détaché dans une autre structure
        EN_MISE_A_DISPOSITION,   // Mis à disposition
        EN_ATTENTE_AFFECTATION,  // En attente d'affectation
        EN_COURS_INTEGRATION,    // En cours d'intégration (E.C.I) - sans matricule
        RETRAITE,                // À la retraite
        DECEDE,                  // Décédé
        SUSPENDU,                // Suspendu
        DISPONIBILITE            // En disponibilité
    }

    /**
     * Nature de l'acte de recrutement (Section C)
     */
    public enum RecruitmentActNature {
        DECRET,                  // Décret présidentiel ou ministériel
        ARRETE,                  // Arrêté ministériel ou préfectoral
        DECISION,                // Décision administrative
        CONTRAT_TRAVAIL,         // Contrat de travail
        AUTRE                    // Autre nature d'acte
    }

    /**
     * Mode de recrutement (Section C)
     */
    public enum RecruitmentMode {
        SUR_CONCOURS,            // Recrutement sur concours (direct ou professionnel)
        SUR_TITRE,               // Recrutement sur titre (diplômes)
        SPECIAL,                 // Recrutement spécial (cas particuliers)
        CONTRACTUEL,             // Recrutement contractuel
        INTEGRATION,             // Intégration directe
        MUTATION,                // Mutation depuis une autre administration
        AUTRE                    // Autre mode
    }

    /**
     * Catégorie du personnel (Section C)
     * Classification administrative camerounaise
     */
    public enum PersonnelCategory {
        CATEGORIE_A,             // Catégorie A (Cadres supérieurs - Bac+3 minimum)
        CATEGORIE_B,             // Catégorie B (Cadres moyens - Bac à Bac+2)
        CATEGORIE_C,             // Catégorie C (Agents de maîtrise - BEPC à Probatoire)
        CATEGORIE_D,             // Catégorie D (Agents d'exécution - CEP à BEPC)
        HORS_CATEGORIE           // Hors catégorie (postes spéciaux)
    }

    /**
     * Nature du texte accordant la situation actuelle (Section C.3)
     */
    public enum CurrentActNature {
        ACTE_INTEGRATION,        // Acte d'intégration dans la fonction publique
        AVANCE_GRADE,            // Avancement de grade
        CONTRAT_TRAVAIL,         // Contrat de travail
        ACTE_RECLASSEMENT,       // Acte de reclassement
        AVENANT_CONTRAT,         // Avenant au contrat de travail
        DECISION,                // Décision administrative
        AUTRE                    // Autre nature de texte
    }

    /**
     * Type de personnel (Section C.3)
     * Statut administratif du personnel
     */
    public enum PersonnelType {
        FONCTIONNAIRE,           // Fonctionnaire titulaire
        CONTRACTUEL,             // Agent contractuel
        DECISIONNAIRE,           // Agent décisionnaire
        STAGIAIRE,               // Fonctionnaire stagiaire
        AUTRE                    // Autre type
    }

    /**
     * Nature de l'acte de nomination (Section C.3)
     */
    public enum AppointmentActNature {
        DECRET,                  // Décret présidentiel ou ministériel
        ARRETE,                  // Arrêté ministériel ou préfectoral
        DECISION,                // Décision administrative
        AUTRE                    // Autre nature d'acte
    }

    // ==================== MÉTHODES CALCULÉES (@Transient) ====================

    /**
     * Calcule l'âge actuel à partir de la date de naissance
     */
    @Transient
    public Integer getAge() {
        if (dateOfBirth != null) {
            return Period.between(dateOfBirth, LocalDate.now()).getYears();
        }
        return null;
    }

    /**
     * Calcule l'ancienneté au poste actuel
     */
    @Transient
    public Period getSeniorityInPost() {
        if (serviceStartDate != null) {
            return Period.between(serviceStartDate, LocalDate.now());
        }
        return null;
    }

    /**
     * Calcule l'ancienneté dans l'administration
     */
    @Transient
    public Period getSeniorityInAdministration() {
        if (hireDate != null) {
            return Period.between(hireDate, LocalDate.now());
        }
        return null;
    }

    /**
     * Retourne le nom complet
     */
    @Transient
    public String getFullName() {
        StringBuilder name = new StringBuilder();
        name.append(lastName);
        if (middleName != null && !middleName.isEmpty()) {
            name.append(" ").append(middleName);
        }
        name.append(" ").append(firstName);
        return name.toString();
    }

    /**
     * Retourne le corps de métier via le grade actuel
     */
    @Transient
    public CorpsMetier getCorpsMetier() {
        return currentGrade != null ? currentGrade.getCorpsMetier() : null;
    }

    /**
     * Retourne la catégorie via le grade actuel
     */
    @Transient
    public String getCategory() {
        return currentGrade != null ? currentGrade.getCategory() : null;
    }

    /**
     * Retourne le nom du grade actuel
     */
    @Transient
    public String getGradeName() {
        return currentGrade != null ? currentGrade.getName() : null;
    }

    /**
     * Retourne le code du grade actuel
     */
    @Transient
    public String getGradeCode() {
        return currentGrade != null ? currentGrade.getCode() : null;
    }

    // ==================== LIFECYCLE CALLBACKS ====================

    /**
     * Calcule automatiquement la date de retraite et les flags d'éligibilité
     * Gère automatiquement le statut E.C.I (En Cours d'Intégration)
     */
    @PrePersist
    @PreUpdate
    private void calculateRetirementDateAndECIStatus() {
        // Calcul de la date de retraite
        if (dateOfBirth != null && retirementDate == null) {
            this.retirementDate = dateOfBirth.plusYears(retirementAge);
        }

        // Vérifier si le personnel est retraitable cette année ou l'année prochaine
        if (retirementDate != null) {
            int currentYear = LocalDate.now().getYear();
            int retirementYear = retirementDate.getYear();

            this.isRetirableThisYear = (retirementYear == currentYear);
            this.isRetirableNextYear = (retirementYear == currentYear + 1);
        }

        // === CALCUL AUTOMATIQUE DE L'ANCIENNETÉ ===
        calculateSeniority();

        // === GESTION AUTOMATIQUE DU STATUT E.C.I ===
        updateECIStatus();
    }

    /**
     * Calcule automatiquement l'ancienneté dans l'administration publique
     */
    private void calculateSeniority() {
        // Ancienneté dans l'administration publique (depuis hire_date)
        if (hireDate != null) {
            Period period = Period.between(hireDate, LocalDate.now());
            this.yearsInPublicService = period.getYears();
        } else {
            this.yearsInPublicService = null;
        }
    }

    /**
     * Met à jour automatiquement le statut E.C.I en fonction du matricule
     *
     * Règles:
     * 1. Si matricule == null → isECI = true, situation = EN_COURS_INTEGRATION
     * 2. Si matricule != null et isECI était true → isECI = false, situation = EN_FONCTION
     * 3. Si matricule != null et isECI était déjà false → pas de changement
     */
    private void updateECIStatus() {
        if (matricule == null || matricule.trim().isEmpty()) {
            // Cas 1: Pas de matricule → E.C.I
            this.isECI = true;
            this.situation = PersonnelSituation.EN_COURS_INTEGRATION;
        } else {
            // Cas 2: Matricule renseigné
            if (Boolean.TRUE.equals(this.isECI)) {
                // Le personnel était E.C.I et vient d'obtenir son matricule
                this.isECI = false;
                // Passer automatiquement en fonction (sauf si déjà retraité, décédé, etc.)
                if (this.situation == PersonnelSituation.EN_COURS_INTEGRATION) {
                    this.situation = PersonnelSituation.EN_FONCTION;
                }
            }
            // Cas 3: Matricule présent et n'était pas E.C.I → rien à faire
        }
    }

    // ==================== MÉTHODES MÉTIER ====================

    /**
     * Vérifie si le personnel peut être affecté à un poste
     * IMPORTANT: Un personnel E.C.I (sans matricule) PEUT être affecté
     * @deprecated Utiliser canBeAssignedToPosition(boolean) pour une meilleure gestion du cumul
     */
    @Deprecated
    public boolean canBeAssignedToPosition() {
        return canBeAssignedToPosition(false);
    }

    /**
     * Vérifie si le personnel peut être affecté à un poste
     * IMPORTANT: Un personnel E.C.I (sans matricule) PEUT être affecté
     *
     * @param movementAllowsCumul true si le mouvement a une autorisation spéciale de cumul
     * @return true si le personnel peut être affecté
     */
    public boolean canBeAssignedToPosition(boolean movementAllowsCumul) {
        // Vérifier le statut et la situation
        boolean isEligible = status == PersonnelStatus.ACTIVE &&
               (situation == PersonnelSituation.EN_FONCTION ||
                situation == PersonnelSituation.EN_ATTENTE_AFFECTATION ||
                situation == PersonnelSituation.EN_COURS_INTEGRATION); // E.C.I peut être affecté

        if (!isEligible) {
            return false;
        }

        // Pas de poste actuel → OK
        if (currentPosition == null) {
            return true;
        }

        // A déjà un poste → Vérifier le cumul
        // Cumul autorisé SI:
        // - Le personnel a l'autorisation générale de cumul (personnel.officialCumul) OU
        // - Le mouvement spécifique a l'autorisation de cumul (movementAllowsCumul)
        return Boolean.TRUE.equals(officialCumul) || movementAllowsCumul;
    }

    /**
     * Vérifie si le personnel a déjà un poste
     */
    public boolean hasCurrentPosition() {
        return currentPosition != null;
    }

    /**
     * Obtient un message d'erreur détaillé expliquant pourquoi le personnel ne peut pas être affecté
     *
     * @param movementAllowsCumul true si le mouvement a une autorisation spéciale de cumul
     * @return message d'erreur ou null si éligible
     */
    public String getIneligibilityReason(boolean movementAllowsCumul) {
        if (status != PersonnelStatus.ACTIVE) {
            return String.format("Le personnel n'est pas actif (statut: %s)", status);
        }

        if (situation != PersonnelSituation.EN_FONCTION &&
            situation != PersonnelSituation.EN_ATTENTE_AFFECTATION &&
            situation != PersonnelSituation.EN_COURS_INTEGRATION) {
            return String.format("La situation du personnel n'est pas compatible avec une affectation (situation: %s)",
                                 situation);
        }

        if (currentPosition != null && !Boolean.TRUE.equals(officialCumul) && !movementAllowsCumul) {
            return String.format("Le personnel est déjà affecté au poste '%s'. " +
                                 "Un cumul de poste nécessite une autorisation spéciale (isOfficialCumul = true).",
                                 currentPosition.getCode());
        }

        return null; // Éligible
    }

    /**
     * Vérifie si le personnel a l'autorisation de cumuler des postes
     */
    public boolean canCumulatePositions() {
        return Boolean.TRUE.equals(officialCumul);
    }

    /**
     * Vérifie si le personnel est éligible à la retraite
     */
    public boolean isEligibleForRetirement() {
        return getAge() != null && getAge() >= retirementAge;
    }

    /**
     * Vérifie si le personnel est affecté dans sa région d'origine
     */
    public boolean isAffectedInOriginRegion() {
        if (regionOrigine == null || structure == null) {
            return false;
        }
        return structure.getRegion() != null &&
               structure.getRegion().equals(regionOrigine);
    }

    /**
     * Vérifie que les origines géographiques sont cohérentes
     */
    public boolean hasConsistentOrigins() {
        // Vérifier que le département appartient à la région
        if (departmentOrigine != null && regionOrigine != null) {
            if (!regionOrigine.equals(departmentOrigine.getRegion())) {
                return false;
            }
        }

        // Vérifier que l'arrondissement appartient au département
        if (arrondissementOrigine != null && departmentOrigine != null) {
            if (!departmentOrigine.equals(arrondissementOrigine.getDepartment())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Libère le poste actuel
     */
    public void releaseCurrentPosition() {
        if (currentPosition != null) {
            currentPosition.releasePersonnel();
            this.currentPosition = null;
            this.serviceStartDate = null;
        }
    }

    /**
     * Affecte à un nouveau poste
     */
    public void assignToPosition(Position position) {
        if (position != null) {
            position.assignPersonnel(this);
            this.currentPosition = position;
            this.serviceStartDate = LocalDate.now();
        }
    }

    /**
     * Promouvoir à un nouveau grade
     */
    public void promoteToGrade(Grade newGrade) {
        if (newGrade != null) {
            this.currentGrade = newGrade;
            // Réinitialiser l'échelon à 1 lors d'une promotion
            this.echelon = 1;
        }
    }

    /**
     * Avancer à l'échelon suivant
     */
    public void advanceToNextEchelon() {
        if (echelon != null) {
            this.echelon = echelon + 1;
        } else {
            this.echelon = 1;
        }
    }

    // ==================== MÉTHODES E.C.I ====================

    /**
     * Vérifie si le personnel est En Cours d'Intégration
     */
    @Transient
    public boolean isEnCoursIntegration() {
        return Boolean.TRUE.equals(isECI) || matricule == null || matricule.trim().isEmpty();
    }

    /**
     * Assigne un matricule au personnel et sort automatiquement du statut E.C.I
     *
     * @param newMatricule Le nouveau matricule à assigner
     */
    public void assignMatricule(String newMatricule) {
        if (newMatricule != null && !newMatricule.trim().isEmpty()) {
            this.matricule = newMatricule.trim();
            // Le @PreUpdate va automatiquement mettre à jour isECI et situation
        }
    }

    /**
     * Retourne le matricule ou "E.C.I" si pas de matricule
     */
    @Transient
    public String getMatriculeOrECI() {
        return (matricule != null && !matricule.trim().isEmpty()) ? matricule : "E.C.I";
    }

    /**
     * Retourne une description du statut E.C.I
     */
    @Transient
    public String getECIStatusDescription() {
        if (Boolean.TRUE.equals(isECI)) {
            return "En Cours d'Intégration - Matricule non attribué";
        }
        return "Personnel intégré avec matricule";
    }
}

