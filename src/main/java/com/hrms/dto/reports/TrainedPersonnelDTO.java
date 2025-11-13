package com.hrms.dto.reports;

import lombok.*;
import java.time.LocalDate;

/**
 * DTO pour le personnel formé (export liste participants)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainedPersonnelDTO {
    private Long personnelId;
    private String matricule;
    private String firstName;
    private String lastName;
    private String structure;
    private String trainingTitle;
    private String trainingField;
    private String sessionCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private Integer score;
    private Double attendanceRate;
    private Boolean certificateObtained;
    private String certificateNumber;
}
