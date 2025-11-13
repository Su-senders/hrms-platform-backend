package com.hrms.bootstrap.loader.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour charger les données de Corps de Métier depuis JSON
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorpsMetierData {
    private String code;
    private String name;
    private String description;
    private String category;
    private String ministere;
    private List<GradeData> grades;
}
