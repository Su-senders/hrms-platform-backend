package com.hrms.controller;

import com.hrms.entity.AssignmentHistory;
import com.hrms.service.AssignmentHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion de l'historique des affectations
 */
@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Tag(name = "Historique des Affectations", description = "API de gestion de l'historique des mouvements et affectations")
@CrossOrigin(origins = "*")
public class AssignmentHistoryController {

    private final AssignmentHistoryService assignmentService;

    /**
     * Obtenir l'historique complet des affectations d'un personnel
     */
    @GetMapping("/personnel/{personnelId}")
    @Operation(summary = "Obtenir l'historique complet des affectations d'un personnel")
    public ResponseEntity<List<AssignmentHistory>> getPersonnelAssignmentHistory(@PathVariable Long personnelId) {
        List<AssignmentHistory> history = assignmentService.getPersonnelAssignmentHistory(personnelId);
        return ResponseEntity.ok(history);
    }

    /**
     * Obtenir l'affectation active d'un personnel
     */
    @GetMapping("/personnel/{personnelId}/active")
    @Operation(summary = "Obtenir l'affectation actuellement active d'un personnel")
    public ResponseEntity<AssignmentHistory> getActiveAssignment(@PathVariable Long personnelId) {
        AssignmentHistory active = assignmentService.getActiveAssignment(personnelId);
        if (active == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(active);
    }

    /**
     * Obtenir l'historique des affectations d'un poste
     */
    @GetMapping("/position/{positionId}")
    @Operation(summary = "Obtenir l'historique de tous les personnels qui ont occupé un poste")
    public ResponseEntity<List<AssignmentHistory>> getPositionAssignmentHistory(@PathVariable Long positionId) {
        List<AssignmentHistory> history = assignmentService.getPositionAssignmentHistory(positionId);
        return ResponseEntity.ok(history);
    }

    /**
     * Obtenir l'historique des affectations d'une structure
     */
    @GetMapping("/structure/{structureId}")
    @Operation(summary = "Obtenir l'historique de toutes les affectations dans une structure")
    public ResponseEntity<List<AssignmentHistory>> getStructureAssignmentHistory(@PathVariable Long structureId) {
        List<AssignmentHistory> history = assignmentService.getStructureAssignmentHistory(structureId);
        return ResponseEntity.ok(history);
    }

    /**
     * Obtenir toutes les affectations actives d'une structure
     */
    @GetMapping("/structure/{structureId}/active")
    @Operation(summary = "Obtenir toutes les affectations actuellement actives dans une structure")
    public ResponseEntity<List<AssignmentHistory>> getActiveAssignmentsByStructure(@PathVariable Long structureId) {
        List<AssignmentHistory> active = assignmentService.getActiveAssignmentsByStructure(structureId);
        return ResponseEntity.ok(active);
    }

    /**
     * Obtenir les affectations par type de mouvement
     */
    @GetMapping("/by-type/{movementType}")
    @Operation(summary = "Obtenir les affectations d'un type spécifique (AFFECTATION, MUTATION, DETACHEMENT, etc.)")
    public ResponseEntity<List<AssignmentHistory>> getAssignmentsByMovementType(
            @PathVariable AssignmentHistory.MovementType movementType) {
        List<AssignmentHistory> assignments = assignmentService.getAssignmentsByMovementType(movementType);
        return ResponseEntity.ok(assignments);
    }

    /**
     * Obtenir les affectations sur une période
     */
    @GetMapping("/by-period")
    @Operation(summary = "Obtenir toutes les affectations sur une période donnée")
    public ResponseEntity<List<AssignmentHistory>> getAssignmentsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AssignmentHistory> assignments = assignmentService.getAssignmentsByPeriod(startDate, endDate);
        return ResponseEntity.ok(assignments);
    }

    /**
     * Compter le nombre d'affectations d'un personnel
     */
    @GetMapping("/personnel/{personnelId}/count")
    @Operation(summary = "Compter le nombre total d'affectations d'un personnel")
    public ResponseEntity<Long> countPersonnelAssignments(@PathVariable Long personnelId) {
        long count = assignmentService.countPersonnelAssignments(personnelId);
        return ResponseEntity.ok(count);
    }

    /**
     * Attacher un document de décision à une affectation
     */
    @PutMapping("/{assignmentId}/attach-document")
    @Operation(summary = "Attacher un document de décision à une affectation existante")
    public ResponseEntity<AssignmentHistory> attachDecisionDocument(
            @PathVariable Long assignmentId,
            @RequestParam String documentPath,
            @RequestParam(required = false) String decisionNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate decisionDate) {

        AssignmentHistory updated = assignmentService.attachDecisionDocument(
                assignmentId, documentPath, decisionNumber, decisionDate);
        return ResponseEntity.ok(updated);
    }

    /**
     * Terminer une affectation
     */
    @PutMapping("/{assignmentId}/end")
    @Operation(summary = "Terminer une affectation active (fin de mission, mutation, etc.)")
    public ResponseEntity<AssignmentHistory> endAssignment(
            @PathVariable Long assignmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        AssignmentHistory ended = assignmentService.endAssignment(assignmentId, endDate);
        return ResponseEntity.ok(ended);
    }

    /**
     * Annuler une affectation
     */
    @PutMapping("/{assignmentId}/cancel")
    @Operation(summary = "Annuler une affectation (erreur administrative, etc.)")
    public ResponseEntity<AssignmentHistory> cancelAssignment(
            @PathVariable Long assignmentId,
            @RequestParam String reason) {

        AssignmentHistory cancelled = assignmentService.cancelAssignment(assignmentId, reason);
        return ResponseEntity.ok(cancelled);
    }

    /**
     * Obtenir les statistiques des affectations
     */
    @GetMapping("/statistics")
    @Operation(summary = "Obtenir les statistiques globales des affectations")
    public ResponseEntity<Map<String, Object>> getAssignmentStatistics() {
        Map<String, Object> stats = assignmentService.getAssignmentStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtenir les statistiques par type de mouvement
     */
    @GetMapping("/statistics/by-movement-type")
    @Operation(summary = "Obtenir le nombre d'affectations par type de mouvement")
    public ResponseEntity<Map<AssignmentHistory.MovementType, Long>> getStatisticsByMovementType() {
        Map<AssignmentHistory.MovementType, Long> stats = assignmentService.getStatisticsByMovementType();
        return ResponseEntity.ok(stats);
    }
}
