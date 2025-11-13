package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Personnel Document entity
 * Manages documents attached to personnel (CNI, CV, diplomas, etc.)
 */
@Entity
@Table(name = "personnel_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel;

    @Column(name = "document_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Column(name = "document_number")
    private String documentNumber; // Numéro du document (CNI, Diplôme, etc.)

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath; // Chemin du fichier

    @Column(name = "file_size")
    private Long fileSize; // Taille en bytes

    @Column(name = "file_type")
    private String fileType; // MIME type (application/pdf, image/jpeg, etc.)

    @Column(name = "issue_date")
    private LocalDate issueDate; // Date d'émission

    @Column(name = "expiry_date")
    private LocalDate expiryDate; // Date d'expiration

    @Column(name = "issuing_authority")
    private String issuingAuthority; // Autorité émettrice

    @Column(name = "is_mandatory")
    private Boolean isMandatory = false;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verification_date")
    private LocalDate verificationDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "version")
    private Integer version = 1;

    @Column(name = "replaced_document_id")
    private Long replacedDocumentId; // ID du document remplacé (gestion des versions)

    public enum DocumentType {
        CNI,                    // Carte Nationale d'Identité
        ACTE_NAISSANCE,         // Acte de naissance
        CERTIFICAT_NATIONALITE, // Certificat de nationalité
        CV,                     // Curriculum Vitae
        DIPLOME,                // Diplôme
        ATTESTATION,            // Attestation
        CERTIFICAT,             // Certificat
        DECISION_AFFECTATION,   // Décision d'affectation
        DECISION_NOMINATION,    // Décision de nomination
        DECISION_PROMOTION,     // Décision de promotion
        ARRETE,                 // Arrêté
        ACTE_CARRIERE,          // Acte de carrière
        CONTRAT,                // Contrat de travail
        FICHE_NOTATION,         // Fiche de notation
        PHOTO,                  // Photo d'identité
        CASIER_JUDICIAIRE,      // Casier judiciaire
        CERTIFICAT_MEDICAL,     // Certificat médical
        ATTESTATION_TRAVAIL,    // Attestation de travail
        BULLETIN_SALAIRE,       // Bulletin de salaire
        AUTRE                   // Autre type de document
    }

    /**
     * Check if document is expired
     */
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    /**
     * Check if document will expire soon (within 30 days)
     */
    public boolean isExpiringSoon() {
        if (expiryDate == null) {
            return false;
        }
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        return expiryDate.isBefore(thirtyDaysFromNow) && !isExpired();
    }

    /**
     * Verify document
     */
    public void verify(String verifier) {
        this.isVerified = true;
        this.verifiedBy = verifier;
        this.verificationDate = LocalDate.now();
    }

    /**
     * Create new version of document
     */
    public PersonnelDocument createNewVersion(String newFilePath, String newFileName) {
        return PersonnelDocument.builder()
                .personnel(this.personnel)
                .documentType(this.documentType)
                .documentNumber(this.documentNumber)
                .title(this.title)
                .description(this.description)
                .filePath(newFilePath)
                .fileName(newFileName)
                .issueDate(this.issueDate)
                .expiryDate(this.expiryDate)
                .issuingAuthority(this.issuingAuthority)
                .isMandatory(this.isMandatory)
                .version(this.version + 1)
                .replacedDocumentId(this.getId())
                .build();
    }

    /**
     * Get document type label in French
     */
    @Transient
    public String getDocumentTypeLabel() {
        switch (documentType) {
            case CNI: return "Carte Nationale d'Identité";
            case ACTE_NAISSANCE: return "Acte de Naissance";
            case CERTIFICAT_NATIONALITE: return "Certificat de Nationalité";
            case CV: return "Curriculum Vitae";
            case DIPLOME: return "Diplôme";
            case ATTESTATION: return "Attestation";
            case CERTIFICAT: return "Certificat";
            case DECISION_AFFECTATION: return "Décision d'Affectation";
            case DECISION_NOMINATION: return "Décision de Nomination";
            case DECISION_PROMOTION: return "Décision de Promotion";
            case ARRETE: return "Arrêté";
            case ACTE_CARRIERE: return "Acte de Carrière";
            case CONTRAT: return "Contrat de Travail";
            case FICHE_NOTATION: return "Fiche de Notation";
            case PHOTO: return "Photo d'Identité";
            case CASIER_JUDICIAIRE: return "Casier Judiciaire";
            case CERTIFICAT_MEDICAL: return "Certificat Médical";
            case ATTESTATION_TRAVAIL: return "Attestation de Travail";
            case BULLETIN_SALAIRE: return "Bulletin de Salaire";
            default: return "Autre Document";
        }
    }
}
