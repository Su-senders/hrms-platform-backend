package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelDocumentCreateDTO {

    @NotNull(message = "Le personnel est obligatoire")
    private Long personnelId;

    @NotNull(message = "Le type de document est obligatoire")
    private String documentType;

    private String documentNumber;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String description;
    private String fileName;

    @NotBlank(message = "Le chemin du fichier est obligatoire")
    private String filePath;

    private Long fileSize;
    private String fileType;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String issuingAuthority;
    private Boolean isMandatory;
    private String notes;
}
