package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelLeaveUpdateDTO {

    private String leaveReason;

    @Min(value = 1, message = "La durée doit être d'au moins 1 jour")
    private Integer durationDays;

    private LocalDate effectiveDate;

    private LocalDate expiryDate;

    @Size(max = 100, message = "Le numéro de décision ne peut pas dépasser 100 caractères")
    private String decisionNumber;

    private LocalDate decisionDate;

    private String status;

    private String notes;
}

