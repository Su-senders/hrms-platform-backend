package com.hrms.mapper;

import com.hrms.dto.PositionDTO;
import com.hrms.dto.PositionCreateDTO;
import com.hrms.dto.PositionUpdateDTO;
import com.hrms.entity.Position;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PositionMapper {

    @Mapping(target = "structureId", source = "structure.id")
    @Mapping(target = "structureName", source = "structure.name")
    @Mapping(target = "currentPersonnelId", source = "currentPersonnel.id")
    @Mapping(target = "currentPersonnelName", expression = "java(position.getCurrentPersonnel() != null ? position.getCurrentPersonnel().getFullName() : null)")
    @Mapping(target = "currentPersonnelMatricule", source = "currentPersonnel.matricule")
    @Mapping(target = "status", expression = "java(position.getStatus() != null ? position.getStatus().name() : null)")
    PositionDTO toDTO(Position position);

    List<PositionDTO> toDTOList(List<Position> positions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "structure", ignore = true)
    @Mapping(target = "currentPersonnel", ignore = true)
    @Mapping(target = "assignmentDate", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", expression = "java(mapStatus(dto.getStatus()))")
    Position toEntity(PositionCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "structure", ignore = true)
    @Mapping(target = "currentPersonnel", ignore = true)
    @Mapping(target = "assignmentDate", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", expression = "java(mapStatus(dto.getStatus()))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(PositionUpdateDTO dto, @MappingTarget Position position);

    default Position.PositionStatus mapStatus(String status) {
        if (status == null) return Position.PositionStatus.VACANT;
        try {
            return Position.PositionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Position.PositionStatus.VACANT;
        }
    }
}
