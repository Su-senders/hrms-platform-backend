package com.hrms.controller;

import com.hrms.dto.*;
import com.hrms.service.PersonnelService;
import com.hrms.service.PersonnelTrainingProfileService;
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
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
@Tag(name = "Personnel", description = "API de gestion du personnel")
@CrossOrigin(origins = "*")
public class PersonnelController {

    private final PersonnelService personnelService;
    private final PersonnelTrainingProfileService trainingProfileService;
    private final com.hrms.service.PersonnelImportService personnelImportService;

    @PostMapping
    @Operation(summary = "Créer un nouveau personnel")
    public ResponseEntity<PersonnelDTO> createPersonnel(@Valid @RequestBody PersonnelCreateDTO dto) {
        PersonnelDTO created = personnelService.createPersonnel(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un personnel")
    public ResponseEntity<PersonnelDTO> updatePersonnel(
            @PathVariable Long id,
            @Valid @RequestBody PersonnelUpdateDTO dto) {
        PersonnelDTO updated = personnelService.updatePersonnel(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un personnel par ID")
    public ResponseEntity<PersonnelDTO> getPersonnelById(@PathVariable Long id) {
        PersonnelDTO personnel = personnelService.getPersonnelById(id);
        return ResponseEntity.ok(personnel);
    }

    @GetMapping("/matricule/{matricule}")
    @Operation(summary = "Obtenir un personnel par matricule")
    public ResponseEntity<PersonnelDTO> getPersonnelByMatricule(@PathVariable String matricule) {
        PersonnelDTO personnel = personnelService.getPersonnelByMatricule(matricule);
        return ResponseEntity.ok(personnel);
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les personnels (paginé)")
    public ResponseEntity<Page<PersonnelDTO>> getAllPersonnel(
            @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<PersonnelDTO> personnelPage = personnelService.getAllPersonnel(pageable);
        return ResponseEntity.ok(personnelPage);
    }

    @PostMapping("/search")
    @Operation(summary = "Rechercher des personnels avec critères multiples")
    public ResponseEntity<Page<PersonnelDTO>> searchPersonnel(
            @RequestBody PersonnelSearchDTO searchDTO,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        Page<PersonnelDTO> results = personnelService.searchPersonnel(searchDTO, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/situation/{situation}")
    @Operation(summary = "Obtenir les personnels par situation")
    public ResponseEntity<Page<PersonnelDTO>> getPersonnelBySituation(
            @PathVariable String situation,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PersonnelDTO> results = personnelService.getPersonnelBySituation(situation, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/grade/{grade}")
    @Operation(summary = "Obtenir les personnels par grade")
    public ResponseEntity<Page<PersonnelDTO>> getPersonnelByGrade(
            @PathVariable String grade,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PersonnelDTO> results = personnelService.getPersonnelByGrade(grade, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/structure/{structureId}")
    @Operation(summary = "Obtenir les personnels par structure")
    public ResponseEntity<Page<PersonnelDTO>> getPersonnelByStructure(
            @PathVariable Long structureId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PersonnelDTO> results = personnelService.getPersonnelByStructure(structureId, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/retirable/current-year")
    @Operation(summary = "Obtenir les personnels retraitables cette année")
    public ResponseEntity<List<PersonnelDTO>> getRetirableThisYear() {
        List<PersonnelDTO> results = personnelService.getRetirableThisYear();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/retirable/next-year")
    @Operation(summary = "Obtenir les personnels retraitables l'année prochaine")
    public ResponseEntity<List<PersonnelDTO>> getRetirableNextYear() {
        List<PersonnelDTO> results = personnelService.getRetirableNextYear();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Obtenir les statistiques du personnel")
    public ResponseEntity<Map<String, Object>> getPersonnelStatistics() {
        Map<String, Object> stats = personnelService.getPersonnelStatistics();
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un personnel (soft delete)")
    public ResponseEntity<Void> deletePersonnel(@PathVariable Long id) {
        personnelService.deletePersonnel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/training-history")
    @Operation(summary = "Obtenir l'historique complet des formations d'un personnel",
               description = "Retourne l'historique unifié combinant ProfessionalTraining (formations anciennes) " +
                             "et TrainingEnrollment (sessions modernes) avec statistiques globales")
    public ResponseEntity<PersonnelTrainingHistoryDTO> getTrainingHistory(@PathVariable Long id) {
        PersonnelTrainingHistoryDTO history = trainingProfileService.getPersonnelTrainingHistory(id);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/can-assign/{personnelId}/to-position/{positionId}")
    @Operation(summary = "Vérifier si un personnel peut être affecté à un poste")
    public ResponseEntity<Boolean> canAssignToPosition(
            @PathVariable Long personnelId,
            @PathVariable Long positionId) {
        boolean canAssign = personnelService.canAssignToPosition(personnelId, positionId);
        return ResponseEntity.ok(canAssign);
    }

    // ==================== IMPORTATION EN MASSE ====================

    @PostMapping("/import/excel")
    @Operation(summary = "Importer des personnels depuis un fichier Excel")
    public ResponseEntity<PersonnelImportResultDTO> importFromExcel(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "validationOnly", defaultValue = "false") boolean validationOnly) {
        PersonnelImportResultDTO result = personnelImportService.importFromExcel(file, validationOnly);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/import/csv")
    @Operation(summary = "Importer des personnels depuis un fichier CSV")
    public ResponseEntity<PersonnelImportResultDTO> importFromCSV(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "validationOnly", defaultValue = "false") boolean validationOnly) {
        PersonnelImportResultDTO result = personnelImportService.importFromCSV(file, validationOnly);
        return ResponseEntity.ok(result);
    }
}
