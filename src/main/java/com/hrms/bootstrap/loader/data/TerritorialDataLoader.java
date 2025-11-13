package com.hrms.bootstrap.loader.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Charge les données territoriales depuis les fichiers JSON
 */
@Slf4j
@Component
public class TerritorialDataLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, RegionData> regionsCache;
    private Map<String, Map<String, List<ArrondissementData>>> arrondissementsCache;

    /**
     * Charge toutes les régions depuis le fichier JSON
     */
    public List<RegionData> loadRegions() {
        try {
            ClassPathResource resource = new ClassPathResource("data/geographic/cameroon/regions.json");
            InputStream inputStream = resource.getInputStream();
            TerritorialData data = objectMapper.readValue(inputStream, TerritorialData.class);
            
            // Construire le cache des régions
            regionsCache = new HashMap<>();
            for (RegionData region : data.getRegions()) {
                regionsCache.put(region.getCode(), region);
            }
            
            log.info("Loaded {} regions from JSON", data.getRegions().size());
            return data.getRegions();
        } catch (IOException e) {
            log.error("Error loading regions data", e);
            throw new RuntimeException("Failed to load regions data", e);
        }
    }

    /**
     * Charge les départements pour une région donnée
     */
    public List<DepartmentData> loadDepartmentsForRegion(String regionCode) {
        if (regionsCache == null) {
            loadRegions();
        }
        
        RegionData region = regionsCache.get(regionCode);
        if (region == null) {
            log.warn("Region {} not found", regionCode);
            return List.of();
        }
        
        return region.getDepartments();
    }

    /**
     * Charge les arrondissements pour un département donné
     * @param regionCode Code de la région (ex: "CE" pour Centre, "ES" pour Est, ou "GOUV-CE")
     * @param departmentName Nom du département
     */
    public List<ArrondissementData> loadArrondissementsForDepartment(String regionCode, String departmentName) {
        if (arrondissementsCache == null) {
            buildArrondissementsCache();
        }
        
        // Extraire le code de région depuis le code complet (GOUV-CE -> CE)
        String code = regionCode;
        if (regionCode.startsWith("GOUV-")) {
            code = regionCode.replace("GOUV-", "");
        }
        
        Map<String, List<ArrondissementData>> regionArrondissements = arrondissementsCache.get(code);
        if (regionArrondissements == null) {
            log.debug("No arrondissements file loaded for region {}", code);
            return List.of();
        }
        
        List<ArrondissementData> arrondissements = regionArrondissements.get(departmentName);
        if (arrondissements == null) {
            log.debug("No arrondissements found for department {} in region {}", departmentName, code);
            return List.of();
        }
        
        return arrondissements;
    }
    
    /**
     * Obtient le nom de fichier pour une région donnée
     */
    private String getRegionFileName(String regionCode) {
        Map<String, String> regionFileMap = new HashMap<>();
        regionFileMap.put("AD", "adamaoua");
        regionFileMap.put("CE", "centre");
        regionFileMap.put("ES", "est");
        regionFileMap.put("EN", "extreme-nord");
        regionFileMap.put("LT", "littoral");
        regionFileMap.put("NO", "nord");
        regionFileMap.put("NW", "nord-ouest");
        regionFileMap.put("OU", "ouest");
        regionFileMap.put("SU", "sud");
        regionFileMap.put("SW", "sud-ouest");
        
        return regionFileMap.getOrDefault(regionCode, regionCode.toLowerCase());
    }

    /**
     * Construit le cache des arrondissements depuis les fichiers JSON par région
     */
    private void buildArrondissementsCache() {
        arrondissementsCache = new HashMap<>();
        
        // Charger les arrondissements pour chaque région
        // Les codes de région correspondent aux suffixes des codes de gouvernorat (GOUV-XX)
        String[] regionCodes = {"AD", "CE", "ES", "EN", "LT", "NO", "NW", "OU", "SU", "SW"};
        
        for (String regionCode : regionCodes) {
            try {
                String fileName = getRegionFileName(regionCode);
                ClassPathResource resource = new ClassPathResource(
                    String.format("data/geographic/cameroon/arrondissements/by-region/%s.json", fileName));
                
                if (resource.exists()) {
                    InputStream inputStream = resource.getInputStream();
                    Map<String, List<ArrondissementData>> departmentArrondissements = 
                        objectMapper.readValue(inputStream, 
                            new TypeReference<Map<String, List<ArrondissementData>>>() {});
                    
                    arrondissementsCache.put(regionCode, departmentArrondissements);
                    log.debug("Loaded arrondissements for region {}: {} departments", 
                        regionCode, departmentArrondissements.size());
                } else {
                    log.debug("No arrondissements file found for region {}", regionCode);
                }
            } catch (IOException e) {
                log.warn("Could not load arrondissements for region {}: {}", regionCode, e.getMessage());
            }
        }
    }

    /**
     * Obtient une région par son code
     */
    public RegionData getRegionByCode(String code) {
        if (regionsCache == null) {
            loadRegions();
        }
        return regionsCache.get(code);
    }
}

