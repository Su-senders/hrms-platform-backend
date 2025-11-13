package com.hrms.controller;

import com.hrms.dto.AdministrativeStructureCreateDTO;
import com.hrms.dto.AdministrativeStructureDTO;
import com.hrms.dto.AdministrativeStructureUpdateDTO;
import com.hrms.service.AdministrativeStructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/structures")
@RequiredArgsConstructor
@Tag(name = "Structures", description = "API de gestion des structures administratives")
@CrossOrigin(origins = "*")
public class AdministrativeStructureController {

    private final AdministrativeStructureService structureService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle structure")
    public ResponseEntity<AdministrativeStructureDTO> createStructure(@Valid @RequestBody AdministrativeStructureCreateDTO dto) {
        AdministrativeStructureDTO created = structureService.createStructure(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une structure")
    public ResponseEntity<AdministrativeStructureDTO> updateStructure(
            @PathVariable Long id,
            @Valid @RequestBody AdministrativeStructureUpdateDTO dto) {
        AdministrativeStructureDTO updated = structureService.updateStructure(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une structure par ID")
    public ResponseEntity<AdministrativeStructureDTO> getStructureById(@PathVariable Long id) {
        AdministrativeStructureDTO structure = structureService.getStructureById(id);
        return ResponseEntity.ok(structure);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Obtenir une structure par code")
    public ResponseEntity<AdministrativeStructureDTO> getStructureByCode(@PathVariable String code) {
        AdministrativeStructureDTO structure = structureService.getStructureByCode(code);
        return ResponseEntity.ok(structure);
    }

    @GetMapping
    @Operation(summary = "Obtenir toutes les structures (paginé)")
    public ResponseEntity<Page<AdministrativeStructureDTO>> getAllStructures(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AdministrativeStructureDTO> structures = structureService.getAllStructures(pageable);
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtenir toutes les structures actives")
    public ResponseEntity<List<AdministrativeStructureDTO>> getAllActiveStructures() {
        List<AdministrativeStructureDTO> structures = structureService.getAllActiveStructures();
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Obtenir les structures par type")
    public ResponseEntity<List<AdministrativeStructureDTO>> getStructuresByType(@PathVariable String type) {
        List<AdministrativeStructureDTO> structures = structureService.getStructuresByType(type);
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/level/{level}")
    @Operation(summary = "Obtenir les structures par niveau")
    public ResponseEntity<List<AdministrativeStructureDTO>> getStructuresByLevel(@PathVariable Integer level) {
        List<AdministrativeStructureDTO> structures = structureService.getStructuresByLevel(level);
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/root")
    @Operation(summary = "Obtenir les structures racines (sans parent)")
    public ResponseEntity<List<AdministrativeStructureDTO>> getRootStructures() {
        List<AdministrativeStructureDTO> structures = structureService.getRootStructures();
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/{parentId}/children")
    @Operation(summary = "Obtenir les sous-structures d'une structure")
    public ResponseEntity<List<AdministrativeStructureDTO>> getChildren(@PathVariable Long parentId) {
        List<AdministrativeStructureDTO> children = structureService.getChildren(parentId);
        return ResponseEntity.ok(children);
    }

    @GetMapping("/parent/{parentId}")
    @Operation(summary = "Obtenir les structures par parent")
    public ResponseEntity<List<AdministrativeStructureDTO>> getStructuresByParent(@PathVariable Long parentId) {
        List<AdministrativeStructureDTO> structures = structureService.getStructuresByParent(parentId);
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des structures")
    public ResponseEntity<Page<AdministrativeStructureDTO>> searchStructures(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AdministrativeStructureDTO> structures = structureService.searchStructures(searchTerm, pageable);
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/{id}/hierarchy")
    @Operation(summary = "Obtenir la hiérarchie complète d'une structure")
    public ResponseEntity<List<AdministrativeStructureDTO>> getHierarchyTree(@PathVariable Long id) {
        List<AdministrativeStructureDTO> hierarchy = structureService.getHierarchyTree(id);
        return ResponseEntity.ok(hierarchy);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Obtenir les statistiques des structures")
    public ResponseEntity<Map<String, Object>> getStructureStatistics() {
        Map<String, Object> stats = structureService.getStructureStatistics();
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une structure (soft delete)")
    public ResponseEntity<Void> deleteStructure(@PathVariable Long id) {
        structureService.deleteStructure(id);
        return ResponseEntity.noContent().build();
    }
}
