package com.hrms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerMovementDTO {
    private Long id;

    private Long personnelId;
    private String personnelName;
    private String personnelMatricule;

    private String movementType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate movementDate;

    private String decisionNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate decisionDate;

    // Source
    private Long sourcePositionId;
    private String sourcePositionTitle;
    private Long sourceStructureId;
    private String sourceStructureName;
    private String sourceGrade;

    // Destination
    private Long destinationPositionId;
    private String destinationPositionTitle;
    private Long destinationStructureId;
    private String destinationStructureName;
    private String destinationGrade;

    private String reason;
    private String notes;
    private Boolean isOfficialCumul;
    private Integer durationMonths;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String status;
    private String approvedBy;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate approvalDate;

    private String documentPath;

    // Audit fields
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate createdDate;
}
