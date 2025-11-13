package com.hrms.mapper;

import com.hrms.dto.TrainingCreateDTO;
import com.hrms.dto.TrainingDTO;
import com.hrms.dto.TrainingUpdateDTO;
import com.hrms.entity.Training;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingMapper {

    @Mapping(target = "category", expression = "java(training.getCategory() != null ? training.getCategory().name() : null)")
    TrainingDTO toDTO(Training training);

    List<TrainingDTO> toDTOList(List<Training> trainings);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sessions", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "category", expression = "java(mapTrainingCategory(dto.getCategory()))")
    Training toEntity(TrainingCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "sessions", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "category", expression = "java(mapTrainingCategory(dto.getCategory()))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TrainingUpdateDTO dto, @MappingTarget Training training);

    default Training.TrainingCategory mapTrainingCategory(String category) {
        if (category == null) return Training.TrainingCategory.OTHER;
        try {
            return Training.TrainingCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Training.TrainingCategory.OTHER;
        }
    }
}




