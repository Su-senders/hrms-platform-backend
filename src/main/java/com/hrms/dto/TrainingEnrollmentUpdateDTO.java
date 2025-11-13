package com.hrms.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingEnrollmentUpdateDTO {
    private String status;
    private String rejectionReason;
    private Boolean certificateIssued;
    private String certificateNumber;
    private String evaluation;
    private Integer score;
    private Double attendanceRate;
    private String notes;
}




