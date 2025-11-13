package com.hrms.controller;

import com.hrms.dto.PreviousPositionDTO;
import com.hrms.dto.PreviousPositionCreateDTO;
import com.hrms.dto.PreviousPositionUpdateDTO;
import com.hrms.service.PreviousPositionService;
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
@RequestMapping("/api/previous-positions")
@RequiredArgsConstructor
@Tag(name = "Previous Positions", description = "API de gestion des postes de travail occupés antérieurement")
@CrossOrigin(origins = "*")
public class PreviousPositionController {

    private final PreviousPositionService positionService;

    @PostMapping
    @Operation(summary = "Créer un poste antérieur")
    public ResponseEntity<PreviousPositionDTO> createPreviousPosition(@Valid @RequestBody PreviousPositionCreateDTO dto) {
        PreviousPositionDTO created = positionService.createPreviousPosition(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un poste antérieur")
    public ResponseEntity<PreviousPositionDTO> updatePreviousPosition(
            @PathVariable Long id,
            @Valid @RequestBody PreviousPositionUpdateDTO dto) {
        PreviousPositionDTO updated = positionService.updatePreviousPosition(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un poste antérieur par ID")
    public ResponseEntity<PreviousPositionDTO> getPreviousPositionById(@PathVariable Long id) {
        PreviousPositionDTO position = positionService.getPreviousPositionById(id);
        return ResponseEntity.ok(position);
    }

    @GetMapping("/personnel/{personnelId}")
    @Operation(summary = "Obtenir les postes antérieurs d'un personnel")
    public ResponseEntity<List<PreviousPositionDTO>> getPreviousPositionsByPersonnel(@PathVariable Long personnelId) {
        List<PreviousPositionDTO> positions = positionService.getPreviousPositionsByPersonnel(personnelId);
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/personnel/{personnelId}/paged")
    @Operation(summary = "Obtenir les postes antérieurs d'un personnel (paginé)")
    public ResponseEntity<Page<PreviousPositionDTO>> getPreviousPositionsByPersonnel(
            @PathVariable Long personnelId,
            @PageableDefault(size = 20, sort = "endDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PreviousPositionDTO> positions = positionService.getPreviousPositionsByPersonnel(personnelId, pageable);
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/personnel/{personnelId}/last-three-years")
    @Operation(summary = "Obtenir les postes antérieurs des 3 dernières années")
    public ResponseEntity<List<PreviousPositionDTO>> getLastThreeYearsPositions(@PathVariable Long personnelId) {
        List<PreviousPositionDTO> positions = positionService.getLastThreeYearsPositions(personnelId);
        return ResponseEntity.ok(positions);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un poste antérieur (soft delete)")
    public ResponseEntity<Void> deletePreviousPosition(@PathVariable Long id) {
        positionService.deletePreviousPosition(id);
        return ResponseEntity.noContent().build();
    }
}

