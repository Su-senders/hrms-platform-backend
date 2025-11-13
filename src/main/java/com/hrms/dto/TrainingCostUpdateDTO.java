package com.hrms.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingCostUpdateDTO {
    private String costType;
    private String description;
    private BigDecimal amount;
    private String currency;
    private LocalDate expenseDate;
    private String invoiceNumber;
    private String paymentStatus;
    private LocalDate paymentDate;
    private String supplier;
    private String notes;
}




