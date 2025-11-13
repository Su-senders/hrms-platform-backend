package com.hrms.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerUpdateDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String specialization;
    private String organization;
    private String type;
    private Long personnelId;
    private String qualifications;
    private Integer experienceYears;
    private BigDecimal hourlyRate;
    private String currency;
    private String notes;
    private Boolean active;
}




