package com.hrms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionDTO {
    private Long id;
    private String code;
    private String title;
    private String description;
    private Long structureId;
    private String structureName;
    private String rank;
    private String category;
    private String requiredGrade;
    private String requiredCorps;
    private String status;

    private Long currentPersonnelId;
    private String currentPersonnelName;
    private String currentPersonnelMatricule;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate assignmentDate;

    private String responsibilities;
    private String requiredQualifications;
    private Integer minExperienceYears;
    private Boolean isManagerial;
    private Long reportsToPositionId;
    private String reportsToPositionTitle;
    private String budgetCode;
    private Boolean active;

    // Audit fields
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate createdDate;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate updatedDate;
}
