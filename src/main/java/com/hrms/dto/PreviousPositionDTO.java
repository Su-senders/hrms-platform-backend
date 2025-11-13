package com.hrms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreviousPositionDTO {
    private Long id;
    private Long personnelId;
    private String personnelName; // Nom complet du personnel

    private String positionTitle;
    
    private Long structureId;
    private String structureName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private Integer durationMonths;
    private String formattedDuration; // Durée formatée

    private Long gradeId;
    private String gradeName;

    private String responsibilities;
    private String endReason;
    private String endType; // Enum: MUTATION, PROMOTION, etc.
    private String notes;

    // Calculated fields
    private Boolean isWithinLastThreeYears;

    // Audit fields
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate createdDate;
}

