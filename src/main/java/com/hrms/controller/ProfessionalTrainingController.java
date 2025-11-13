package com.hrms.controller;

import com.hrms.dto.ProfessionalTrainingDTO;
import com.hrms.dto.ProfessionalTrainingCreateDTO;
import com.hrms.dto.ProfessionalTrainingUpdateDTO;
import com.hrms.service.ProfessionalTrainingService;
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

@RestController
@RequestMapping("/api/professional-trainings")
@RequiredArgsConstructor
@Tag(name = "Professional Trainings", description = "API de gestion des stages professionnels")
@CrossOrigin(origins = "*")
public class ProfessionalTrainingController {

    private final ProfessionalTrainingService trainingService;

    @PostMapping
    @Operation(summary = "Créer un stage professionnel")
    public ResponseEntity<ProfessionalTrainingDTO> createTraining(@Valid @RequestBody ProfessionalTrainingCreateDTO dto) {
        ProfessionalTrainingDTO created = trainingService.createTraining(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un stage professionnel")
    public ResponseEntity<ProfessionalTrainingDTO> updateTraining(
            @PathVariable Long id,
            @Valid @RequestBody ProfessionalTrainingUpdateDTO dto) {
        ProfessionalTrainingDTO updated = trainingService.updateTraining(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un stage par ID")
    public ResponseEntity<ProfessionalTrainingDTO> getTrainingById(@PathVariable Long id) {
        ProfessionalTrainingDTO training = trainingService.getTrainingById(id);
        return ResponseEntity.ok(training);
    }

    @GetMapping("/personnel/{personnelId}")
    @Operation(summary = "Obtenir les stages d'un personnel")
    public ResponseEntity<List<ProfessionalTrainingDTO>> getTrainingsByPersonnel(@PathVariable Long personnelId) {
        List<ProfessionalTrainingDTO> trainings = trainingService.getTrainingsByPersonnel(personnelId);
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/personnel/{personnelId}/paged")
    @Operation(summary = "Obtenir les stages d'un personnel (paginé)")
    public ResponseEntity<Page<ProfessionalTrainingDTO>> getTrainingsByPersonnel(
            @PathVariable Long personnelId,
            @PageableDefault(size = 20, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProfessionalTrainingDTO> trainings = trainingService.getTrainingsByPersonnel(personnelId, pageable);
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/in-progress")
    @Operation(summary = "Obtenir les stages en cours")
    public ResponseEntity<List<ProfessionalTrainingDTO>> getInProgressTrainings() {
        List<ProfessionalTrainingDTO> trainings = trainingService.getInProgressTrainings();
        return ResponseEntity.ok(trainings);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un stage (soft delete)")
    public ResponseEntity<Void> deleteTraining(@PathVariable Long id) {
        trainingService.deleteTraining(id);
        return ResponseEntity.noContent().build();
    }
}

