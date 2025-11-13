package com.hrms.controller;

import com.hrms.dto.PersonnelLeaveDTO;
import com.hrms.dto.PersonnelLeaveCreateDTO;
import com.hrms.dto.PersonnelLeaveUpdateDTO;
import com.hrms.service.PersonnelLeaveService;
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
@RequestMapping("/api/personnel-leaves")
@RequiredArgsConstructor
@Tag(name = "Personnel Leaves", description = "API de gestion des mises en congés")
@CrossOrigin(origins = "*")
public class PersonnelLeaveController {

    private final PersonnelLeaveService leaveService;

    @PostMapping
    @Operation(summary = "Créer une mise en congé")
    public ResponseEntity<PersonnelLeaveDTO> createLeave(@Valid @RequestBody PersonnelLeaveCreateDTO dto) {
        PersonnelLeaveDTO created = leaveService.createLeave(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une mise en congé")
    public ResponseEntity<PersonnelLeaveDTO> updateLeave(
            @PathVariable Long id,
            @Valid @RequestBody PersonnelLeaveUpdateDTO dto) {
        PersonnelLeaveDTO updated = leaveService.updateLeave(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une mise en congé par ID")
    public ResponseEntity<PersonnelLeaveDTO> getLeaveById(@PathVariable Long id) {
        PersonnelLeaveDTO leave = leaveService.getLeaveById(id);
        return ResponseEntity.ok(leave);
    }

    @GetMapping("/personnel/{personnelId}")
    @Operation(summary = "Obtenir les congés d'un personnel")
    public ResponseEntity<List<PersonnelLeaveDTO>> getLeavesByPersonnel(@PathVariable Long personnelId) {
        List<PersonnelLeaveDTO> leaves = leaveService.getLeavesByPersonnel(personnelId);
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/personnel/{personnelId}/paged")
    @Operation(summary = "Obtenir les congés d'un personnel (paginé)")
    public ResponseEntity<Page<PersonnelLeaveDTO>> getLeavesByPersonnel(
            @PathVariable Long personnelId,
            @PageableDefault(size = 20, sort = "effectiveDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PersonnelLeaveDTO> leaves = leaveService.getLeavesByPersonnel(personnelId, pageable);
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/personnel/{personnelId}/reason/{reason}")
    @Operation(summary = "Obtenir les congés d'un personnel par motif")
    public ResponseEntity<List<PersonnelLeaveDTO>> getLeavesByReason(
            @PathVariable Long personnelId,
            @PathVariable String reason) {
        List<PersonnelLeaveDTO> leaves = leaveService.getLeavesByReason(personnelId, reason);
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/in-progress")
    @Operation(summary = "Obtenir les congés en cours")
    public ResponseEntity<List<PersonnelLeaveDTO>> getInProgressLeaves() {
        List<PersonnelLeaveDTO> leaves = leaveService.getInProgressLeaves();
        return ResponseEntity.ok(leaves);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une mise en congé (soft delete)")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id) {
        leaveService.deleteLeave(id);
        return ResponseEntity.noContent().build();
    }
}

