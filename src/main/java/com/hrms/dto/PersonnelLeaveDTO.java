package com.hrms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelLeaveDTO {
    private Long id;
    private Long personnelId;
    private String personnelName; // Nom complet du personnel

    private String leaveReason; // Enum: ADMINISTRATIF, ANNUEL, MALADIE, etc.
    private Integer durationDays;
    private String formattedDuration; // Durée formatée
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private String decisionNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate decisionDate;

    private String status; // Enum: REQUESTED, APPROVED, REJECTED, etc.
    private String notes;

    // Calculated fields
    private Boolean isInProgress;
    private Boolean isCompleted;
    private Boolean isUpcoming;

    // Audit fields
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate createdDate;
}

