package com.hrms.controller;

import com.hrms.dto.PersonnelDTO;
import com.hrms.dto.PersonnelSearchCriteriaDTO;
import com.hrms.service.PersonnelAdvancedSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la recherche avancée multi-critères de personnels
 */
@RestController
@RequestMapping("/api/personnel/search")
@RequiredArgsConstructor
@Tag(name = "Recherche Avancée Personnel", description = "API de recherche multi-critères de personnels")
@CrossOrigin(origins = "*")
public class PersonnelAdvancedSearchController {

    private final PersonnelAdvancedSearchService searchService;

    /**
     * Recherche avancée multi-critères (paginée)
     */
    @PostMapping("/advanced")
    @Operation(summary = "Recherche avancée avec critères multiples (30+ filtres disponibles)")
    public ResponseEntity<Page<PersonnelDTO>> advancedSearch(
            @Valid @RequestBody PersonnelSearchCriteriaDTO criteria,
            @PageableDefault(size = 20, sort = "matricule", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<PersonnelDTO> results = searchService.advancedSearch(criteria, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Recherche avancée - tous les résultats (pour export)
     */
    @PostMapping("/advanced/all")
    @Operation(summary = "Recherche avancée - tous les résultats (pour export Excel/PDF)")
    public ResponseEntity<List<PersonnelDTO>> advancedSearchAll(
            @Valid @RequestBody PersonnelSearchCriteriaDTO criteria) {
        List<PersonnelDTO> results = searchService.advancedSearchAll(criteria);
        return ResponseEntity.ok(results);
    }

    /**
     * Compter les résultats d'une recherche (sans récupérer les données)
     */
    @PostMapping("/advanced/count")
    @Operation(summary = "Compter le nombre de résultats d'une recherche sans récupérer les données")
    public ResponseEntity<Long> countAdvancedSearch(
            @Valid @RequestBody PersonnelSearchCriteriaDTO criteria) {
        long count = searchService.countAdvancedSearch(criteria);
        return ResponseEntity.ok(count);
    }

    /**
     * Recherche rapide par texte (matricule, nom, prénom)
     */
    @GetMapping("/quick")
    @Operation(summary = "Recherche rapide par matricule, nom ou prénom")
    public ResponseEntity<Page<PersonnelDTO>> quickSearch(
            @RequestParam String searchText,
            @PageableDefault(size = 10, sort = "matricule", direction = Sort.Direction.ASC) Pageable pageable) {

        PersonnelSearchCriteriaDTO criteria = new PersonnelSearchCriteriaDTO();
        criteria.setSearchText(searchText);

        Page<PersonnelDTO> results = searchService.advancedSearch(criteria, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Recherche par structure (avec descendants)
     */
    @GetMapping("/by-structure/{structureId}")
    @Operation(summary = "Rechercher tous les personnels d'une structure (incluant sous-structures)")
    public ResponseEntity<Page<PersonnelDTO>> searchByStructure(
            @PathVariable Long structureId,
            @RequestParam(defaultValue = "false") boolean includeChildren,
            @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable) {

        PersonnelSearchCriteriaDTO criteria = new PersonnelSearchCriteriaDTO();
        criteria.setStructureId(structureId);
        criteria.setIncludeSubStructures(includeChildren);

        Page<PersonnelDTO> results = searchService.advancedSearch(criteria, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Recherche par grade et échelon
     */
    @GetMapping("/by-grade")
    @Operation(summary = "Rechercher les personnels par grade et échelon")
    public ResponseEntity<Page<PersonnelDTO>> searchByGrade(
            @RequestParam String grade,
            @RequestParam(required = false) Integer echelon,
            @PageableDefault(size = 20) Pageable pageable) {

        PersonnelSearchCriteriaDTO criteria = new PersonnelSearchCriteriaDTO();
        criteria.setGrade(grade);
        criteria.setEchelon(echelon);

        Page<PersonnelDTO> results = searchService.advancedSearch(criteria, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Recherche des personnels E.C.I (En Cours d'Intégration)
     */
    @GetMapping("/eci")
    @Operation(summary = "Obtenir tous les personnels E.C.I (sans matricule)")
    public ResponseEntity<Page<PersonnelDTO>> getECIPersonnel(
            @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable) {

        PersonnelSearchCriteriaDTO criteria = new PersonnelSearchCriteriaDTO();
        criteria.setIsECI(true);

        Page<PersonnelDTO> results = searchService.advancedSearch(criteria, pageable);
        return ResponseEntity.ok(results);
    }
}
