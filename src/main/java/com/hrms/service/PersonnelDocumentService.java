package com.hrms.service;

import com.hrms.dto.PersonnelDocumentCreateDTO;
import com.hrms.dto.PersonnelDocumentDTO;
import com.hrms.entity.Personnel;
import com.hrms.entity.PersonnelDocument;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.PersonnelDocumentMapper;
import com.hrms.repository.PersonnelDocumentRepository;
import com.hrms.repository.PersonnelRepository;
import com.hrms.util.AuditUtil;
import com.hrms.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PersonnelDocumentService {

    private final PersonnelDocumentRepository documentRepository;
    private final PersonnelRepository personnelRepository;
    private final PersonnelDocumentMapper documentMapper;
    private final AuditUtil auditUtil;
    private final FileUtil fileUtil;

    /**
     * Upload and create document
     */
    public PersonnelDocumentDTO uploadDocument(PersonnelDocumentCreateDTO dto, MultipartFile file) {
        log.info("Uploading document for personnel ID: {}", dto.getPersonnelId());

        // Validate file type
        String[] allowedExtensions = {"pdf", "jpg", "jpeg", "png", "doc", "docx"};
        if (!fileUtil.isValidFileType(file.getOriginalFilename(), allowedExtensions)) {
            throw new IllegalArgumentException("Type de fichier non autorisé");
        }

        // Store file
        String filePath = fileUtil.storeFile(file, "documents/" + dto.getPersonnelId());

        // Update DTO with file info
        dto.setFilePath(filePath);
        dto.setFileName(file.getOriginalFilename());
        dto.setFileSize(file.getSize());
        dto.setFileType(file.getContentType());

        return createDocument(dto);
    }

    /**
     * Create document without file upload (for existing files)
     */
    public PersonnelDocumentDTO createDocument(PersonnelDocumentCreateDTO dto) {
        log.info("Creating document for personnel ID: {}", dto.getPersonnelId());

        PersonnelDocument document = documentMapper.toEntity(dto);

        // Set personnel
        Personnel personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", dto.getPersonnelId()));
        document.setPersonnel(personnel);

        document.setCreatedBy(auditUtil.getCurrentUser());
        document.setCreatedDate(LocalDate.now());

        PersonnelDocument saved = documentRepository.save(document);
        log.info("Document created with ID: {}", saved.getId());

        return documentMapper.toDTO(saved);
    }

    /**
     * Get document by ID
     */
    @Transactional(readOnly = true)
    public PersonnelDocumentDTO getDocumentById(Long id) {
        PersonnelDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
        return documentMapper.toDTO(document);
    }

    /**
     * Get all documents for a personnel
     */
    @Transactional(readOnly = true)
    public List<PersonnelDocumentDTO> getDocumentsByPersonnel(Long personnelId) {
        log.info("Fetching documents for personnel ID: {}", personnelId);
        return documentRepository.findByPersonnelId(personnelId).stream()
                .map(documentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get documents by personnel with pagination
     */
    @Transactional(readOnly = true)
    public Page<PersonnelDocumentDTO> getDocumentsByPersonnel(Long personnelId, Pageable pageable) {
        return documentRepository.findByPersonnelId(personnelId, pageable)
                .map(documentMapper::toDTO);
    }

    /**
     * Get documents by type
     */
    @Transactional(readOnly = true)
    public List<PersonnelDocumentDTO> getDocumentsByType(Long personnelId, String type) {
        PersonnelDocument.DocumentType documentType = PersonnelDocument.DocumentType.valueOf(type.toUpperCase());
        return documentRepository.findByPersonnelIdAndDocumentType(personnelId, documentType).stream()
                .map(documentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get expired documents
     */
    @Transactional(readOnly = true)
    public List<PersonnelDocumentDTO> getExpiredDocuments() {
        log.info("Fetching expired documents");
        return documentRepository.findExpiredDocuments(LocalDate.now()).stream()
                .map(documentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get documents expiring soon (within 30 days)
     */
    @Transactional(readOnly = true)
    public List<PersonnelDocumentDTO> getDocumentsExpiringSoon() {
        log.info("Fetching documents expiring soon");
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysFromNow = now.plusDays(30);
        return documentRepository.findExpiringSoon(now, thirtyDaysFromNow).stream()
                .map(documentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verify document
     */
    public PersonnelDocumentDTO verifyDocument(Long id) {
        log.info("Verifying document with ID: {}", id);

        PersonnelDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        document.verify(auditUtil.getCurrentUser());

        PersonnelDocument verified = documentRepository.save(document);
        log.info("Document verified successfully: {}", id);

        return documentMapper.toDTO(verified);
    }

    /**
     * Create new version of document
     */
    public PersonnelDocumentDTO createNewVersion(Long oldDocumentId, MultipartFile file) {
        log.info("Creating new version for document ID: {}", oldDocumentId);

        PersonnelDocument oldDocument = documentRepository.findById(oldDocumentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", oldDocumentId));

        // Store new file
        String filePath = fileUtil.storeFile(file, "documents/" + oldDocument.getPersonnel().getId());

        // Create new version
        PersonnelDocument newDocument = oldDocument.createNewVersion(filePath, file.getOriginalFilename());
        newDocument.setFileSize(file.getSize());
        newDocument.setFileType(file.getContentType());
        newDocument.setCreatedBy(auditUtil.getCurrentUser());
        newDocument.setCreatedDate(LocalDate.now());

        PersonnelDocument saved = documentRepository.save(newDocument);
        log.info("New document version created with ID: {}", saved.getId());

        return documentMapper.toDTO(saved);
    }

    /**
     * Delete document (soft delete)
     */
    public void deleteDocument(Long id) {
        log.info("Deleting document with ID: {}", id);

        PersonnelDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        // Optionally delete physical file
        // fileUtil.deleteFile(document.getFilePath());

        document.setDeleted(true);
        document.setDeletedAt(LocalDateTime.now());
        document.setDeletedBy(auditUtil.getCurrentUser());

        documentRepository.save(document);
        log.info("Document deleted successfully: {}", id);
    }

    /**
     * Get all documents (paginated)
     */
    @Transactional(readOnly = true)
    public Page<PersonnelDocumentDTO> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable).map(documentMapper::toDTO);
    }

    /**
     * Download document file
     */
    @Transactional(readOnly = true)
    public Resource downloadDocument(Long id) {
        log.info("Downloading document with ID: {}", id);
        
        PersonnelDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
        
        return fileUtil.loadFileAsResource(document.getFilePath());
    }

    /**
     * Get document with file info for download
     */
    @Transactional(readOnly = true)
    public PersonnelDocument getDocumentForDownload(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
    }
}
