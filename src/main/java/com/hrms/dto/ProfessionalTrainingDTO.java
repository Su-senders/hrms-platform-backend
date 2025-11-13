package com.hrms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalTrainingDTO {
    private Long id;
    private Long personnelId;
    private String personnelName; // Nom complet du personnel

    private String trainingField;
    private String trainer;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private Integer durationDays;
    private String formattedDuration; // Durée formatée
    private String trainingLocation;
    private String description;
    private String certificateObtained;
    private String status;
    private String notes;

    // Calculated fields
    private Boolean isInProgress;
    private Boolean isCompleted;

    // Audit fields
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate createdDate;
}

