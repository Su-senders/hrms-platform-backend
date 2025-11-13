package com.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArrondissementDTO {
    private Long id;
    private String code;
    private String name;
    private String chefLieu;
    private String type; // NORMAL, URBAIN, SPECIAL
    private Double superficieKm2;
    private Long population;
    private String description;
    private Boolean active;

    // Département parent
    private Long departmentId;
    private String departmentCode;
    private String departmentName;

    // Région (via département)
    private Long regionId;
    private String regionCode;
    private String regionName;

    // Informations sur la sous-préfecture associée
    private Long sousPrefectureId;
    private String sousPrefectureCode;
    private String sousPrefectureName;
}
