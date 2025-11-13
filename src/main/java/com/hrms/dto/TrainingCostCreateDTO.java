package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingCostCreateDTO {
    @NotNull(message = "La session est obligatoire")
    private Long sessionId;

    @NotNull(message = "Le type de coût est obligatoire")
    private String costType; // TRAINER_FEE, VENUE, etc.

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal amount;

    private String currency = "XAF";

    @NotNull(message = "La date de dépense est obligatoire")
    private LocalDate expenseDate;

    private String invoiceNumber;
    private String paymentStatus = "PENDING";
    private String supplier;
    private String notes;
}




