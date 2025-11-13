package com.hrms.controller;

import com.hrms.dto.TrainerCreateDTO;
import com.hrms.dto.TrainerDTO;
import com.hrms.dto.TrainerUpdateDTO;
import com.hrms.service.TrainerService;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Tag(name = "Formateurs", description = "API de gestion des formateurs")
@CrossOrigin(origins = "*")
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping
    @Operation(summary = "Créer un nouveau formateur")
    public ResponseEntity<TrainerDTO> createTrainer(@Valid @RequestBody TrainerCreateDTO dto) {
        TrainerDTO created = trainerService.createTrainer(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un formateur")
    public ResponseEntity<TrainerDTO> updateTrainer(
            @PathVariable Long id,
            @Valid @RequestBody TrainerUpdateDTO dto) {
        TrainerDTO updated = trainerService.updateTrainer(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un formateur par ID")
    public ResponseEntity<TrainerDTO> getTrainerById(@PathVariable Long id) {
        TrainerDTO trainer = trainerService.getTrainerById(id);
        return ResponseEntity.ok(trainer);
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les formateurs")
    public ResponseEntity<Page<TrainerDTO>> getAllTrainers(
            @PageableDefault(sort = "lastName") Pageable pageable) {
        Page<TrainerDTO> trainers = trainerService.getAllTrainers(pageable);
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtenir tous les formateurs actifs")
    public ResponseEntity<List<TrainerDTO>> getAllActiveTrainers() {
        List<TrainerDTO> trainers = trainerService.getAllActiveTrainers();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des formateurs")
    public ResponseEntity<Page<TrainerDTO>> searchTrainers(
            @RequestParam String searchTerm,
            @PageableDefault(sort = "lastName") Pageable pageable) {
        Page<TrainerDTO> trainers = trainerService.searchTrainers(searchTerm, pageable);
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/advanced-search")
    @Operation(summary = "Recherche avancée de formateurs")
    public ResponseEntity<Page<TrainerDTO>> advancedSearch(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String specialization,
            @PageableDefault(sort = "lastName") Pageable pageable) {
        Page<TrainerDTO> trainers = trainerService.advancedSearch(type, specialization, pageable);
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Obtenir les formateurs par type")
    public ResponseEntity<List<TrainerDTO>> getTrainersByType(@PathVariable String type) {
        List<TrainerDTO> trainers = trainerService.getTrainersByType(type);
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Vérifier la disponibilité d'un formateur")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        boolean available = trainerService.isTrainerAvailable(id, startDate, endDate);
        return ResponseEntity.ok(available);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un formateur (soft delete)")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }
}




