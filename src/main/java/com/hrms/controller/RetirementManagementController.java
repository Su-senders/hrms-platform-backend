package com.hrms.controller;

import com.hrms.dto.RetirablePersonnelDTO;
import com.hrms.service.RetirementManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des départs à la retraite
 */
@RestController
@RequestMapping("/api/retirement")
@RequiredArgsConstructor
@Tag(name = "Gestion des Retraites", description = "API de gestion des départs à la retraite")
@CrossOrigin(origins = "*")
public class RetirementManagementController {

    private final RetirementManagementService retirementService;

    /**
     * Obtenir tous les personnels retraitables (ayant atteint l'âge de la retraite)
     */
    @GetMapping("/retirable")
    @Operation(summary = "Obtenir la liste de tous les personnels retraitables (paginé)")
    public ResponseEntity<Page<RetirablePersonnelDTO>> getRetirablePersonnel(
            @PageableDefault(size = 20, sort = "retirementDate", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<RetirablePersonnelDTO> retirable = retirementService.getRetirablePersonnelPaginated(pageable);
        return ResponseEntity.ok(retirable);
    }

    /**
     * Obtenir les personnels retraitables pour une année spécifique
     */
    @GetMapping("/retirable/year/{year}")
    @Operation(summary = "Obtenir les personnels qui partent à la retraite une année donnée")
    public ResponseEntity<List<RetirablePersonnelDTO>> getRetirablePersonnelByYear(@PathVariable int year) {
        List<RetirablePersonnelDTO> retirable = retirementService.getRetirablePersonnelByYear(year);
        return ResponseEntity.ok(retirable);
    }

    /**
     * Obtenir les personnels retraitables d'une structure pour une année
     */
    @GetMapping("/retirable/structure/{structureId}/year/{year}")
    @Operation(summary = "Obtenir les retraites d'une structure pour une année donnée")
    public ResponseEntity<List<RetirablePersonnelDTO>> getRetirablePersonnelByStructureAndYear(
            @PathVariable Long structureId,
            @PathVariable int year) {
        List<RetirablePersonnelDTO> retirable = retirementService
                .getRetirablePersonnelByStructureAndYear(structureId, year);
        return ResponseEntity.ok(retirable);
    }

    /**
     * Obtenir les personnels en pré-retraite (3-6 mois avant le départ)
     */
    @GetMapping("/pre-retirement")
    @Operation(summary = "Obtenir les personnels en pré-retraite (dans les 3 à 6 mois)")
    public ResponseEntity<Page<RetirablePersonnelDTO>> getPreRetirementPersonnel(
            @PageableDefault(size = 20, sort = "retirementDate", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<RetirablePersonnelDTO> preRetirement = retirementService.getPreRetirementPersonnelPaginated(pageable);
        return ResponseEntity.ok(preRetirement);
    }

    /**
     * Obtenir les personnels qui partent à la retraite dans les N prochains mois
     */
    @GetMapping("/upcoming/{months}")
    @Operation(summary = "Obtenir les départs à la retraite dans les N prochains mois")
    public ResponseEntity<List<RetirablePersonnelDTO>> getUpcomingRetirements(@PathVariable int months) {
        List<RetirablePersonnelDTO> upcoming = retirementService.getRetirableWithinMonths(months);
        return ResponseEntity.ok(upcoming);
    }

    /**
     * Obtenir les statistiques de retraite par année
     */
    @GetMapping("/statistics/by-year")
    @Operation(summary = "Obtenir le nombre de départs à la retraite par année")
    public ResponseEntity<Map<Integer, Long>> getRetirementStatisticsByYear(
            @RequestParam int startYear,
            @RequestParam int endYear) {
        Map<Integer, Long> stats = retirementService.getRetirementStatisticsByYear(startYear, endYear);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtenir les statistiques de retraite par structure
     */
    @GetMapping("/statistics/by-structure")
    @Operation(summary = "Obtenir le nombre de départs à la retraite par structure pour une période")
    public ResponseEntity<Map<Long, Long>> getRetirementStatisticsByStructure(
            @RequestParam int startYear,
            @RequestParam int endYear) {
        Map<Long, Long> stats = retirementService.getRetirementStatisticsByStructure(startYear, endYear);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtenir les statistiques globales de retraite
     */
    @GetMapping("/statistics/global")
    @Operation(summary = "Obtenir les statistiques globales de retraite (5 prochaines années)")
    public ResponseEntity<Map<String, Object>> getGlobalRetirementStatistics() {
        Map<String, Object> stats = retirementService.getGlobalRetirementStatistics();
        return ResponseEntity.ok(stats);
    }
}
