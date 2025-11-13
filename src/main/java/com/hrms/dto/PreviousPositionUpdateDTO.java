package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreviousPositionUpdateDTO {

    @Size(max = 300, message = "L'intitulé du poste ne peut pas dépasser 300 caractères")
    private String positionTitle;

    private Long structureId;

    @Size(max = 300, message = "Le nom de la structure ne peut pas dépasser 300 caractères")
    private String structureName;

    @Past(message = "La date de début doit être dans le passé")
    private LocalDate startDate;

    @PastOrPresent(message = "La date de fin doit être dans le passé ou aujourd'hui")
    private LocalDate endDate;

    private Integer durationMonths;

    private Long gradeId;

    @Size(max = 100, message = "Le nom du grade ne peut pas dépasser 100 caractères")
    private String gradeName;

    private String responsibilities;

    @Size(max = 200, message = "La raison de fin ne peut pas dépasser 200 caractères")
    private String endReason;

    private String endType;

    private String notes;
}

