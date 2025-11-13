package com.hrms.controller;

import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.Position;
import com.hrms.entity.PositionTemplate;
import com.hrms.repository.AdministrativeStructureRepository;
import com.hrms.service.PositionTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/position-templates")
@RequiredArgsConstructor
@Tag(name = "Position Templates", description = "API de gestion des modèles de postes prédéfinis")
@CrossOrigin(origins = "*")
public class PositionTemplateController {

    private final PositionTemplateService templateService;
    private final AdministrativeStructureRepository structureRepository;

    @GetMapping
    @Operation(summary = "Obtenir tous les modèles de postes actifs")
    public ResponseEntity<List<PositionTemplate>> getAllTemplates() {
        List<PositionTemplate> templates = templateService.getAllActiveTemplates();
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/applicable/{structureType}")
    @Operation(summary = "Obtenir les modèles applicables à un type de structure")
    public ResponseEntity<List<PositionTemplate>> getApplicableTemplates(
            @PathVariable AdministrativeStructure.StructureType structureType) {
        List<PositionTemplate> templates = templateService.getApplicableTemplates(structureType);
        return ResponseEntity.ok(templates);
    }

    @PostMapping("/create-positions/{structureId}")
    @Operation(summary = "Créer des postes à partir des modèles pour une structure")
    public ResponseEntity<List<Position>> createPositionsFromTemplates(@PathVariable Long structureId) {
        AdministrativeStructure structure = structureRepository.findById(structureId)
                .orElseThrow(() -> new RuntimeException("Structure not found"));

        List<Position> positions = templateService.createPositionsFromTemplates(structure);
        return ResponseEntity.ok(positions);
    }

    @PostMapping("/create-auto-positions/{structureId}")
    @Operation(summary = "Créer automatiquement les postes obligatoires pour une structure")
    public ResponseEntity<List<Position>> createAutoPositions(@PathVariable Long structureId) {
        AdministrativeStructure structure = structureRepository.findById(structureId)
                .orElseThrow(() -> new RuntimeException("Structure not found"));

        List<Position> positions = templateService.createAutoPositions(structure);
        return ResponseEntity.ok(positions);
    }

    @PostMapping("/bulk-create-positions")
    @Operation(summary = "Créer des postes pour toutes les structures (bulk)")
    public ResponseEntity<String> bulkCreatePositions() {
        List<AdministrativeStructure> structures = structureRepository.findByActiveTrue();
        templateService.createPositionsForAllStructures(structures);
        return ResponseEntity.ok("Positions creation initiated for all structures");
    }
}
