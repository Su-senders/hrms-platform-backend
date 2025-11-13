package com.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionDTO {
    private Long id;
    private String code;
    private String name;
    private String chefLieu;
    private Double superficieKm2;
    private Long population;
    private String description;
    private Boolean active;

    // Informations sur le gouvernorat associé
    private Long gouvernoratId;
    private String gouvernoratCode;
    private String gouvernoratName;

    // Statistiques
    private Long nombreDepartements;
    private Long nombreArrondissements;
}
