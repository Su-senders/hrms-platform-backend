package com.hrms.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingEnrollmentDTO {
    private Long id;
    private Long sessionId;
    private String sessionTitle;
    private Long personnelId;
    private String personnelName;
    private String personnelMatricule;
    private String status; // PENDING, APPROVED, REJECTED, etc.
    private LocalDate enrollmentDate;
    private LocalDate approvalDate;
    private String approvedBy;
    private String rejectionReason;
    private Boolean certificateIssued;
    private String certificateNumber;
    private LocalDate certificateDate;
    private String evaluation;
    private Integer score;
    private Double attendanceRate;
    private String notes;
}




