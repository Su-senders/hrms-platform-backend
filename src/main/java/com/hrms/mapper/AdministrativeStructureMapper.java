package com.hrms.mapper;

import com.hrms.dto.AdministrativeStructureDTO;
import com.hrms.dto.AdministrativeStructureCreateDTO;
import com.hrms.dto.AdministrativeStructureUpdateDTO;
import com.hrms.entity.AdministrativeStructure;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdministrativeStructureMapper {

    @Mapping(target = "parentStructureId", source = "parentStructure.id")
    @Mapping(target = "parentStructureName", source = "parentStructure.name")
    @Mapping(target = "type", expression = "java(structure.getType() != null ? structure.getType().name() : null)")
    @Mapping(target = "fullPath", expression = "java(structure.getFullPath())")
    AdministrativeStructureDTO toDTO(AdministrativeStructure structure);

    List<AdministrativeStructureDTO> toDTOList(List<AdministrativeStructure> structures);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentStructure", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "totalPositions", ignore = true)
    @Mapping(target = "occupiedPositions", constant = "0")
    @Mapping(target = "vacantPositions", constant = "0")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "type", expression = "java(mapStructureType(dto.getType()))")
    AdministrativeStructure toEntity(AdministrativeStructureCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentStructure", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "totalPositions", ignore = true)
    @Mapping(target = "occupiedPositions", ignore = true)
    @Mapping(target = "vacantPositions", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "type", expression = "java(mapStructureType(dto.getType()))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(AdministrativeStructureUpdateDTO dto, @MappingTarget AdministrativeStructure structure);

    default AdministrativeStructure.StructureType mapStructureType(String type) {
        if (type == null) return null;
        try {
            return AdministrativeStructure.StructureType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
