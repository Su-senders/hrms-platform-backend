package com.hrms.controller;

import com.hrms.dto.ArrondissementDTO;
import com.hrms.dto.GeographicStatisticsDTO;
import com.hrms.service.GeographicService;
import com.hrms.service.GeographicStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des arrondissements géographiques
 */
@RestController
@RequestMapping("/api/geography/arrondissements")
@RequiredArgsConstructor
@Tag(name = "Geography - Arrondissements", description = "API de consultation des arrondissements géographiques du Cameroun")
@CrossOrigin(origins = "*")
public class ArrondissementController {

    private final GeographicService geographicService;
    private final GeographicStatisticsService statisticsService;

    @GetMapping
    @Operation(summary = "Obtenir tous les arrondissements actifs", 
               description = "Retourne la liste de tous les arrondissements actifs, avec filtres optionnels")
    public ResponseEntity<List<ArrondissementDTO>> getAllArrondissements(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long departmentId) {
        List<ArrondissementDTO> arrondissements;
        
        if (regionId != null) {
            arrondissements = geographicService.getArrondissementsByRegionId(regionId);
        } else if (departmentId != null) {
            arrondissements = geographicService.getArrondissementsByDepartmentId(departmentId);
        } else {
            arrondissements = geographicService.getAllArrondissements();
        }
        
        return ResponseEntity.ok(arrondissements);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un arrondissement par ID", 
               description = "Retourne les détails d'un arrondissement avec ses informations de département et région")
    public ResponseEntity<ArrondissementDTO> getArrondissementById(@PathVariable Long id) {
        ArrondissementDTO arrondissement = geographicService.getArrondissementById(id);
        return ResponseEntity.ok(arrondissement);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Obtenir un arrondissement par code", 
               description = "Retourne les détails d'un arrondissement par son code (ex: CE-MFOU-YDE1)")
    public ResponseEntity<ArrondissementDTO> getArrondissementByCode(@PathVariable String code) {
        ArrondissementDTO arrondissement = geographicService.getArrondissementByCode(code);
        return ResponseEntity.ok(arrondissement);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des arrondissements", 
               description = "Recherche des arrondissements par nom ou chef-lieu")
    public ResponseEntity<List<ArrondissementDTO>> searchArrondissements(
            @RequestParam String searchTerm) {
        List<ArrondissementDTO> arrondissements = geographicService.searchArrondissements(searchTerm);
        return ResponseEntity.ok(arrondissements);
    }

    @GetMapping("/{id}/statistics")
    @Operation(summary = "Obtenir les statistiques d'un arrondissement", 
               description = "Retourne les statistiques détaillées d'un arrondissement (nombre de personnel)")
    public ResponseEntity<GeographicStatisticsDTO.ArrondissementStatisticsDTO> getArrondissementStatistics(
            @PathVariable Long id) {
        GeographicStatisticsDTO.ArrondissementStatisticsDTO stats = statisticsService.getArrondissementStatistics(id);
        return ResponseEntity.ok(stats);
    }
}

