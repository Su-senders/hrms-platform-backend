package com.hrms.controller;

import com.hrms.dto.PersonnelDocumentCreateDTO;
import com.hrms.dto.PersonnelDocumentDTO;
import com.hrms.entity.PersonnelDocument;
import com.hrms.service.PersonnelDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "API de gestion des documents du personnel")
@CrossOrigin(origins = "*")
public class PersonnelDocumentController {

    private final PersonnelDocumentService documentService;

    @PostMapping("/upload")
    @Operation(summary = "Télécharger un nouveau document")
    public ResponseEntity<PersonnelDocumentDTO> uploadDocument(
            @RequestPart("document") @Valid PersonnelDocumentCreateDTO dto,
            @RequestPart("file") MultipartFile file) {
        PersonnelDocumentDTO created = documentService.uploadDocument(dto, file);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping
    @Operation(summary = "Créer un document (sans fichier)")
    public ResponseEntity<PersonnelDocumentDTO> createDocument(@Valid @RequestBody PersonnelDocumentCreateDTO dto) {
        PersonnelDocumentDTO created = documentService.createDocument(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un document par ID")
    public ResponseEntity<PersonnelDocumentDTO> getDocumentById(@PathVariable Long id) {
        PersonnelDocumentDTO document = documentService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les documents (paginé)")
    public ResponseEntity<Page<PersonnelDocumentDTO>> getAllDocuments(
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PersonnelDocumentDTO> documents = documentService.getAllDocuments(pageable);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/personnel/{personnelId}")
    @Operation(summary = "Obtenir tous les documents d'un personnel")
    public ResponseEntity<List<PersonnelDocumentDTO>> getDocumentsByPersonnel(@PathVariable Long personnelId) {
        List<PersonnelDocumentDTO> documents = documentService.getDocumentsByPersonnel(personnelId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/personnel/{personnelId}/paged")
    @Operation(summary = "Obtenir les documents d'un personnel (paginé)")
    public ResponseEntity<Page<PersonnelDocumentDTO>> getDocumentsByPersonnelPaged(
            @PathVariable Long personnelId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PersonnelDocumentDTO> documents = documentService.getDocumentsByPersonnel(personnelId, pageable);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/personnel/{personnelId}/type/{type}")
    @Operation(summary = "Obtenir les documents d'un personnel par type")
    public ResponseEntity<List<PersonnelDocumentDTO>> getDocumentsByType(
            @PathVariable Long personnelId,
            @PathVariable String type) {
        List<PersonnelDocumentDTO> documents = documentService.getDocumentsByType(personnelId, type);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/expired")
    @Operation(summary = "Obtenir les documents expirés")
    public ResponseEntity<List<PersonnelDocumentDTO>> getExpiredDocuments() {
        List<PersonnelDocumentDTO> documents = documentService.getExpiredDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/expiring-soon")
    @Operation(summary = "Obtenir les documents qui expirent bientôt")
    public ResponseEntity<List<PersonnelDocumentDTO>> getDocumentsExpiringSoon() {
        List<PersonnelDocumentDTO> documents = documentService.getDocumentsExpiringSoon();
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/{id}/verify")
    @Operation(summary = "Vérifier un document")
    public ResponseEntity<PersonnelDocumentDTO> verifyDocument(@PathVariable Long id) {
        PersonnelDocumentDTO verified = documentService.verifyDocument(id);
        return ResponseEntity.ok(verified);
    }

    @PostMapping("/{id}/new-version")
    @Operation(summary = "Créer une nouvelle version d'un document")
    public ResponseEntity<PersonnelDocumentDTO> createNewVersion(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {
        PersonnelDocumentDTO newVersion = documentService.createNewVersion(id, file);
        return new ResponseEntity<>(newVersion, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un document (soft delete)")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Télécharger un document")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        PersonnelDocument document = documentService.getDocumentForDownload(id);
        Resource resource = documentService.downloadDocument(id);
        
        String contentType = document.getFileType() != null 
                ? document.getFileType() 
                : "application/octet-stream";
        
        String filename = document.getFileName() != null 
                ? document.getFileName() 
                : "document_" + id;
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/view")
    @Operation(summary = "Visualiser un document (inline)")
    public ResponseEntity<Resource> viewDocument(@PathVariable Long id) {
        PersonnelDocument document = documentService.getDocumentForDownload(id);
        Resource resource = documentService.downloadDocument(id);
        
        String contentType = document.getFileType() != null 
                ? document.getFileType() 
                : "application/pdf";
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }
}
