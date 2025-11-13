package com.hrms.controller;

import com.hrms.dto.GeographicStatisticsDTO;
import com.hrms.service.GeographicStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour les statistiques géographiques
 */
@RestController
@RequestMapping("/api/geography/statistics")
@RequiredArgsConstructor
@Tag(name = "Geography - Statistics", description = "API de statistiques géographiques")
@CrossOrigin(origins = "*")
public class GeographicStatisticsController {

    private final GeographicStatisticsService statisticsService;

    @GetMapping("/global")
    @Operation(summary = "Obtenir les statistiques géographiques globales", 
               description = "Retourne les statistiques globales : nombre de régions/départements/arrondissements et répartition du personnel")
    public ResponseEntity<GeographicStatisticsDTO> getGlobalStatistics() {
        GeographicStatisticsDTO stats = statisticsService.getGlobalStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/regions/{regionId}")
    @Operation(summary = "Obtenir les statistiques d'une région", 
               description = "Retourne les statistiques détaillées d'une région")
    public ResponseEntity<GeographicStatisticsDTO.RegionStatisticsDTO> getRegionStatistics(
            @PathVariable Long regionId) {
        GeographicStatisticsDTO.RegionStatisticsDTO stats = statisticsService.getRegionStatistics(regionId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/departments/{departmentId}")
    @Operation(summary = "Obtenir les statistiques d'un département", 
               description = "Retourne les statistiques détaillées d'un département")
    public ResponseEntity<GeographicStatisticsDTO.DepartmentStatisticsDTO> getDepartmentStatistics(
            @PathVariable Long departmentId) {
        GeographicStatisticsDTO.DepartmentStatisticsDTO stats = statisticsService.getDepartmentStatistics(departmentId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/arrondissements/{arrondissementId}")
    @Operation(summary = "Obtenir les statistiques d'un arrondissement", 
               description = "Retourne les statistiques détaillées d'un arrondissement")
    public ResponseEntity<GeographicStatisticsDTO.ArrondissementStatisticsDTO> getArrondissementStatistics(
            @PathVariable Long arrondissementId) {
        GeographicStatisticsDTO.ArrondissementStatisticsDTO stats = statisticsService.getArrondissementStatistics(arrondissementId);
        return ResponseEntity.ok(stats);
    }
}

