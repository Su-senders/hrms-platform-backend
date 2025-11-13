package com.hrms.bootstrap.loader.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Charge les données de référence des corps de métiers et grades depuis JSON
 */
@Slf4j
@Component
public class CorpsMetiersDataLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Charge tous les corps de métiers avec leurs grades
     */
    public List<CorpsMetierData> loadCorpsMetiers() {
        return loadCorpsMetiersReference("data/reference/corps-metiers.json");
    }

    /**
     * Charge le fichier de référence des corps de métiers
     */
    private List<CorpsMetierData> loadCorpsMetiersReference(String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (!resource.exists()) {
                log.warn("Corps metiers reference file not found: {}", resourcePath);
                return List.of();
            }

            InputStream inputStream = resource.getInputStream();
            CorpsMetiersReferenceData data = objectMapper.readValue(inputStream, CorpsMetiersReferenceData.class);

            log.info("Loaded {} corps de métiers from {}",
                data.getCorpsMetiers().size(), resourcePath);

            // Compter le total de grades
            int totalGrades = data.getCorpsMetiers().stream()
                .mapToInt(c -> c.getGrades() != null ? c.getGrades().size() : 0)
                .sum();
            log.info("Total grades loaded: {}", totalGrades);

            return data.getCorpsMetiers();
        } catch (IOException e) {
            log.error("Error loading corps metiers reference from {}", resourcePath, e);
            throw new RuntimeException("Failed to load corps metiers reference: " + resourcePath, e);
        }
    }

    /**
     * Trouve un corps de métier par son code
     */
    public CorpsMetierData findCorpsMetierByCode(String code) {
        List<CorpsMetierData> corpsMetiers = loadCorpsMetiers();
        return corpsMetiers.stream()
            .filter(c -> c.getCode().equals(code))
            .findFirst()
            .orElse(null);
    }

    /**
     * Compte le nombre total de grades
     */
    public int countTotalGrades() {
        List<CorpsMetierData> corpsMetiers = loadCorpsMetiers();
        return corpsMetiers.stream()
            .mapToInt(c -> c.getGrades() != null ? c.getGrades().size() : 0)
            .sum();
    }
}
