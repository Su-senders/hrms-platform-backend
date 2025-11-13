package com.hrms.controller;

import com.hrms.dto.TrainingCostCreateDTO;
import com.hrms.dto.TrainingCostDTO;
import com.hrms.dto.TrainingCostUpdateDTO;
import com.hrms.service.TrainingCostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/training-costs")
@RequiredArgsConstructor
@Tag(name = "Coûts de Formation", description = "API de gestion des coûts de formation")
@CrossOrigin(origins = "*")
public class TrainingCostController {

    private final TrainingCostService costService;

    @PostMapping
    @Operation(summary = "Créer un nouveau coût")
    public ResponseEntity<TrainingCostDTO> createCost(@Valid @RequestBody TrainingCostCreateDTO dto) {
        TrainingCostDTO created = costService.createCost(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un coût")
    public ResponseEntity<TrainingCostDTO> updateCost(
            @PathVariable Long id,
            @Valid @RequestBody TrainingCostUpdateDTO dto) {
        TrainingCostDTO updated = costService.updateCost(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un coût par ID")
    public ResponseEntity<TrainingCostDTO> getCostById(@PathVariable Long id) {
        TrainingCostDTO cost = costService.getCostById(id);
        return ResponseEntity.ok(cost);
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Obtenir les coûts d'une session")
    public ResponseEntity<List<TrainingCostDTO>> getCostsBySession(@PathVariable Long sessionId) {
        List<TrainingCostDTO> costs = costService.getCostsBySession(sessionId);
        return ResponseEntity.ok(costs);
    }

    @GetMapping("/session/{sessionId}/type/{costType}")
    @Operation(summary = "Obtenir les coûts d'une session par type")
    public ResponseEntity<List<TrainingCostDTO>> getCostsBySessionAndType(
            @PathVariable Long sessionId,
            @PathVariable String costType) {
        List<TrainingCostDTO> costs = costService.getCostsBySessionAndType(sessionId, costType);
        return ResponseEntity.ok(costs);
    }

    @GetMapping("/session/{sessionId}/total")
    @Operation(summary = "Obtenir le coût total d'une session")
    public ResponseEntity<BigDecimal> getTotalCostBySession(@PathVariable Long sessionId) {
        BigDecimal total = costService.getTotalCostBySession(sessionId);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/{id}/mark-paid")
    @Operation(summary = "Marquer un coût comme payé")
    public ResponseEntity<TrainingCostDTO> markAsPaid(@PathVariable Long id) {
        TrainingCostDTO cost = costService.markAsPaid(id);
        return ResponseEntity.ok(cost);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un coût (soft delete)")
    public ResponseEntity<Void> deleteCost(@PathVariable Long id) {
        costService.deleteCost(id);
        return ResponseEntity.noContent().build();
    }
}




