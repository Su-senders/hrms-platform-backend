package com.hrms.dto;

import lombok.*;
import java.util.List;

/**
 * DTO pour le résultat d'importation de personnels
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelImportResultDTO {
    /**
     * Nombre total de lignes traitées
     */
    private Integer totalRows;
    
    /**
     * Nombre de personnels créés avec succès
     */
    private Integer successCount;
    
    /**
     * Nombre d'erreurs
     */
    private Integer errorCount;
    
    /**
     * Liste des erreurs détaillées
     */
    private List<ImportErrorDTO> errors;
    
    /**
     * Liste des personnels créés (IDs)
     */
    private List<Long> createdPersonnelIds;
    
    /**
     * Mode de validation (true = vérification seule, false = import réel)
     */
    private Boolean validationOnly;
    
    /**
     * Message de résumé
     */
    private String summaryMessage;
    
    /**
     * DTO pour une erreur d'importation
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImportErrorDTO {
        private Integer rowNumber;
        private String field;
        private String value;
        private String errorMessage;
        private String errorType; // VALIDATION, DUPLICATE, GEOGRAPHIC, DATE, etc.
    }
}

