package com.hrms.bootstrap.loader.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour charger les données de Grade depuis JSON
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeData {
    private String code;
    private String name;
    private Integer level;
    private String category;
    private String description;
}
