package com.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour représenter un nœud dans l'arbre hiérarchique des structures
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructureTreeNodeDTO {

    private Long id;
    private String code;
    private String name;
    private String type; // MINISTERE, GOUVERNORAT, PREFECTURE, SOUS_PREFECTURE
    private Integer level; // 1, 2, 3, 4
    private String city;

    // Parent
    private Long parentId;
    private String parentName;

    // Géographie liée
    private Long regionId;
    private String regionName;
    private Long departmentId;
    private String departmentName;
    private Long arrondissementId;
    private String arrondissementName;

    // Statistiques
    private Long personnelCount; // Nombre de personnels affectés
    private Long activePersonnelCount; // Nombre de personnels actifs
    private Long positionCount; // Nombre de postes
    private Long vacantPositionCount; // Nombre de postes vacants

    // Enfants
    @Builder.Default
    private List<StructureTreeNodeDTO> children = new ArrayList<>();

    // Fil d'Ariane (chemin complet)
    private String fullPath; // Ex: "MINAT > Gouvernorat Centre > Préfecture Mfoundi"

    /**
     * Ajoute un enfant à ce nœud
     */
    public void addChild(StructureTreeNodeDTO child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    /**
     * Vérifie si le nœud a des enfants
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    /**
     * Nombre d'enfants directs
     */
    public int getChildrenCount() {
        return (children != null) ? children.size() : 0;
    }

    /**
     * Nombre total de descendants (récursif)
     */
    public int getTotalDescendantsCount() {
        if (children == null || children.isEmpty()) {
            return 0;
        }

        int count = children.size();
        for (StructureTreeNodeDTO child : children) {
            count += child.getTotalDescendantsCount();
        }
        return count;
    }

    /**
     * Taux d'occupation des postes
     */
    public Double getOccupationRate() {
        if (positionCount == null || positionCount == 0) {
            return 0.0;
        }

        long occupied = positionCount - (vacantPositionCount != null ? vacantPositionCount : 0);
        return Math.round((occupied * 100.0 / positionCount) * 100.0) / 100.0;
    }
}
