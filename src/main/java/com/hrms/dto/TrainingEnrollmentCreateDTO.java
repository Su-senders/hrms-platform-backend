package com.hrms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingEnrollmentCreateDTO {
    @NotNull(message = "La session est obligatoire")
    private Long sessionId;

    @NotNull(message = "Le personnel est obligatoire")
    private Long personnelId;

    private String notes;
}




