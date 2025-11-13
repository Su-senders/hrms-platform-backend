package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingSessionCreateDTO {
    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotNull(message = "La formation est obligatoire")
    private Long trainingId;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate endDate;

    @NotBlank(message = "Le lieu est obligatoire")
    private String location;

    @NotNull(message = "Le nombre maximum de participants est obligatoire")
    @Positive(message = "Le nombre maximum de participants doit être positif")
    private Integer maxParticipants;

    private Integer minParticipants;

    @NotNull(message = "Le formateur est obligatoire")
    private Long trainerId;

    private List<Long> coTrainerIds;

    private Long organizingStructureId;

    private BigDecimal budget;

    private String description;
    private String notes;

    private LocalDate enrollmentStartDate;
    private LocalDate enrollmentEndDate;
}




