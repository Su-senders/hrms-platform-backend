package com.hrms.controller;

import com.hrms.dto.TrainingCreateDTO;
import com.hrms.dto.TrainingDTO;
import com.hrms.dto.TrainingUpdateDTO;
import com.hrms.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
@Tag(name = "Formations", description = "API de gestion des formations")
@CrossOrigin(origins = "*")
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle formation")
    public ResponseEntity<TrainingDTO> createTraining(@Valid @RequestBody TrainingCreateDTO dto) {
        TrainingDTO created = trainingService.createTraining(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une formation")
    public ResponseEntity<TrainingDTO> updateTraining(
            @PathVariable Long id,
            @Valid @RequestBody TrainingUpdateDTO dto) {
        TrainingDTO updated = trainingService.updateTraining(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une formation par ID")
    public ResponseEntity<TrainingDTO> getTrainingById(@PathVariable Long id) {
        TrainingDTO training = trainingService.getTrainingById(id);
        return ResponseEntity.ok(training);
    }

    @GetMapping
    @Operation(summary = "Obtenir toutes les formations")
    public ResponseEntity<Page<TrainingDTO>> getAllTrainings(
            @PageableDefault(sort = "title") Pageable pageable) {
        Page<TrainingDTO> trainings = trainingService.getAllTrainings(pageable);
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtenir toutes les formations actives")
    public ResponseEntity<List<TrainingDTO>> getAllActiveTrainings() {
        List<TrainingDTO> trainings = trainingService.getAllActiveTrainings();
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des formations")
    public ResponseEntity<Page<TrainingDTO>> searchTrainings(
            @RequestParam String searchTerm,
            @PageableDefault(sort = "title") Pageable pageable) {
        Page<TrainingDTO> trainings = trainingService.searchTrainings(searchTerm, pageable);
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/advanced-search")
    @Operation(summary = "Recherche avancée de formations")
    public ResponseEntity<Page<TrainingDTO>> advancedSearch(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String trainingField,
            @PageableDefault(sort = "title") Pageable pageable) {
        Page<TrainingDTO> trainings = trainingService.advancedSearch(category, trainingField, pageable);
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Obtenir les formations par catégorie")
    public ResponseEntity<List<TrainingDTO>> getTrainingsByCategory(@PathVariable String category) {
        List<TrainingDTO> trainings = trainingService.getTrainingsByCategory(category);
        return ResponseEntity.ok(trainings);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une formation (soft delete)")
    public ResponseEntity<Void> deleteTraining(@PathVariable Long id) {
        trainingService.deleteTraining(id);
        return ResponseEntity.noContent().build();
    }
}




