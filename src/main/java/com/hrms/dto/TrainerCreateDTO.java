package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerCreateDTO {
    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    private String email;
    private String phone;

    @NotBlank(message = "La spécialisation est obligatoire")
    private String specialization;

    private String organization;

    @NotNull(message = "Le type est obligatoire")
    private String type; // INTERNAL, EXTERNAL

    private Long personnelId; // Si formateur interne

    private String qualifications;
    private Integer experienceYears;
    private BigDecimal hourlyRate;
    private String currency = "XAF";
    private String notes;
    private Boolean active = true;
}




