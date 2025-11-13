package com.hrms.controller;

import com.hrms.dto.reports.*;
import com.hrms.service.TrainingReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur pour les rapports et statistiques de formation
 */
@RestController
@RequestMapping("/api/training-reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Training Reports", description = "Rapports et statistiques sur les formations")
public class TrainingReportController {

    private final TrainingReportService reportService;

    @GetMapping("/personnel/{personnelId}/statistics")
    @Operation(summary = "Statistiques de formation d'un personnel pour une année donnée")
    public ResponseEntity<PersonnelTrainingStatisticsDTO> getPersonnelStatistics(
            @PathVariable Long personnelId,
            @RequestParam(defaultValue = "2024") int year) {
        return ResponseEntity.ok(reportService.getPersonnelStatistics(personnelId, year));
    }

    @GetMapping("/structures/{structureId}/statistics")
    @Operation(summary = "Statistiques de formation d'une structure pour une année donnée")
    public ResponseEntity<StructureTrainingStatisticsDTO> getStructureStatistics(
            @PathVariable Long structureId,
            @RequestParam(defaultValue = "2024") int year) {
        return ResponseEntity.ok(reportService.getStructureStatistics(structureId, year));
    }

    @GetMapping("/global/statistics")
    @Operation(summary = "Statistiques globales de formation pour une année donnée")
    public ResponseEntity<GlobalTrainingStatisticsDTO> getGlobalStatistics(
            @RequestParam(defaultValue = "2024") int year) {
        return ResponseEntity.ok(reportService.getGlobalStatistics(year));
    }

    @GetMapping("/trained-personnel")
    @Operation(summary = "Liste du personnel formé par domaine et période")
    public ResponseEntity<List<TrainedPersonnelDTO>> getTrainedPersonnel(
            @RequestParam String trainingField,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(
            reportService.getTrainedPersonnelByFieldAndPeriod(trainingField, startDate, endDate)
        );
    }

    @GetMapping("/sessions/{sessionId}/participants")
    @Operation(summary = "Liste des participants d'une session de formation")
    public ResponseEntity<List<ParticipantDTO>> getSessionParticipants(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(reportService.getSessionParticipants(sessionId));
    }

    @GetMapping("/top-trainers")
    @Operation(summary = "Top des formateurs les plus actifs")
    public ResponseEntity<List<TrainerActivityDTO>> getTopTrainers(
            @RequestParam(defaultValue = "2024") int year,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reportService.getTopTrainers(year, limit));
    }
}
