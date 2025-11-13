package com.hrms.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingCostDTO {
    private Long id;
    private Long sessionId;
    private String sessionTitle;
    private String costType; // TRAINER_FEE, VENUE, etc.
    private String description;
    private BigDecimal amount;
    private String currency;
    private LocalDate expenseDate;
    private String invoiceNumber;
    private String paymentStatus; // PENDING, PAID, CANCELLED
    private LocalDate paymentDate;
    private String supplier;
    private String notes;
}




