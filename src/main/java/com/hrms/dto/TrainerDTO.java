package com.hrms.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerDTO {
    private Long id;
    private String code;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private String organization;
    private String type; // INTERNAL, EXTERNAL
    private Long personnelId;
    private String personnelName;
    private String qualifications;
    private Integer experienceYears;
    private BigDecimal hourlyRate;
    private String currency;
    private String notes;
    private Boolean active;
}




