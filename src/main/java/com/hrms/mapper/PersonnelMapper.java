package com.hrms.mapper;

import com.hrms.dto.PersonnelDTO;
import com.hrms.dto.PersonnelCreateDTO;
import com.hrms.dto.PersonnelUpdateDTO;
import com.hrms.entity.Personnel;
import org.mapstruct.*;

import java.time.Period;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonnelMapper {

    @Mapping(target = "currentPositionId", source = "currentPosition.id")
    @Mapping(target = "currentPositionTitle", source = "currentPosition.title")
    @Mapping(target = "structureId", source = "structure.id")
    @Mapping(target = "structureName", source = "structure.name")
    @Mapping(target = "fullName", expression = "java(personnel.getFullName())")
    @Mapping(target = "age", expression = "java(personnel.getAge())")
    @Mapping(target = "seniorityInPost", expression = "java(formatPeriod(personnel.getSeniorityInPost()))")
    @Mapping(target = "seniorityInAdministration", expression = "java(formatPeriod(personnel.getSeniorityInAdministration()))")
    @Mapping(target = "gender", expression = "java(personnel.getGender() != null ? personnel.getGender().name() : null)")
    @Mapping(target = "maritalStatus", expression = "java(personnel.getMaritalStatus() != null ? personnel.getMaritalStatus().name() : null)")
    @Mapping(target = "status", expression = "java(personnel.getStatus() != null ? personnel.getStatus().name() : null)")
    @Mapping(target = "situation", expression = "java(personnel.getSituation() != null ? personnel.getSituation().name() : null)")
    @Mapping(target = "recruitmentDiplomaType", expression = "java(personnel.getRecruitmentDiplomaType() != null ? personnel.getRecruitmentDiplomaType().name() : null)")
    @Mapping(target = "recruitmentEducationLevel", expression = "java(personnel.getRecruitmentEducationLevel() != null ? personnel.getRecruitmentEducationLevel().name() : null)")
    @Mapping(target = "recruitmentStudyField", expression = "java(personnel.getRecruitmentStudyField() != null ? personnel.getRecruitmentStudyField().name() : null)")
    @Mapping(target = "highestDiplomaType", expression = "java(personnel.getHighestDiplomaType() != null ? personnel.getHighestDiplomaType().name() : null)")
    @Mapping(target = "highestEducationLevel", expression = "java(personnel.getHighestEducationLevel() != null ? personnel.getHighestEducationLevel().name() : null)")
    @Mapping(target = "highestStudyField", expression = "java(personnel.getHighestStudyField() != null ? personnel.getHighestStudyField().name() : null)")
    PersonnelDTO toDTO(Personnel personnel);

    List<PersonnelDTO> toDTOList(List<Personnel> personnelList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentPosition", ignore = true)
    @Mapping(target = "structure", ignore = true)
    @Mapping(target = "retirementDate", ignore = true)
    @Mapping(target = "isRetirableThisYear", ignore = true)
    @Mapping(target = "isRetirableNextYear", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "gender", expression = "java(mapGender(dto.getGender()))")
    @Mapping(target = "maritalStatus", expression = "java(mapMaritalStatus(dto.getMaritalStatus()))")
    @Mapping(target = "status", expression = "java(mapStatus(dto.getStatus()))")
    @Mapping(target = "situation", expression = "java(mapSituation(dto.getSituation()))")
    @Mapping(target = "recruitmentDiplomaType", expression = "java(mapDiplomaType(dto.getRecruitmentDiplomaType()))")
    @Mapping(target = "recruitmentEducationLevel", expression = "java(mapEducationLevel(dto.getRecruitmentEducationLevel()))")
    @Mapping(target = "recruitmentStudyField", expression = "java(mapStudyField(dto.getRecruitmentStudyField()))")
    @Mapping(target = "highestDiplomaType", expression = "java(mapDiplomaType(dto.getHighestDiplomaType()))")
    @Mapping(target = "highestEducationLevel", expression = "java(mapEducationLevel(dto.getHighestEducationLevel()))")
    @Mapping(target = "highestStudyField", expression = "java(mapStudyField(dto.getHighestStudyField()))")
    Personnel toEntity(PersonnelCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentPosition", ignore = true)
    @Mapping(target = "structure", ignore = true)
    @Mapping(target = "retirementDate", ignore = true)
    @Mapping(target = "isRetirableThisYear", ignore = true)
    @Mapping(target = "isRetirableNextYear", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "gender", expression = "java(mapGender(dto.getGender()))")
    @Mapping(target = "maritalStatus", expression = "java(mapMaritalStatus(dto.getMaritalStatus()))")
    @Mapping(target = "status", expression = "java(mapStatus(dto.getStatus()))")
    @Mapping(target = "situation", expression = "java(mapSituation(dto.getSituation()))")
    @Mapping(target = "recruitmentDiplomaType", expression = "java(mapDiplomaType(dto.getRecruitmentDiplomaType()))")
    @Mapping(target = "recruitmentEducationLevel", expression = "java(mapEducationLevel(dto.getRecruitmentEducationLevel()))")
    @Mapping(target = "recruitmentStudyField", expression = "java(mapStudyField(dto.getRecruitmentStudyField()))")
    @Mapping(target = "highestDiplomaType", expression = "java(mapDiplomaType(dto.getHighestDiplomaType()))")
    @Mapping(target = "highestEducationLevel", expression = "java(mapEducationLevel(dto.getHighestEducationLevel()))")
    @Mapping(target = "highestStudyField", expression = "java(mapStudyField(dto.getHighestStudyField()))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(PersonnelUpdateDTO dto, @MappingTarget Personnel personnel);

    // Helper methods for formatting
    default String formatPeriod(Period period) {
        if (period == null) {
            return null;
        }
        int years = period.getYears();
        int months = period.getMonths();
        if (years > 0 && months > 0) {
            return years + " ans " + months + " mois";
        } else if (years > 0) {
            return years + " ans";
        } else if (months > 0) {
            return months + " mois";
        }
        return "0 mois";
    }

    default Personnel.Gender mapGender(String gender) {
        if (gender == null) return null;
        try {
            return Personnel.Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default Personnel.MaritalStatus mapMaritalStatus(String status) {
        if (status == null) return null;
        try {
            return Personnel.MaritalStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default Personnel.PersonnelStatus mapStatus(String status) {
        if (status == null) return Personnel.PersonnelStatus.ACTIVE;
        try {
            return Personnel.PersonnelStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Personnel.PersonnelStatus.ACTIVE;
        }
    }

    default Personnel.PersonnelSituation mapSituation(String situation) {
        if (situation == null) return Personnel.PersonnelSituation.EN_FONCTION;
        try {
            return Personnel.PersonnelSituation.valueOf(situation.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Personnel.PersonnelSituation.EN_FONCTION;
        }
    }

    // Helper methods for Section B - Qualifications
    default Personnel.DiplomaType mapDiplomaType(String type) {
        if (type == null) return null;
        try {
            return Personnel.DiplomaType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default Personnel.EducationLevel mapEducationLevel(String level) {
        if (level == null) return null;
        try {
            return Personnel.EducationLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default Personnel.StudyField mapStudyField(String field) {
        if (field == null) return null;
        try {
            return Personnel.StudyField.valueOf(field.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
