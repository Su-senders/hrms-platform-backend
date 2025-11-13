package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministrativeStructureUpdateDTO {

    @Size(max = 50, message = "Le code ne peut pas dépasser 50 caractères")
    private String code;

    private String name;
    private String type;
    private String description;
    private Long parentStructureId;
    private String region;
    private String department;
    private String arrondissement;
    private String address;

    @Pattern(regexp = "^[+]?[0-9]{9,20}$", message = "Numéro de téléphone invalide")
    private String phone;

    @Email(message = "Email invalide")
    private String email;

    private String fax;
    private String headOfficerName;
    private String headOfficerTitle;
    private Boolean active;
}
