package com.hrms.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingSessionDTO {
    private Long id;
    private String code;
    private String title;
    private Long trainingId;
    private String trainingTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private Integer maxParticipants;
    private Integer minParticipants;
    private Integer enrolledCount;
    private Integer availableSlots;
    private String status; // PLANNED, OPEN, IN_PROGRESS, etc.
    private Long trainerId;
    private String trainerName;
    private List<Long> coTrainerIds;
    private List<String> coTrainerNames;
    private Long organizingStructureId;
    private String organizingStructureName;
    private BigDecimal totalCost;
    private BigDecimal costPerParticipant;
    private BigDecimal budget;
    private String description;
    private String notes;
    private LocalDate enrollmentStartDate;
    private LocalDate enrollmentEndDate;
    private Boolean isFull;
    private Boolean isEnrollmentOpen;
    private Boolean canStart;
}




