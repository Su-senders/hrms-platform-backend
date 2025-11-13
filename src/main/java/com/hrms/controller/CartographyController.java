package com.hrms.controller;

import com.hrms.dto.CartographyDTO;
import com.hrms.dto.CartographyFilterDTO;
import com.hrms.service.CartographyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartography")
@RequiredArgsConstructor
@Tag(name = "Cartographie", description = "API de cartographie des personnels par structure et poste")
@CrossOrigin(origins = "*")
public class CartographyController {

    private final CartographyService cartographyService;

    /**
     * Obtenir la cartographie complète avec filtres
     */
    @PostMapping
    @Operation(summary = "Obtenir la cartographie complète avec filtres personnalisés")
    public ResponseEntity<CartographyDTO> getCartography(@RequestBody(required = false) CartographyFilterDTO filters) {
        if (filters == null) {
            filters = CartographyFilterDTO.builder().build();
        }
        CartographyDTO cartography = cartographyService.getCartography(filters);
        return ResponseEntity.ok(cartography);
    }

    /**
     * Obtenir la cartographie complète (sans filtres)
     */
    @GetMapping
    @Operation(summary = "Obtenir la cartographie complète de toutes les structures")
    public ResponseEntity<CartographyDTO> getFullCartography() {
        CartographyFilterDTO filters = CartographyFilterDTO.builder().build();
        CartographyDTO cartography = cartographyService.getCartography(filters);
        return ResponseEntity.ok(cartography);
    }

    /**
     * Obtenir la cartographie d'une structure spécifique
     */
    @GetMapping("/structure/{structureId}")
    @Operation(summary = "Obtenir la cartographie d'une structure spécifique")
    public ResponseEntity<CartographyDTO> getCartographyByStructure(
            @PathVariable Long structureId,
            @RequestParam(required = false) Boolean includeChildren) {
        CartographyFilterDTO filters = CartographyFilterDTO.builder()
                .structureId(structureId)
                .includeChildren(includeChildren != null ? includeChildren : false)
                .build();
        CartographyDTO cartography = cartographyService.getCartographyByStructure(structureId, filters);
        return ResponseEntity.ok(cartography);
    }

    /**
     * Obtenir la cartographie hiérarchique à partir d'une structure racine
     */
    @GetMapping("/hierarchical/{rootStructureId}")
    @Operation(summary = "Obtenir la cartographie hiérarchique (structure parent → enfants)")
    public ResponseEntity<CartographyDTO> getHierarchicalCartography(
            @PathVariable Long rootStructureId) {
        CartographyFilterDTO filters = CartographyFilterDTO.builder().build();
        CartographyDTO cartography = cartographyService.getHierarchicalCartography(rootStructureId, filters);
        return ResponseEntity.ok(cartography);
    }

    /**
     * Obtenir la cartographie filtrée par type de structure
     */
    @GetMapping("/type/{structureType}")
    @Operation(summary = "Obtenir la cartographie par type de structure (MINISTERE, DIRECTION, etc.)")
    public ResponseEntity<CartographyDTO> getCartographyByStructureType(
            @PathVariable String structureType) {
        CartographyFilterDTO filters = CartographyFilterDTO.builder()
                .structureType(structureType)
                .build();
        CartographyDTO cartography = cartographyService.getCartography(filters);
        return ResponseEntity.ok(cartography);
    }

    /**
     * Obtenir la cartographie filtrée par statut de poste
     */
    @GetMapping("/positions/{status}")
    @Operation(summary = "Obtenir la cartographie filtrée par statut de poste (VACANT, OCCUPE)")
    public ResponseEntity<CartographyDTO> getCartographyByPositionStatus(
            @PathVariable String status) {
        CartographyFilterDTO filters = CartographyFilterDTO.builder()
                .positionStatus(status)
                .build();
        CartographyDTO cartography = cartographyService.getCartography(filters);
        return ResponseEntity.ok(cartography);
    }

    /**
     * Obtenir la cartographie filtrée par grade
     */
    @GetMapping("/grade/{grade}")
    @Operation(summary = "Obtenir la cartographie filtrée par grade")
    public ResponseEntity<CartographyDTO> getCartographyByGrade(
            @PathVariable String grade) {
        CartographyFilterDTO filters = CartographyFilterDTO.builder()
                .grade(grade)
                .build();
        CartographyDTO cartography = cartographyService.getCartography(filters);
        return ResponseEntity.ok(cartography);
    }

    /**
     * Obtenir la cartographie filtrée par corps de métier
     */
    @GetMapping("/corps/{corps}")
    @Operation(summary = "Obtenir la cartographie filtrée par corps de métier")
    public ResponseEntity<CartographyDTO> getCartographyByCorps(
            @PathVariable String corps) {
        CartographyFilterDTO filters = CartographyFilterDTO.builder()
                .corps(corps)
                .build();
        CartographyDTO cartography = cartographyService.getCartography(filters);
        return ResponseEntity.ok(cartography);
    }

    /**
     * Obtenir la cartographie filtrée par situation du personnel
     */
    @GetMapping("/situation/{situation}")
    @Operation(summary = "Obtenir la cartographie filtrée par situation du personnel")
    public ResponseEntity<CartographyDTO> getCartographyBySituation(
            @PathVariable String situation) {
        CartographyFilterDTO filters = CartographyFilterDTO.builder()
                .situation(situation)
                .build();
        CartographyDTO cartography = cartographyService.getCartography(filters);
        return ResponseEntity.ok(cartography);
    }

    /**
     * Obtenir la cartographie filtrée par rang de poste
     */
    @GetMapping("/rank/{rank}")
    @Operation(summary = "Obtenir la cartographie filtrée par rang de poste")
    public ResponseEntity<CartographyDTO> getCartographyByRank(
            @PathVariable String rank) {
        CartographyFilterDTO filters = CartographyFilterDTO.builder()
                .rank(rank)
                .build();
        CartographyDTO cartography = cartographyService.getCartography(filters);
        return ResponseEntity.ok(cartography);
    }

    /**
     * Obtenir uniquement les postes occupés
     */
    @GetMapping("/occupied")
    @Operation(summary = "Obtenir la cartographie des postes occupés uniquement")
    public ResponseEntity<CartographyDTO> getOccupiedPositionsCartography() {
        CartographyFilterDTO filters = CartographyFilterDTO.builder()
                .onlyOccupied(true)
                .build();
        CartographyDTO cartography = cartographyService.getCartography(filters);
        return ResponseEntity.ok(cartography);
    }

    /**
     * Obtenir uniquement les postes vacants
     */
    @GetMapping("/vacant")
    @Operation(summary = "Obtenir la cartographie des postes vacants uniquement")
    public ResponseEntity<CartographyDTO> getVacantPositionsCartography() {
        CartographyFilterDTO filters = CartographyFilterDTO.builder()
                .onlyVacant(true)
                .build();
        CartographyDTO cartography = cartographyService.getCartography(filters);
        return ResponseEntity.ok(cartography);
    }
}




