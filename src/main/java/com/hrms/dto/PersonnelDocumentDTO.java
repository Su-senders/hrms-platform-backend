package com.hrms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelDocumentDTO {
    private Long id;

    private Long personnelId;
    private String personnelName;
    private String personnelMatricule;

    private String documentType;
    private String documentTypeLabel;
    private String documentNumber;
    private String title;
    private String description;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private String issuingAuthority;
    private Boolean isMandatory;
    private Boolean isVerified;
    private String verifiedBy;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate verificationDate;

    private String notes;
    private Integer version;
    private Long replacedDocumentId;

    // Calculated fields
    private Boolean isExpired;
    private Boolean isExpiringSoon;

    // Audit fields
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate createdDate;
}
