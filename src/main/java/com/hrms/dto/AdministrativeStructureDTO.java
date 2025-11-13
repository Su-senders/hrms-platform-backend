package com.hrms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministrativeStructureDTO {
    private Long id;
    private String code;
    private String name;
    private String type;
    private String description;

    private Long parentStructureId;
    private String parentStructureName;

    private Integer level;
    private String region;
    private String department;
    private String arrondissement;
    private String address;
    private String phone;
    private String email;
    private String fax;
    private String headOfficerName;
    private String headOfficerTitle;
    private Boolean active;

    // Statistics
    private Integer totalPositions;
    private Integer occupiedPositions;
    private Integer vacantPositions;

    // Full path
    private String fullPath;

    // Audit fields
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate createdDate;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate updatedDate;
}
