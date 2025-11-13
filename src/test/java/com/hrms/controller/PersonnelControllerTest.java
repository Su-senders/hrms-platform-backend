package com.hrms.controller;

import com.hrms.dto.PersonnelCreateDTO;
import com.hrms.dto.PersonnelDTO;
import com.hrms.service.PersonnelImportService;
import com.hrms.service.PersonnelService;
import com.hrms.service.PersonnelTrainingProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour PersonnelController
 */
@WebMvcTest(PersonnelController.class)
@DisplayName("Tests du contrôleur du personnel")
class PersonnelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonnelService personnelService;

    @MockBean
    private PersonnelTrainingProfileService trainingProfileService;

    @MockBean
    private PersonnelImportService personnelImportService;

    @Test
    @DisplayName("Devrait créer un nouveau personnel")
    void shouldCreatePersonnel() throws Exception {
        // Given
        PersonnelCreateDTO createDTO = PersonnelCreateDTO.builder()
                .matricule("MAT001")
                .firstName("Jean")
                .lastName("Dupont")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();

        PersonnelDTO personnelDTO = PersonnelDTO.builder()
                .id(1L)
                .matricule("MAT001")
                .firstName("Jean")
                .lastName("Dupont")
                .build();

        when(personnelService.createPersonnel(any(PersonnelCreateDTO.class))).thenReturn(personnelDTO);

        // When / Then
        mockMvc.perform(post("/api/personnel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.matricule").value("MAT001"))
                .andExpect(jsonPath("$.firstName").value("Jean"));
    }

    @Test
    @DisplayName("Devrait retourner un personnel par ID")
    void shouldGetPersonnelById() throws Exception {
        // Given
        PersonnelDTO personnelDTO = PersonnelDTO.builder()
                .id(1L)
                .matricule("MAT001")
                .firstName("Jean")
                .lastName("Dupont")
                .build();

        when(personnelService.getPersonnelById(1L)).thenReturn(personnelDTO);

        // When / Then
        mockMvc.perform(get("/api/personnel/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.matricule").value("MAT001"));
    }

    @Test
    @DisplayName("Devrait retourner tous les personnels paginés")
    void shouldGetAllPersonnel() throws Exception {
        // Given
        PersonnelDTO personnelDTO = PersonnelDTO.builder()
                .id(1L)
                .matricule("MAT001")
                .firstName("Jean")
                .lastName("Dupont")
                .build();

        Page<PersonnelDTO> page = new PageImpl<>(Arrays.asList(personnelDTO));
        when(personnelService.getAllPersonnel(any(Pageable.class))).thenReturn(page);

        // When / Then
        mockMvc.perform(get("/api/personnel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].matricule").value("MAT001"));
    }

    @Test
    @DisplayName("Devrait retourner un personnel par matricule")
    void shouldGetPersonnelByMatricule() throws Exception {
        // Given
        PersonnelDTO personnelDTO = PersonnelDTO.builder()
                .id(1L)
                .matricule("MAT001")
                .firstName("Jean")
                .lastName("Dupont")
                .build();

        when(personnelService.getPersonnelByMatricule("MAT001")).thenReturn(personnelDTO);

        // When / Then
        mockMvc.perform(get("/api/personnel/matricule/MAT001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("MAT001"));
    }

    @Test
    @DisplayName("Devrait mettre à jour un personnel")
    void shouldUpdatePersonnel() throws Exception {
        // Given
        PersonnelDTO personnelDTO = PersonnelDTO.builder()
                .id(1L)
                .matricule("MAT001")
                .firstName("Jean-Pierre")
                .lastName("Dupont")
                .build();

        when(personnelService.updatePersonnel(anyLong(), any())).thenReturn(personnelDTO);

        // When / Then
        mockMvc.perform(put("/api/personnel/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jean-Pierre\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jean-Pierre"));
    }

    @Test
    @DisplayName("Devrait supprimer un personnel")
    void shouldDeletePersonnel() throws Exception {
        // Given
        doNothing().when(personnelService).deletePersonnel(1L);

        // When / Then
        mockMvc.perform(delete("/api/personnel/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

