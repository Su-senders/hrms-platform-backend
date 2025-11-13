package com.hrms.service;

import com.hrms.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service de gestion du stockage des documents
 * Gère l'upload, le stockage et la récupération sécurisée des fichiers
 */
@Slf4j
@Service
public class DocumentStorageService {

    private final Path fileStorageLocation;

    // Extensions autorisées pour les documents
    private static final String[] ALLOWED_EXTENSIONS = {
        "pdf", "doc", "docx", "jpg", "jpeg", "png", "xls", "xlsx"
    };

    // Taille maximale des fichiers (10 MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public DocumentStorageService(@Value("${app.file.upload-dir:./uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Répertoire de stockage des fichiers créé: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new BusinessException("Impossible de créer le répertoire de stockage des fichiers", ex);
        }
    }

    /**
     * Upload un document pour un personnel spécifique
     *
     * @param file Fichier à uploader
     * @param personnelMatricule Matricule du personnel
     * @param documentType Type de document (ex: "decision", "certificate", "contract")
     * @return Chemin relatif du fichier stocké
     */
    public String storePersonnelDocument(MultipartFile file, String personnelMatricule, String documentType) {
        // Validation du fichier
        validateFile(file);

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);

        // Créer un nom de fichier unique et sécurisé
        String fileName = generateUniqueFileName(personnelMatricule, documentType, fileExtension);

        try {
            // Créer le sous-répertoire pour le personnel si nécessaire
            Path personnelDir = this.fileStorageLocation.resolve(personnelMatricule);
            Files.createDirectories(personnelDir);

            // Créer le sous-répertoire pour le type de document si nécessaire
            Path documentTypeDir = personnelDir.resolve(documentType);
            Files.createDirectories(documentTypeDir);

            // Copier le fichier vers le répertoire cible
            Path targetLocation = documentTypeDir.resolve(fileName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // Retourner le chemin relatif: matricule/type/fichier.pdf
            String relativePath = String.format("%s/%s/%s", personnelMatricule, documentType, fileName);

            log.info("Document stocké avec succès: {}", relativePath);
            return relativePath;

        } catch (IOException ex) {
            throw new BusinessException("Impossible de stocker le fichier " + fileName, ex);
        }
    }

    /**
     * Upload un document de décision de mouvement
     */
    public String storeMovementDecisionDocument(MultipartFile file, Long movementId, String personnelMatricule) {
        String documentType = String.format("movements/decision_%d", movementId);
        return storePersonnelDocument(file, personnelMatricule, documentType);
    }

    /**
     * Récupère un document
     */
    public Path loadDocument(String relativePath) {
        try {
            Path filePath = this.fileStorageLocation.resolve(relativePath).normalize();

            // Vérifier que le fichier existe
            if (!Files.exists(filePath)) {
                throw new BusinessException("Fichier non trouvé: " + relativePath);
            }

            // Vérifier que le fichier est bien dans le répertoire de stockage (sécurité)
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new BusinessException("Accès non autorisé au fichier");
            }

            return filePath;

        } catch (Exception ex) {
            throw new BusinessException("Impossible de charger le fichier: " + relativePath, ex);
        }
    }

    /**
     * Supprime un document
     */
    public void deleteDocument(String relativePath) {
        try {
            Path filePath = this.fileStorageLocation.resolve(relativePath).normalize();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Document supprimé: {}", relativePath);
            }

        } catch (IOException ex) {
            log.error("Erreur lors de la suppression du fichier: {}", relativePath, ex);
            throw new BusinessException("Impossible de supprimer le fichier: " + relativePath, ex);
        }
    }

    /**
     * Valide un fichier uploadé
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Le fichier est vide");
        }

        // Vérifier la taille
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(
                String.format("Le fichier est trop volumineux (max: %d MB)", MAX_FILE_SIZE / (1024 * 1024))
            );
        }

        // Vérifier l'extension
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getFileExtension(originalFileName);

        boolean isAllowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new BusinessException(
                String.format("Type de fichier non autorisé. Extensions autorisées: %s",
                    String.join(", ", ALLOWED_EXTENSIONS))
            );
        }

        // Vérifier les caractères invalides dans le nom
        if (originalFileName.contains("..")) {
            throw new BusinessException("Le nom de fichier contient une séquence de chemin invalide");
        }
    }

    /**
     * Obtient l'extension d'un fichier
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * Génère un nom de fichier unique et sécurisé
     */
    private String generateUniqueFileName(String personnelMatricule, String documentType, String extension) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        return String.format("%s_%s_%s_%s.%s",
            personnelMatricule,
            documentType.replaceAll("[^a-zA-Z0-9]", "_"),
            dateStr,
            uuid,
            extension
        );
    }

    /**
     * Liste tous les documents d'un personnel
     */
    public java.util.List<String> listPersonnelDocuments(String personnelMatricule) {
        try {
            Path personnelDir = this.fileStorageLocation.resolve(personnelMatricule);

            if (!Files.exists(personnelDir)) {
                return java.util.Collections.emptyList();
            }

            return Files.walk(personnelDir)
                .filter(Files::isRegularFile)
                .map(path -> this.fileStorageLocation.relativize(path).toString())
                .collect(java.util.stream.Collectors.toList());

        } catch (IOException ex) {
            log.error("Erreur lors de la liste des documents pour: {}", personnelMatricule, ex);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Obtient la taille d'un fichier en octets
     */
    public long getFileSize(String relativePath) {
        try {
            Path filePath = loadDocument(relativePath);
            return Files.size(filePath);
        } catch (IOException ex) {
            return 0;
        }
    }

    /**
     * Vérifie si un fichier existe
     */
    public boolean fileExists(String relativePath) {
        try {
            Path filePath = this.fileStorageLocation.resolve(relativePath).normalize();
            return Files.exists(filePath);
        } catch (Exception ex) {
            return false;
        }
    }
}
