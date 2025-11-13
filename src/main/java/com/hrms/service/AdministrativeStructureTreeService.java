package com.hrms.service;

import com.hrms.dto.StructureTreeNodeDTO;
import com.hrms.entity.AdministrativeStructure;
import com.hrms.repository.AdministrativeStructureRepository;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion de l'arbre hiérarchique des structures administratives
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdministrativeStructureTreeService {

    private final AdministrativeStructureRepository structureRepository;
    private final PersonnelRepository personnelRepository;
    private final PositionRepository positionRepository;

    /**
     * Obtient l'arbre complet des structures depuis la racine (Ministère)
     */
    public StructureTreeNodeDTO getCompleteTree() {
        log.info("Construction de l'arbre complet des structures");

        // Trouver la racine (Ministère)
        List<AdministrativeStructure> roots = structureRepository.findByParentStructureIsNullAndDeletedFalse();

        if (roots.isEmpty()) {
            log.warn("Aucune structure racine trouvée");
            return null;
        }

        // Prendre la première racine (normalement il n'y en a qu'une : MINAT)
        AdministrativeStructure root = roots.get(0);
        return buildTreeNode(root, true);
    }

    /**
     * Obtient l'arbre d'une structure spécifique et ses enfants
     */
    public StructureTreeNodeDTO getStructureTree(Long structureId) {
        log.info("Construction de l'arbre pour la structure {}", structureId);

        AdministrativeStructure structure = structureRepository.findById(structureId)
            .orElseThrow(() -> new RuntimeException("Structure non trouvée"));

        return buildTreeNode(structure, true);
    }

    /**
     * Obtient uniquement les enfants directs d'une structure
     */
    public List<StructureTreeNodeDTO> getDirectChildren(Long structureId) {
        log.info("Récupération des enfants directs de la structure {}", structureId);

        AdministrativeStructure structure = structureRepository.findById(structureId)
            .orElseThrow(() -> new RuntimeException("Structure non trouvée"));

        List<AdministrativeStructure> children = structureRepository
            .findByParentStructureIdAndDeletedFalse(structure.getId());

        return children.stream()
            .map(child -> buildTreeNode(child, false))
            .collect(Collectors.toList());
    }

    /**
     * Obtient le fil d'Ariane (breadcrumb) d'une structure
     */
    public List<StructureTreeNodeDTO> getBreadcrumb(Long structureId) {
        log.info("Génération du fil d'Ariane pour la structure {}", structureId);

        List<StructureTreeNodeDTO> breadcrumb = new ArrayList<>();
        AdministrativeStructure current = structureRepository.findById(structureId)
            .orElseThrow(() -> new RuntimeException("Structure non trouvée"));

        // Remonter la hiérarchie jusqu'à la racine
        while (current != null) {
            StructureTreeNodeDTO node = buildTreeNode(current, false);
            breadcrumb.add(0, node); // Ajouter au début pour avoir l'ordre racine -> feuille

            current = current.getParentStructure();
        }

        return breadcrumb;
    }

    /**
     * Obtient le chemin complet d'une structure
     */
    public String getFullPath(Long structureId) {
        List<StructureTreeNodeDTO> breadcrumb = getBreadcrumb(structureId);
        return breadcrumb.stream()
            .map(StructureTreeNodeDTO::getName)
            .collect(Collectors.joining(" > "));
    }

    /**
     * Construit un nœud de l'arbre avec statistiques
     */
    private StructureTreeNodeDTO buildTreeNode(AdministrativeStructure structure, boolean includeChildren) {
        // Statistiques personnels
        long personnelCount = personnelRepository.countByStructureIdAndDeletedFalse(structure.getId());
        long activePersonnelCount = personnelRepository
            .countByStructureIdAndAdministrativeStatusAndDeletedFalse(
                structure.getId(), com.hrms.entity.Personnel.AdministrativeStatus.ACTIVE);

        // Statistiques postes
        long positionCount = positionRepository.countByStructureIdAndDeletedFalse(structure.getId());
        long vacantPositionCount = positionRepository
            .countByStructureIdAndStatusAndDeletedFalse(
                structure.getId(), com.hrms.entity.Position.PositionStatus.VACANT);

        // Calculer le chemin complet directement ici pour éviter la récursion infinie
        String fullPath = buildFullPath(structure);

        StructureTreeNodeDTO node = StructureTreeNodeDTO.builder()
            .id(structure.getId())
            .code(structure.getCode())
            .name(structure.getName())
            .type(structure.getType() != null ? structure.getType().name() : null)
            .level(structure.getLevel())
            .city(structure.getCity())
            .parentId(structure.getParentStructure() != null ? structure.getParentStructure().getId() : null)
            .parentName(structure.getParentStructure() != null ? structure.getParentStructure().getName() : null)
            .personnelCount(personnelCount)
            .activePersonnelCount(activePersonnelCount)
            .positionCount(positionCount)
            .vacantPositionCount(vacantPositionCount)
            .fullPath(fullPath)
            .build();

        // Géographie liée
        if (structure.getRegion() != null) {
            node.setRegionId(structure.getRegion().getId());
            node.setRegionName(structure.getRegion().getName());
        }
        if (structure.getDepartment() != null) {
            node.setDepartmentId(structure.getDepartment().getId());
            node.setDepartmentName(structure.getDepartment().getName());
        }
        if (structure.getArrondissement() != null) {
            node.setArrondissementId(structure.getArrondissement().getId());
            node.setArrondissementName(structure.getArrondissement().getName());
        }

        // Construire les enfants récursivement si demandé
        if (includeChildren) {
            List<AdministrativeStructure> children = structureRepository
                .findByParentStructureIdAndDeletedFalse(structure.getId());

            for (AdministrativeStructure child : children) {
                StructureTreeNodeDTO childNode = buildTreeNode(child, true);
                node.addChild(childNode);
            }
        }

        return node;
    }

    /**
     * Recherche une structure par nom dans l'arbre
     */
    public List<StructureTreeNodeDTO> searchStructureByName(String name) {
        log.info("Recherche de structures par nom: {}", name);

        List<AdministrativeStructure> structures = structureRepository
            .findByNameContainingIgnoreCaseAndDeletedFalse(name);

        return structures.stream()
            .map(structure -> buildTreeNode(structure, false))
            .collect(Collectors.toList());
    }

    /**
     * Obtient toutes les structures feuilles (sans enfants)
     */
    public List<StructureTreeNodeDTO> getLeafStructures() {
        log.info("Récupération des structures feuilles");

        List<AdministrativeStructure> allStructures = structureRepository.findByDeletedFalse();

        return allStructures.stream()
            .filter(structure -> {
                List<AdministrativeStructure> children = structureRepository
                    .findByParentStructureIdAndDeletedFalse(structure.getId());
                return children.isEmpty();
            })
            .map(structure -> buildTreeNode(structure, false))
            .collect(Collectors.toList());
    }

    /**
     * Obtient les structures par niveau
     */
    public List<StructureTreeNodeDTO> getStructuresByLevel(int level) {
        log.info("Récupération des structures de niveau {}", level);

        List<AdministrativeStructure> structures = structureRepository
            .findByLevelAndDeletedFalse(level);

        return structures.stream()
            .map(structure -> buildTreeNode(structure, false))
            .collect(Collectors.toList());
    }

    /**
     * Construit le chemin complet d'une structure en remontant la hiérarchie
     * (méthode utilitaire pour éviter la récursion infinie)
     */
    private String buildFullPath(AdministrativeStructure structure) {
        List<String> pathElements = new ArrayList<>();
        AdministrativeStructure current = structure;

        // Remonter la hiérarchie jusqu'à la racine
        while (current != null) {
            pathElements.add(0, current.getName()); // Ajouter au début
            current = current.getParentStructure();
        }

        return String.join(" > ", pathElements);
    }
}
