package com.hrms.util;

import com.hrms.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Component
public class FileUtil {

    private static final String UPLOAD_DIR = "./uploads/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Store a file and return the file path
     */
    public String storeFile(MultipartFile file, String subDirectory) {
        if (file.isEmpty()) {
            throw new BusinessException("Le fichier est vide");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("La taille du fichier dépasse la limite de 10MB");
        }

        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR + subDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID().toString() + extension;

            // Copy file to target location
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", targetLocation);
            return subDirectory + "/" + filename;

        } catch (IOException ex) {
            log.error("Failed to store file", ex);
            throw new BusinessException("Impossible de stocker le fichier", ex);
        }
    }

    /**
     * Delete a file
     */
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(UPLOAD_DIR + filePath);
            Files.deleteIfExists(path);
            log.info("File deleted successfully: {}", filePath);
        } catch (IOException ex) {
            log.error("Failed to delete file: {}", filePath, ex);
            throw new BusinessException("Impossible de supprimer le fichier", ex);
        }
    }

    /**
     * Get file extension
     */
    public String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return "";
    }

    /**
     * Validate file type
     */
    public boolean isValidFileType(String filename, String[] allowedExtensions) {
        String extension = getFileExtension(filename).toLowerCase();
        for (String allowed : allowedExtensions) {
            if (extension.equals(allowed.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Load file as Resource for download/viewing
     */
    public Resource loadFileAsResource(String filePath) {
        try {
            Path path = Paths.get(UPLOAD_DIR + filePath);
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                log.info("File loaded successfully: {}", filePath);
                return resource;
            } else {
                log.error("File not found or not readable: {}", filePath);
                throw new BusinessException("Fichier non trouvé ou non lisible: " + filePath);
            }
        } catch (MalformedURLException ex) {
            log.error("Invalid file path: {}", filePath, ex);
            throw new BusinessException("Chemin de fichier invalide: " + filePath, ex);
        }
    }
}
