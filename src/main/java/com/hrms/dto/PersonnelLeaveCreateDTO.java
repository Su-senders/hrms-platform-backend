package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelLeaveCreateDTO {

    @NotNull(message = "L'ID du personnel est obligatoire")
    private Long personnelId;

    @NotNull(message = "Le motif de congé est obligatoire")
    private String leaveReason; // Enum: ADMINISTRATIF, ANNUEL, MALADIE, etc.

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 1, message = "La durée doit être d'au moins 1 jour")
    private Integer durationDays;

    @NotNull(message = "La date d'effet est obligatoire")
    private LocalDate effectiveDate;

    @NotNull(message = "La date d'expiration est obligatoire")
    private LocalDate expiryDate;

    @Size(max = 100, message = "Le numéro de décision ne peut pas dépasser 100 caractères")
    private String decisionNumber;

    private LocalDate decisionDate;

    private String status; // Enum: REQUESTED, APPROVED, REJECTED, etc.

    private String notes;
}

