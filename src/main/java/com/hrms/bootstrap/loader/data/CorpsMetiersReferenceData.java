package com.hrms.bootstrap.loader.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO racine pour charger l'ensemble des corps de métiers depuis JSON
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorpsMetiersReferenceData {
    private List<CorpsMetierData> corpsMetiers;
}
