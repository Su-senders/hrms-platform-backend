package com.hrms.controller;

import com.hrms.dto.StructureTreeNodeDTO;
import com.hrms.service.AdministrativeStructureTreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion de l'arbre hiérarchique des structures administratives
 */
@RestController
@RequestMapping("/api/structures/tree")
@RequiredArgsConstructor
@Tag(name = "Arbre des Structures", description = "API de gestion de l'arbre hiérarchique des structures")
@CrossOrigin(origins = "*")
public class StructureTreeController {

    private final AdministrativeStructureTreeService treeService;

    /**
     * Obtenir l'arbre complet des structures depuis la racine (Ministère)
     */
    @GetMapping("/complete")
    @Operation(summary = "Obtenir l'arbre complet des structures depuis la racine (MINAT)")
    public ResponseEntity<StructureTreeNodeDTO> getCompleteTree() {
        StructureTreeNodeDTO tree = treeService.getCompleteTree();
        return ResponseEntity.ok(tree);
    }

    /**
     * Obtenir l'arbre d'une structure spécifique et ses enfants
     */
    @GetMapping("/{structureId}")
    @Operation(summary = "Obtenir l'arbre d'une structure spécifique et tous ses descendants")
    public ResponseEntity<StructureTreeNodeDTO> getStructureTree(@PathVariable Long structureId) {
        StructureTreeNodeDTO tree = treeService.getStructureTree(structureId);
        return ResponseEntity.ok(tree);
    }

    /**
     * Obtenir uniquement les enfants directs d'une structure
     */
    @GetMapping("/{structureId}/children")
    @Operation(summary = "Obtenir uniquement les enfants directs d'une structure (non récursif)")
    public ResponseEntity<List<StructureTreeNodeDTO>> getDirectChildren(@PathVariable Long structureId) {
        List<StructureTreeNodeDTO> children = treeService.getDirectChildren(structureId);
        return ResponseEntity.ok(children);
    }

    /**
     * Obtenir le fil d'Ariane (breadcrumb) d'une structure
     */
    @GetMapping("/{structureId}/breadcrumb")
    @Operation(summary = "Obtenir le fil d'Ariane (chemin depuis la racine) d'une structure")
    public ResponseEntity<List<StructureTreeNodeDTO>> getBreadcrumb(@PathVariable Long structureId) {
        List<StructureTreeNodeDTO> breadcrumb = treeService.getBreadcrumb(structureId);
        return ResponseEntity.ok(breadcrumb);
    }

    /**
     * Obtenir le chemin complet d'une structure (texte)
     */
    @GetMapping("/{structureId}/fullPath")
    @Operation(summary = "Obtenir le chemin complet d'une structure sous forme de texte (ex: MINAT > Gouvernorat > Préfecture)")
    public ResponseEntity<String> getFullPath(@PathVariable Long structureId) {
        String path = treeService.getFullPath(structureId);
        return ResponseEntity.ok(path);
    }

    /**
     * Rechercher une structure par nom dans l'arbre
     */
    @GetMapping("/search")
    @Operation(summary = "Rechercher des structures par nom")
    public ResponseEntity<List<StructureTreeNodeDTO>> searchStructureByName(@RequestParam String name) {
        List<StructureTreeNodeDTO> results = treeService.searchStructureByName(name);
        return ResponseEntity.ok(results);
    }

    /**
     * Obtenir toutes les structures feuilles (sans enfants)
     */
    @GetMapping("/leaves")
    @Operation(summary = "Obtenir toutes les structures feuilles (structures sans sous-structures)")
    public ResponseEntity<List<StructureTreeNodeDTO>> getLeafStructures() {
        List<StructureTreeNodeDTO> leaves = treeService.getLeafStructures();
        return ResponseEntity.ok(leaves);
    }

    /**
     * Obtenir les structures par niveau hiérarchique
     */
    @GetMapping("/level/{level}")
    @Operation(summary = "Obtenir toutes les structures d'un niveau hiérarchique donné")
    public ResponseEntity<List<StructureTreeNodeDTO>> getStructuresByLevel(@PathVariable int level) {
        List<StructureTreeNodeDTO> structures = treeService.getStructuresByLevel(level);
        return ResponseEntity.ok(structures);
    }
}
