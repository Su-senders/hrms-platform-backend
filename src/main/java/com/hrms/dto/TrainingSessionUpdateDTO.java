package com.hrms.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingSessionUpdateDTO {
    private String title;
    private Long trainingId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private Integer maxParticipants;
    private Integer minParticipants;
    private String status;
    private Long trainerId;
    private List<Long> coTrainerIds;
    private Long organizingStructureId;
    private BigDecimal budget;
    private String description;
    private String notes;
    private LocalDate enrollmentStartDate;
    private LocalDate enrollmentEndDate;
}




