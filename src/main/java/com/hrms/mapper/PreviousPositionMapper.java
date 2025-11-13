package com.hrms.mapper;

import com.hrms.dto.PreviousPositionDTO;
import com.hrms.dto.PreviousPositionCreateDTO;
import com.hrms.dto.PreviousPositionUpdateDTO;
import com.hrms.entity.PreviousPosition;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PreviousPositionMapper {

    @Mapping(target = "personnelId", source = "personnel.id")
    @Mapping(target = "personnelName", expression = "java(position.getPersonnel() != null ? position.getPersonnel().getFullName() : null)")
    @Mapping(target = "structureId", source = "structure.id")
    @Mapping(target = "structureName", expression = "java(position.getStructureNameValue())")
    @Mapping(target = "gradeId", source = "grade.id")
    @Mapping(target = "gradeName", expression = "java(position.getGradeNameValue())")
    @Mapping(target = "endType", expression = "java(position.getEndType() != null ? position.getEndType().name() : null)")
    @Mapping(target = "formattedDuration", expression = "java(position.getFormattedDuration())")
    @Mapping(target = "isWithinLastThreeYears", expression = "java(position.isWithinLastThreeYears())")
    PreviousPositionDTO toDTO(PreviousPosition position);

    List<PreviousPositionDTO> toDTOList(List<PreviousPosition> positions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "structure", ignore = true)
    @Mapping(target = "grade", ignore = true)
    @Mapping(target = "endType", expression = "java(mapEndType(dto.getEndType()))")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    PreviousPosition toEntity(PreviousPositionCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "structure", ignore = true)
    @Mapping(target = "grade", ignore = true)
    @Mapping(target = "endType", expression = "java(mapEndType(dto.getEndType()))")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(PreviousPositionUpdateDTO dto, @MappingTarget PreviousPosition position);

    default PreviousPosition.EndType mapEndType(String endType) {
        if (endType == null) return null;
        try {
            return PreviousPosition.EndType.valueOf(endType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

