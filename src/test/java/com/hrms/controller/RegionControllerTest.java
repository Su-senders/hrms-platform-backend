package com.hrms.controller;

import com.hrms.dto.DepartmentDTO;
import com.hrms.dto.GeographicStatisticsDTO;
import com.hrms.dto.RegionDTO;
import com.hrms.service.GeographicService;
import com.hrms.service.GeographicStatisticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour RegionController
 */
@WebMvcTest(RegionController.class)
@DisplayName("Tests du contrôleur des régions")
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeographicService geographicService;

    @MockBean
    private GeographicStatisticsService statisticsService;

    @Test
    @DisplayName("Devrait retourner toutes les régions")
    void shouldGetAllRegions() throws Exception {
        // Given
        RegionDTO region = RegionDTO.builder()
                .id(1L)
                .code("CE")
                .name("Centre")
                .chefLieu("Yaoundé")
                .active(true)
                .build();

        when(geographicService.getAllRegions()).thenReturn(Arrays.asList(region));

        // When / Then
        mockMvc.perform(get("/api/geography/regions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].code").value("CE"))
                .andExpect(jsonPath("$[0].name").value("Centre"));
    }

    @Test
    @DisplayName("Devrait retourner une région par ID")
    void shouldGetRegionById() throws Exception {
        // Given
        RegionDTO region = RegionDTO.builder()
                .id(1L)
                .code("CE")
                .name("Centre")
                .chefLieu("Yaoundé")
                .build();

        when(geographicService.getRegionById(1L)).thenReturn(region);

        // When / Then
        mockMvc.perform(get("/api/geography/regions/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("CE"))
                .andExpect(jsonPath("$.name").value("Centre"));
    }

    @Test
    @DisplayName("Devrait retourner une région par code")
    void shouldGetRegionByCode() throws Exception {
        // Given
        RegionDTO region = RegionDTO.builder()
                .id(1L)
                .code("CE")
                .name("Centre")
                .build();

        when(geographicService.getRegionByCode("CE")).thenReturn(region);

        // When / Then
        mockMvc.perform(get("/api/geography/regions/code/CE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CE"));
    }

    @Test
    @DisplayName("Devrait rechercher des régions")
    void shouldSearchRegions() throws Exception {
        // Given
        RegionDTO region = RegionDTO.builder()
                .id(1L)
                .code("CE")
                .name("Centre")
                .build();

        when(geographicService.searchRegions("Centre")).thenReturn(Arrays.asList(region));

        // When / Then
        mockMvc.perform(get("/api/geography/regions/search")
                        .param("searchTerm", "Centre")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Centre"));
    }

    @Test
    @DisplayName("Devrait retourner les départements d'une région")
    void shouldGetDepartmentsByRegion() throws Exception {
        // Given
        DepartmentDTO department = DepartmentDTO.builder()
                .id(1L)
                .code("CE-MFOU")
                .name("Mfoundi")
                .build();

        when(geographicService.getDepartmentsByRegion(1L)).thenReturn(Arrays.asList(department));

        // When / Then
        mockMvc.perform(get("/api/geography/regions/1/departments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].code").value("CE-MFOU"));
    }

    @Test
    @DisplayName("Devrait retourner les statistiques d'une région")
    void shouldGetRegionStatistics() throws Exception {
        // Given
        GeographicStatisticsDTO.RegionStatisticsDTO stats = GeographicStatisticsDTO.RegionStatisticsDTO.builder()
                .regionId(1L)
                .regionName("Centre")
                .regionCode("CE")
                .nombreDepartements(10L)
                .nombreArrondissements(78L)
                .nombrePersonnel(1250L)
                .build();

        when(statisticsService.getRegionStatistics(1L)).thenReturn(stats);

        // When / Then
        mockMvc.perform(get("/api/geography/regions/1/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regionId").value(1))
                .andExpect(jsonPath("$.regionName").value("Centre"))
                .andExpect(jsonPath("$.nombreDepartements").value(10))
                .andExpect(jsonPath("$.nombrePersonnel").value(1250));
    }
}

