package com.hrms.mapper;

import com.hrms.dto.PersonnelDocumentDTO;
import com.hrms.dto.PersonnelDocumentCreateDTO;
import com.hrms.entity.PersonnelDocument;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonnelDocumentMapper {

    @Mapping(target = "personnelId", source = "personnel.id")
    @Mapping(target = "personnelName", expression = "java(document.getPersonnel() != null ? document.getPersonnel().getFullName() : null)")
    @Mapping(target = "personnelMatricule", source = "personnel.matricule")
    @Mapping(target = "documentType", expression = "java(document.getDocumentType() != null ? document.getDocumentType().name() : null)")
    @Mapping(target = "documentTypeLabel", expression = "java(document.getDocumentTypeLabel())")
    @Mapping(target = "isExpired", expression = "java(document.isExpired())")
    @Mapping(target = "isExpiringSoon", expression = "java(document.isExpiringSoon())")
    PersonnelDocumentDTO toDTO(PersonnelDocument document);

    List<PersonnelDocumentDTO> toDTOList(List<PersonnelDocument> documents);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "verifiedBy", ignore = true)
    @Mapping(target = "verificationDate", ignore = true)
    @Mapping(target = "version", constant = "1")
    @Mapping(target = "replacedDocumentId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "documentType", expression = "java(mapDocumentType(dto.getDocumentType()))")
    PersonnelDocument toEntity(PersonnelDocumentCreateDTO dto);

    default PersonnelDocument.DocumentType mapDocumentType(String type) {
        if (type == null) return null;
        try {
            return PersonnelDocument.DocumentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
