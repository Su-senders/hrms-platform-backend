package com.hrms.bootstrap.loader.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Charge les templates organisationnels depuis les fichiers JSON
 */
@Slf4j
@Component
public class TemplateDataLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Charge le template de Gouvernorat
     */
    public TemplateData loadGovernorateTemplate() {
        return loadTemplate("data/templates/governorate-template.json");
    }

    /**
     * Charge le template de Préfecture
     */
    public TemplateData loadPrefectureTemplate() {
        return loadTemplate("data/templates/prefecture-template.json");
    }

    /**
     * Charge le template de Sous-Préfecture
     */
    public TemplateData loadSousPrefectureTemplate() {
        return loadTemplate("data/templates/sous-prefecture-template.json");
    }

    /**
     * Charge un template depuis un fichier JSON
     */
    private TemplateData loadTemplate(String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (!resource.exists()) {
                log.warn("Template file not found: {}", resourcePath);
                return null;
            }

            InputStream inputStream = resource.getInputStream();
            TemplateData template = objectMapper.readValue(inputStream, TemplateData.class);

            log.info("Loaded template: {} from {}", template.getCode(), resourcePath);
            return template;
        } catch (IOException e) {
            log.error("Error loading template from {}", resourcePath, e);
            throw new RuntimeException("Failed to load template: " + resourcePath, e);
        }
    }
}
