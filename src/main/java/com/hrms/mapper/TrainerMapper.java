package com.hrms.mapper;

import com.hrms.dto.TrainerCreateDTO;
import com.hrms.dto.TrainerDTO;
import com.hrms.dto.TrainerUpdateDTO;
import com.hrms.entity.Trainer;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainerMapper {

    @Mapping(target = "fullName", expression = "java(trainer.getFullName())")
    @Mapping(target = "type", expression = "java(trainer.getType() != null ? trainer.getType().name() : null)")
    @Mapping(target = "personnelId", source = "personnel.id")
    @Mapping(target = "personnelName", expression = "java(trainer.getPersonnel() != null ? trainer.getPersonnel().getFullName() : null)")
    TrainerDTO toDTO(Trainer trainer);

    List<TrainerDTO> toDTOList(List<Trainer> trainers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "sessions", ignore = true)
    @Mapping(target = "coTrainingSessions", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "type", expression = "java(mapTrainerType(dto.getType()))")
    Trainer toEntity(TrainerCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "sessions", ignore = true)
    @Mapping(target = "coTrainingSessions", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "type", expression = "java(mapTrainerType(dto.getType()))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TrainerUpdateDTO dto, @MappingTarget Trainer trainer);

    default Trainer.TrainerType mapTrainerType(String type) {
        if (type == null) return Trainer.TrainerType.EXTERNAL;
        try {
            return Trainer.TrainerType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Trainer.TrainerType.EXTERNAL;
        }
    }
}




