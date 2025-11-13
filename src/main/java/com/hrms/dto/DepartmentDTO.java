package com.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDTO {
    private Long id;
    private String code;
    private String name;
    private String chefLieu;
    private Double superficieKm2;
    private Long population;
    private String description;
    private Boolean active;

    // Région parente
    private Long regionId;
    private String regionCode;
    private String regionName;

    // Informations sur la préfecture associée
    private Long prefectureId;
    private String prefectureCode;
    private String prefectureName;

    // Statistiques
    private Long nombreArrondissements;
}
