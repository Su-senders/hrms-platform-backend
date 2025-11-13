package com.hrms.dto;

import lombok.*;
import java.util.List;

/**
 * DTO pour la configuration d'export personnalisé
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportConfigurationDTO {
    /**
     * Colonnes à exporter (si null, toutes les colonnes)
     */
    private List<String> selectedColumns;
    
    /**
     * Filtres de recherche (optionnel)
     */
    private PersonnelSearchDTO filters;
    
    /**
     * Format d'export : EXCEL, PDF, CSV
     */
    private String format; // EXCEL, PDF, CSV
    
    /**
     * Inclure les champs calculés (âge, ancienneté, etc.)
     */
    private Boolean includeCalculatedFields = true;
    
    /**
     * Inclure les données géographiques
     */
    private Boolean includeGeographicData = true;
    
    /**
     * Inclure les données de contact
     */
    private Boolean includeContactData = true;
    
    /**
     * Inclure les données professionnelles
     */
    private Boolean includeProfessionalData = true;
    
    /**
     * Tri personnalisé (colonne, direction)
     */
    private String sortBy;
    private String sortDirection; // ASC, DESC
}

