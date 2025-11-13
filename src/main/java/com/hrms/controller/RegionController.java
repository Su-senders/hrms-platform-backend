package com.hrms.controller;

import com.hrms.dto.GeographicStatisticsDTO;
import com.hrms.dto.RegionDTO;
import com.hrms.service.GeographicService;
import com.hrms.service.GeographicStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des régions géographiques
 */
@RestController
@RequestMapping("/api/geography/regions")
@RequiredArgsConstructor
@Tag(name = "Geography - Regions", description = "API de consultation des régions géographiques du Cameroun")
@CrossOrigin(origins = "*")
public class RegionController {

    private final GeographicService geographicService;
    private final GeographicStatisticsService statisticsService;

    @GetMapping
    @Operation(summary = "Obtenir toutes les régions actives", 
               description = "Retourne la liste de toutes les régions actives, triées par nom")
    public ResponseEntity<List<RegionDTO>> getAllRegions() {
        List<RegionDTO> regions = geographicService.getAllRegions();
        return ResponseEntity.ok(regions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une région par ID", 
               description = "Retourne les détails d'une région avec ses statistiques")
    public ResponseEntity<RegionDTO> getRegionById(@PathVariable Long id) {
        RegionDTO region = geographicService.getRegionById(id);
        return ResponseEntity.ok(region);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Obtenir une région par code", 
               description = "Retourne les détails d'une région par son code (ex: CE, AD, EN)")
    public ResponseEntity<RegionDTO> getRegionByCode(@PathVariable String code) {
        RegionDTO region = geographicService.getRegionByCode(code);
        return ResponseEntity.ok(region);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des régions", 
               description = "Recherche des régions par nom ou chef-lieu")
    public ResponseEntity<List<RegionDTO>> searchRegions(
            @RequestParam String searchTerm) {
        List<RegionDTO> regions = geographicService.searchRegions(searchTerm);
        return ResponseEntity.ok(regions);
    }

    @GetMapping("/{id}/departments")
    @Operation(summary = "Obtenir les départements d'une région", 
               description = "Retourne la liste de tous les départements d'une région donnée")
    public ResponseEntity<List<com.hrms.dto.DepartmentDTO>> getDepartmentsByRegion(
            @PathVariable Long id) {
        List<com.hrms.dto.DepartmentDTO> departments = geographicService.getDepartmentsByRegion(id);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/code/{code}/departments")
    @Operation(summary = "Obtenir les départements d'une région par code", 
               description = "Retourne la liste de tous les départements d'une région par son code")
    public ResponseEntity<List<com.hrms.dto.DepartmentDTO>> getDepartmentsByRegionCode(
            @PathVariable String code) {
        List<com.hrms.dto.DepartmentDTO> departments = geographicService.getDepartmentsByRegionCode(code);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}/statistics")
    @Operation(summary = "Obtenir les statistiques d'une région", 
               description = "Retourne les statistiques détaillées d'une région (nombre de départements, arrondissements, personnel)")
    public ResponseEntity<GeographicStatisticsDTO.RegionStatisticsDTO> getRegionStatistics(
            @PathVariable Long id) {
        GeographicStatisticsDTO.RegionStatisticsDTO stats = statisticsService.getRegionStatistics(id);
        return ResponseEntity.ok(stats);
    }
}

