package com.hrms.controller;

import com.hrms.dto.TrainingSessionCreateDTO;
import com.hrms.dto.TrainingSessionDTO;
import com.hrms.dto.TrainingSessionUpdateDTO;
import com.hrms.service.TrainingSessionService;
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
@RequestMapping("/api/training-sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions de Formation", description = "API de gestion des sessions de formation")
@CrossOrigin(origins = "*")
public class TrainingSessionController {

    private final TrainingSessionService sessionService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle session de formation")
    public ResponseEntity<TrainingSessionDTO> createSession(@Valid @RequestBody TrainingSessionCreateDTO dto) {
        TrainingSessionDTO created = sessionService.createSession(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une session de formation")
    public ResponseEntity<TrainingSessionDTO> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody TrainingSessionUpdateDTO dto) {
        TrainingSessionDTO updated = sessionService.updateSession(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une session par ID")
    public ResponseEntity<TrainingSessionDTO> getSessionById(@PathVariable Long id) {
        TrainingSessionDTO session = sessionService.getSessionById(id);
        return ResponseEntity.ok(session);
    }

    @GetMapping
    @Operation(summary = "Obtenir toutes les sessions")
    public ResponseEntity<Page<TrainingSessionDTO>> getAllSessions(
            @PageableDefault(sort = "startDate") Pageable pageable) {
        Page<TrainingSessionDTO> sessions = sessionService.getAllSessions(pageable);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/planned")
    @Operation(summary = "Obtenir les sessions planifiées")
    public ResponseEntity<List<TrainingSessionDTO>> getPlannedSessions() {
        List<TrainingSessionDTO> sessions = sessionService.getPlannedSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/open")
    @Operation(summary = "Obtenir les sessions avec inscriptions ouvertes")
    public ResponseEntity<List<TrainingSessionDTO>> getOpenSessions() {
        List<TrainingSessionDTO> sessions = sessionService.getOpenSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/in-progress")
    @Operation(summary = "Obtenir les sessions en cours")
    public ResponseEntity<List<TrainingSessionDTO>> getInProgressSessions() {
        List<TrainingSessionDTO> sessions = sessionService.getInProgressSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/completed")
    @Operation(summary = "Obtenir les sessions terminées")
    public ResponseEntity<List<TrainingSessionDTO>> getCompletedSessions() {
        List<TrainingSessionDTO> sessions = sessionService.getCompletedSessions();
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/{id}/open-enrollments")
    @Operation(summary = "Ouvrir les inscriptions pour une session")
    public ResponseEntity<TrainingSessionDTO> openEnrollments(@PathVariable Long id) {
        TrainingSessionDTO session = sessionService.openEnrollments(id);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Démarrer une session")
    public ResponseEntity<TrainingSessionDTO> startSession(@PathVariable Long id) {
        TrainingSessionDTO session = sessionService.startSession(id);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Terminer une session")
    public ResponseEntity<TrainingSessionDTO> completeSession(@PathVariable Long id) {
        TrainingSessionDTO session = sessionService.completeSession(id);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler une session")
    public ResponseEntity<TrainingSessionDTO> cancelSession(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        TrainingSessionDTO session = sessionService.cancelSession(id, reason);
        return ResponseEntity.ok(session);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une session (soft delete)")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}




