package com.hrms.controller;

import com.hrms.dto.DepartmentDTO;
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
 * Contrôleur REST pour la gestion des départements géographiques
 */
@RestController
@RequestMapping("/api/geography/departments")
@RequiredArgsConstructor
@Tag(name = "Geography - Departments", description = "API de consultation des départements géographiques du Cameroun")
@CrossOrigin(origins = "*")
public class DepartmentController {

    private final GeographicService geographicService;
    private final GeographicStatisticsService statisticsService;

    @GetMapping
    @Operation(summary = "Obtenir tous les départements actifs", 
               description = "Retourne la liste de tous les départements actifs")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments(
            @RequestParam(required = false) Long regionId) {
        List<DepartmentDTO> departments;
        if (regionId != null) {
            departments = geographicService.getDepartmentsByRegionId(regionId);
        } else {
            departments = geographicService.getAllDepartments();
        }
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un département par ID", 
               description = "Retourne les détails d'un département avec ses informations de région")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        DepartmentDTO department = geographicService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Obtenir un département par code", 
               description = "Retourne les détails d'un département par son code (ex: CE-MFOU, AD-DJER)")
    public ResponseEntity<DepartmentDTO> getDepartmentByCode(@PathVariable String code) {
        DepartmentDTO department = geographicService.getDepartmentByCode(code);
        return ResponseEntity.ok(department);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des départements", 
               description = "Recherche des départements par nom ou chef-lieu")
    public ResponseEntity<List<DepartmentDTO>> searchDepartments(
            @RequestParam String searchTerm) {
        List<DepartmentDTO> departments = geographicService.searchDepartments(searchTerm);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}/arrondissements")
    @Operation(summary = "Obtenir les arrondissements d'un département", 
               description = "Retourne la liste de tous les arrondissements d'un département donné")
    public ResponseEntity<List<com.hrms.dto.ArrondissementDTO>> getArrondissementsByDepartment(
            @PathVariable Long id) {
        List<com.hrms.dto.ArrondissementDTO> arrondissements = geographicService.getArrondissementsByDepartment(id);
        return ResponseEntity.ok(arrondissements);
    }

    @GetMapping("/code/{code}/arrondissements")
    @Operation(summary = "Obtenir les arrondissements d'un département par code", 
               description = "Retourne la liste de tous les arrondissements d'un département par son code")
    public ResponseEntity<List<com.hrms.dto.ArrondissementDTO>> getArrondissementsByDepartmentCode(
            @PathVariable String code) {
        List<com.hrms.dto.ArrondissementDTO> arrondissements = geographicService.getArrondissementsByDepartmentCode(code);
        return ResponseEntity.ok(arrondissements);
    }

    @GetMapping("/{id}/statistics")
    @Operation(summary = "Obtenir les statistiques d'un département", 
               description = "Retourne les statistiques détaillées d'un département (nombre d'arrondissements, personnel)")
    public ResponseEntity<GeographicStatisticsDTO.DepartmentStatisticsDTO> getDepartmentStatistics(
            @PathVariable Long id) {
        GeographicStatisticsDTO.DepartmentStatisticsDTO stats = statisticsService.getDepartmentStatistics(id);
        return ResponseEntity.ok(stats);
    }
}

