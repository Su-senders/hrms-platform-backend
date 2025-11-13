package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalTrainingCreateDTO {

    @NotNull(message = "L'ID du personnel est obligatoire")
    private Long personnelId;

    @NotBlank(message = "Le domaine de formation est obligatoire")
    @Size(max = 200, message = "Le domaine de formation ne peut pas dépasser 200 caractères")
    private String trainingField;

    @NotBlank(message = "Le formateur est obligatoire")
    @Size(max = 300, message = "Le formateur ne peut pas dépasser 300 caractères")
    private String trainer;

    @NotNull(message = "La date de début est obligatoire")
    @PastOrPresent(message = "La date de début doit être dans le passé ou aujourd'hui")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate endDate;

    private Integer durationDays; // Optionnel, calculé automatiquement si null

    @NotBlank(message = "Le lieu de formation est obligatoire")
    @Size(max = 300, message = "Le lieu de formation ne peut pas dépasser 300 caractères")
    private String trainingLocation;

    private String description;

    @Size(max = 200, message = "Le certificat ne peut pas dépasser 200 caractères")
    private String certificateObtained;

    private String status; // Enum: PLANNED, IN_PROGRESS, COMPLETED, CANCELLED, SUSPENDED

    private String notes;
}

