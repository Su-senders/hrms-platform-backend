package com.hrms.bootstrap.loader.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Charge les données des structures administratives depuis les fichiers JSON
 */
@Slf4j
@Component
public class AdministrativeStructureLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Charge la structure organisationnelle du MINAT
     */
    public AdministrativeStructureData loadMinatStructure() {
        try {
            ClassPathResource resource = new ClassPathResource("data/administrative/minat/structure.json");
            InputStream inputStream = resource.getInputStream();
            AdministrativeStructureData data = objectMapper.readValue(inputStream, AdministrativeStructureData.class);
            
            log.info("Loaded MINAT administrative structure from JSON");
            return data;
        } catch (IOException e) {
            log.error("Error loading MINAT structure data", e);
            throw new RuntimeException("Failed to load MINAT structure data", e);
        }
    }
}

