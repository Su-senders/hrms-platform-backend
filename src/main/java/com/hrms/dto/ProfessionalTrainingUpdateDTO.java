package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalTrainingUpdateDTO {

    @Size(max = 200, message = "Le domaine de formation ne peut pas dépasser 200 caractères")
    private String trainingField;

    @Size(max = 300, message = "Le formateur ne peut pas dépasser 300 caractères")
    private String trainer;

    @PastOrPresent(message = "La date de début doit être dans le passé ou aujourd'hui")
    private LocalDate startDate;

    private LocalDate endDate;

    private Integer durationDays;

    @Size(max = 300, message = "Le lieu de formation ne peut pas dépasser 300 caractères")
    private String trainingLocation;

    private String description;

    @Size(max = 200, message = "Le certificat ne peut pas dépasser 200 caractères")
    private String certificateObtained;

    private String status;

    private String notes;
}

