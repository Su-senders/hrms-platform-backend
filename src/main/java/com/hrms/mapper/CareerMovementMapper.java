package com.hrms.mapper;

import com.hrms.dto.CareerMovementDTO;
import com.hrms.dto.CareerMovementCreateDTO;
import com.hrms.dto.CareerMovementUpdateDTO;
import com.hrms.entity.CareerMovement;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CareerMovementMapper {

    @Mapping(target = "personnelId", source = "personnel.id")
    @Mapping(target = "personnelName", expression = "java(movement.getPersonnel() != null ? movement.getPersonnel().getFullName() : null)")
    @Mapping(target = "personnelMatricule", source = "personnel.matricule")
    @Mapping(target = "sourcePositionId", source = "sourcePosition.id")
    @Mapping(target = "sourcePositionTitle", source = "sourcePosition.title")
    @Mapping(target = "sourceStructureId", source = "sourceStructure.id")
    @Mapping(target = "sourceStructureName", source = "sourceStructure.name")
    @Mapping(target = "destinationPositionId", source = "destinationPosition.id")
    @Mapping(target = "destinationPositionTitle", source = "destinationPosition.title")
    @Mapping(target = "destinationStructureId", source = "destinationStructure.id")
    @Mapping(target = "destinationStructureName", source = "destinationStructure.name")
    @Mapping(target = "movementType", expression = "java(movement.getMovementType() != null ? movement.getMovementType().name() : null)")
    @Mapping(target = "status", expression = "java(movement.getStatus() != null ? movement.getStatus().name() : null)")
    CareerMovementDTO toDTO(CareerMovement movement);

    List<CareerMovementDTO> toDTOList(List<CareerMovement> movements);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "sourcePosition", ignore = true)
    @Mapping(target = "sourceStructure", ignore = true)
    @Mapping(target = "destinationPosition", ignore = true)
    @Mapping(target = "destinationStructure", ignore = true)
    @Mapping(target = "status", expression = "java(com.hrms.entity.CareerMovement.MovementStatus.PENDING)")
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "approvalDate", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "movementType", expression = "java(mapMovementType(dto.getMovementType()))")
    CareerMovement toEntity(CareerMovementCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "sourcePosition", ignore = true)
    @Mapping(target = "sourceStructure", ignore = true)
    @Mapping(target = "destinationPosition", ignore = true)
    @Mapping(target = "destinationStructure", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "movementType", expression = "java(mapMovementType(dto.getMovementType()))")
    @Mapping(target = "status", expression = "java(mapMovementStatus(dto.getStatus()))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(CareerMovementUpdateDTO dto, @MappingTarget CareerMovement movement);

    default CareerMovement.MovementType mapMovementType(String type) {
        if (type == null) return null;
        try {
            return CareerMovement.MovementType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default CareerMovement.MovementStatus mapMovementStatus(String status) {
        if (status == null) return null;
        try {
            return CareerMovement.MovementStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
