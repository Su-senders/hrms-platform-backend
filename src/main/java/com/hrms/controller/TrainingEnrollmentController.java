package com.hrms.controller;

import com.hrms.dto.TrainingEnrollmentCreateDTO;
import com.hrms.dto.TrainingEnrollmentDTO;
import com.hrms.dto.TrainingEnrollmentUpdateDTO;
import com.hrms.service.TrainingEnrollmentService;
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
@RequestMapping("/api/training-enrollments")
@RequiredArgsConstructor
@Tag(name = "Inscriptions aux Formations", description = "API de gestion des inscriptions aux formations")
@CrossOrigin(origins = "*")
public class TrainingEnrollmentController {

    private final TrainingEnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle inscription")
    public ResponseEntity<TrainingEnrollmentDTO> createEnrollment(@Valid @RequestBody TrainingEnrollmentCreateDTO dto) {
        TrainingEnrollmentDTO created = enrollmentService.createEnrollment(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une inscription")
    public ResponseEntity<TrainingEnrollmentDTO> updateEnrollment(
            @PathVariable Long id,
            @Valid @RequestBody TrainingEnrollmentUpdateDTO dto) {
        TrainingEnrollmentDTO updated = enrollmentService.updateEnrollment(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une inscription par ID")
    public ResponseEntity<TrainingEnrollmentDTO> getEnrollmentById(@PathVariable Long id) {
        TrainingEnrollmentDTO enrollment = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(enrollment);
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Obtenir les inscriptions d'une session")
    public ResponseEntity<List<TrainingEnrollmentDTO>> getEnrollmentsBySession(@PathVariable Long sessionId) {
        List<TrainingEnrollmentDTO> enrollments = enrollmentService.getEnrollmentsBySession(sessionId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/session/{sessionId}/paged")
    @Operation(summary = "Obtenir les inscriptions d'une session (paginé)")
    public ResponseEntity<Page<TrainingEnrollmentDTO>> getEnrollmentsBySession(
            @PathVariable Long sessionId,
            @PageableDefault(sort = "enrollmentDate") Pageable pageable) {
        Page<TrainingEnrollmentDTO> enrollments = enrollmentService.getEnrollmentsBySession(sessionId, pageable);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/personnel/{personnelId}")
    @Operation(summary = "Obtenir les formations d'un personnel")
    public ResponseEntity<List<TrainingEnrollmentDTO>> getEnrollmentsByPersonnel(@PathVariable Long personnelId) {
        List<TrainingEnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByPersonnel(personnelId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/personnel/{personnelId}/paged")
    @Operation(summary = "Obtenir les formations d'un personnel (paginé)")
    public ResponseEntity<Page<TrainingEnrollmentDTO>> getEnrollmentsByPersonnel(
            @PathVariable Long personnelId,
            @PageableDefault(sort = "enrollmentDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<TrainingEnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByPersonnel(personnelId, pageable);
        return ResponseEntity.ok(enrollments);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approuver une inscription")
    public ResponseEntity<TrainingEnrollmentDTO> approveEnrollment(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "System") String approver) {
        TrainingEnrollmentDTO enrollment = enrollmentService.approveEnrollment(id, approver);
        return ResponseEntity.ok(enrollment);
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Rejeter une inscription")
    public ResponseEntity<TrainingEnrollmentDTO> rejectEnrollment(
            @PathVariable Long id,
            @RequestParam String reason) {
        TrainingEnrollmentDTO enrollment = enrollmentService.rejectEnrollment(id, reason);
        return ResponseEntity.ok(enrollment);
    }

    @PostMapping("/{id}/mark-attended")
    @Operation(summary = "Marquer un participant comme ayant assisté")
    public ResponseEntity<TrainingEnrollmentDTO> markAsAttended(@PathVariable Long id) {
        TrainingEnrollmentDTO enrollment = enrollmentService.markAsAttended(id);
        return ResponseEntity.ok(enrollment);
    }

    @PostMapping("/{id}/mark-absent")
    @Operation(summary = "Marquer un participant comme absent")
    public ResponseEntity<TrainingEnrollmentDTO> markAsAbsent(@PathVariable Long id) {
        TrainingEnrollmentDTO enrollment = enrollmentService.markAsAbsent(id);
        return ResponseEntity.ok(enrollment);
    }

    @PostMapping("/{id}/issue-certificate")
    @Operation(summary = "Délivrer un certificat")
    public ResponseEntity<TrainingEnrollmentDTO> issueCertificate(
            @PathVariable Long id,
            @RequestParam(required = false) String certificateNumber) {
        TrainingEnrollmentDTO enrollment = enrollmentService.issueCertificate(id, certificateNumber);
        return ResponseEntity.ok(enrollment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une inscription (soft delete)")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}




