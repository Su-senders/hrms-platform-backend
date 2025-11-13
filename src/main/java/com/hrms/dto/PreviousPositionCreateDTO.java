package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreviousPositionCreateDTO {

    @NotNull(message = "L'ID du personnel est obligatoire")
    private Long personnelId;

    @NotBlank(message = "L'intitulé du poste est obligatoire")
    @Size(max = 300, message = "L'intitulé du poste ne peut pas dépasser 300 caractères")
    private String positionTitle;

    private Long structureId; // Optionnel si structureName est renseigné

    @Size(max = 300, message = "Le nom de la structure ne peut pas dépasser 300 caractères")
    private String structureName; // Optionnel si structureId est renseigné

    @NotNull(message = "La date de début est obligatoire")
    @Past(message = "La date de début doit être dans le passé")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire")
    @PastOrPresent(message = "La date de fin doit être dans le passé ou aujourd'hui")
    private LocalDate endDate;

    private Integer durationMonths; // Optionnel, calculé automatiquement si null

    private Long gradeId; // Optionnel si gradeName est renseigné

    @Size(max = 100, message = "Le nom du grade ne peut pas dépasser 100 caractères")
    private String gradeName; // Optionnel si gradeId est renseigné

    private String responsibilities;

    @Size(max = 200, message = "La raison de fin ne peut pas dépasser 200 caractères")
    private String endReason;

    private String endType; // Enum: MUTATION, PROMOTION, etc.

    private String notes;
}

