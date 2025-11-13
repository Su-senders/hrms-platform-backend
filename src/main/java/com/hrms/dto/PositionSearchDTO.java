package com.hrms.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionSearchDTO {
    private String searchTerm; // Search in title, code
    private String code;
    private String title;
    private Long structureId;
    private String structureName;
    private String rank;
    private String category;
    private String status; // VACANT, OCCUPE, EN_CREATION, SUPPRIME
    private Boolean isManagerial;
    private Boolean active;
}
