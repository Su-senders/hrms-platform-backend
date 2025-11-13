package com.hrms.controller;

import com.hrms.dto.*;
import com.hrms.service.PositionService;
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
@RequestMapping("/api/positions")
@RequiredArgsConstructor
@Tag(name = "Positions", description = "API de gestion des postes de travail")
@CrossOrigin(origins = "*")
public class PositionController {

    private final PositionService positionService;

    @PostMapping
    @Operation(summary = "Créer un nouveau poste")
    public ResponseEntity<PositionDTO> createPosition(@Valid @RequestBody PositionCreateDTO dto) {
        PositionDTO created = positionService.createPosition(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un poste")
    public ResponseEntity<PositionDTO> updatePosition(
            @PathVariable Long id,
            @Valid @RequestBody PositionUpdateDTO dto) {
        PositionDTO updated = positionService.updatePosition(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un poste par ID")
    public ResponseEntity<PositionDTO> getPositionById(@PathVariable Long id) {
        PositionDTO position = positionService.getPositionById(id);
        return ResponseEntity.ok(position);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Obtenir un poste par code")
    public ResponseEntity<PositionDTO> getPositionByCode(@PathVariable String code) {
        PositionDTO position = positionService.getPositionByCode(code);
        return ResponseEntity.ok(position);
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les postes (paginé)")
    public ResponseEntity<Page<PositionDTO>> getAllPositions(
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<PositionDTO> positions = positionService.getAllPositions(pageable);
        return ResponseEntity.ok(positions);
    }

    @PostMapping("/search")
    @Operation(summary = "Rechercher des postes")
    public ResponseEntity<Page<PositionDTO>> searchPositions(
            @RequestBody PositionSearchDTO searchDTO,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<PositionDTO> results = positionService.searchPositions(searchDTO, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/vacant")
    @Operation(summary = "Obtenir les postes vacants")
    public ResponseEntity<List<PositionDTO>> getVacantPositions() {
        List<PositionDTO> vacant = positionService.getVacantPositions();
        return ResponseEntity.ok(vacant);
    }

    @GetMapping("/vacant/structure/{structureId}")
    @Operation(summary = "Obtenir les postes vacants par structure")
    public ResponseEntity<List<PositionDTO>> getVacantPositionsByStructure(@PathVariable Long structureId) {
        List<PositionDTO> vacant = positionService.getVacantPositionsByStructure(structureId);
        return ResponseEntity.ok(vacant);
    }

    @GetMapping("/occupied")
    @Operation(summary = "Obtenir les postes occupés")
    public ResponseEntity<List<PositionDTO>> getOccupiedPositions() {
        List<PositionDTO> occupied = positionService.getOccupiedPositions();
        return ResponseEntity.ok(occupied);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtenir les postes par statut")
    public ResponseEntity<Page<PositionDTO>> getPositionsByStatus(
            @PathVariable String status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PositionDTO> results = positionService.getPositionsByStatus(status, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/rank/{rank}")
    @Operation(summary = "Obtenir les postes par rang")
    public ResponseEntity<Page<PositionDTO>> getPositionsByRank(
            @PathVariable String rank,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PositionDTO> results = positionService.getPositionsByRank(rank, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/structure/{structureId}")
    @Operation(summary = "Obtenir les postes par structure")
    public ResponseEntity<Page<PositionDTO>> getPositionsByStructure(
            @PathVariable Long structureId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PositionDTO> results = positionService.getPositionsByStructure(structureId, pageable);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{positionId}/assign/{personnelId}")
    @Operation(summary = "Affecter un personnel à un poste")
    public ResponseEntity<PositionDTO> assignPersonnel(
            @PathVariable Long positionId,
            @PathVariable Long personnelId) {
        PositionDTO position = positionService.assignPersonnelToPosition(positionId, personnelId);
        return ResponseEntity.ok(position);
    }

    @PostMapping("/{positionId}/release")
    @Operation(summary = "Libérer un personnel d'un poste")
    public ResponseEntity<PositionDTO> releasePersonnel(@PathVariable Long positionId) {
        PositionDTO position = positionService.releasePersonnelFromPosition(positionId);
        return ResponseEntity.ok(position);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Obtenir les statistiques des postes")
    public ResponseEntity<Map<String, Object>> getPositionStatistics() {
        Map<String, Object> stats = positionService.getPositionStatistics();
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un poste (soft delete)")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
        return ResponseEntity.noContent().build();
    }
}
