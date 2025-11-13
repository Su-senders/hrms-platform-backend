package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerMovementCreateDTO {

    @NotNull(message = "Le personnel est obligatoire")
    private Long personnelId;

    @NotNull(message = "Le type de mouvement est obligatoire")
    private String movementType;

    @NotNull(message = "La date du mouvement est obligatoire")
    private LocalDate movementDate;

    private String decisionNumber;
    private LocalDate decisionDate;

    // Source
    private Long sourcePositionId;
    private Long sourceStructureId;
    private String sourceGrade;

    // Destination
    private Long destinationPositionId;
    private Long destinationStructureId;
    private String destinationGrade;

    private String reason;
    private String notes;
    private Boolean isOfficialCumul;

    @Min(value = 0, message = "La durée doit être positive")
    private Integer durationMonths;

    private LocalDate endDate;
    private String documentPath;
}
