package com.hrms.controller;

import com.hrms.dto.CareerMovementCreateDTO;
import com.hrms.dto.CareerMovementDTO;
import com.hrms.dto.CareerMovementUpdateDTO;
import com.hrms.dto.GlobalMovementStatisticsDTO;
import com.hrms.dto.StructureMovementStatisticsDTO;
import com.hrms.service.CareerMovementService;
import com.hrms.service.CareerMovementStatisticsService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/career-movements")
@RequiredArgsConstructor
@Tag(name = "Career Movements", description = "API de gestion des mouvements de carrière")
@CrossOrigin(origins = "*")
public class CareerMovementController {

    private final CareerMovementService careerMovementService;
    private final CareerMovementStatisticsService statisticsService;

    @PostMapping
    @Operation(summary = "Créer un mouvement de carrière")
    public ResponseEntity<CareerMovementDTO> createMovement(@Valid @RequestBody CareerMovementCreateDTO dto) {
        CareerMovementDTO created = careerMovementService.createMovement(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un mouvement de carrière")
    public ResponseEntity<CareerMovementDTO> updateMovement(
            @PathVariable Long id,
            @Valid @RequestBody CareerMovementUpdateDTO dto) {
        CareerMovementDTO updated = careerMovementService.updateMovement(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un mouvement par ID")
    public ResponseEntity<CareerMovementDTO> getMovementById(@PathVariable Long id) {
        CareerMovementDTO movement = careerMovementService.getMovementById(id);
        return ResponseEntity.ok(movement);
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les mouvements (paginé)")
    public ResponseEntity<Page<CareerMovementDTO>> getAllMovements(
            @PageableDefault(size = 20, sort = "movementDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CareerMovementDTO> movements = careerMovementService.getAllMovements(pageable);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/personnel/{personnelId}")
    @Operation(summary = "Obtenir les mouvements d'un personnel")
    public ResponseEntity<Page<CareerMovementDTO>> getMovementsByPersonnel(
            @PathVariable Long personnelId,
            @PageableDefault(size = 20, sort = "movementDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CareerMovementDTO> movements = careerMovementService.getMovementsByPersonnel(personnelId, pageable);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Obtenir les mouvements par type")
    public ResponseEntity<Page<CareerMovementDTO>> getMovementsByType(
            @PathVariable String type,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CareerMovementDTO> movements = careerMovementService.getMovementsByType(type, pageable);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtenir les mouvements par statut")
    public ResponseEntity<Page<CareerMovementDTO>> getMovementsByStatus(
            @PathVariable String status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CareerMovementDTO> movements = careerMovementService.getMovementsByStatus(status, pageable);
        return ResponseEntity.ok(movements);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approuver un mouvement de carrière")
    public ResponseEntity<CareerMovementDTO> approveMovement(@PathVariable Long id) {
        CareerMovementDTO approved = careerMovementService.approveMovement(id);
        return ResponseEntity.ok(approved);
    }

    @PostMapping("/{id}/execute")
    @Operation(summary = "Exécuter un mouvement de carrière")
    public ResponseEntity<CareerMovementDTO> executeMovement(@PathVariable Long id) {
        CareerMovementDTO executed = careerMovementService.executeMovement(id);
        return ResponseEntity.ok(executed);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler un mouvement de carrière")
    public ResponseEntity<CareerMovementDTO> cancelMovement(@PathVariable Long id) {
        CareerMovementDTO cancelled = careerMovementService.cancelMovement(id);
        return ResponseEntity.ok(cancelled);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un mouvement (soft delete)")
    public ResponseEntity<Void> deleteMovement(@PathVariable Long id) {
        careerMovementService.deleteMovement(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== STATISTIQUES ====================

    @GetMapping("/statistics/global")
    @Operation(summary = "Obtenir les statistiques globales de mouvements")
    public ResponseEntity<GlobalMovementStatisticsDTO> getGlobalStatistics(
            @RequestParam(required = false) Integer year) {
        GlobalMovementStatisticsDTO stats = statisticsService.getGlobalStatistics(year);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/structure/{structureId}")
    @Operation(summary = "Obtenir les statistiques de mouvements par structure")
    public ResponseEntity<StructureMovementStatisticsDTO> getStructureStatistics(
            @PathVariable Long structureId,
            @RequestParam(required = false) Integer year) {
        StructureMovementStatisticsDTO stats = statisticsService.getStructureStatistics(structureId, year);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/by-type")
    @Operation(summary = "Obtenir les statistiques par type de mouvement")
    public ResponseEntity<Map<String, Long>> getStatisticsByType() {
        Map<String, Long> stats = statisticsService.getStatisticsByType();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/by-status")
    @Operation(summary = "Obtenir les statistiques par statut de mouvement")
    public ResponseEntity<Map<String, Long>> getStatisticsByStatus() {
        Map<String, Long> stats = statisticsService.getStatisticsByStatus();
        return ResponseEntity.ok(stats);
    }
}
