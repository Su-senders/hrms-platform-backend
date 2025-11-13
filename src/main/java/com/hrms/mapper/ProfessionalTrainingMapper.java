package com.hrms.mapper;

import com.hrms.dto.ProfessionalTrainingDTO;
import com.hrms.dto.ProfessionalTrainingCreateDTO;
import com.hrms.dto.ProfessionalTrainingUpdateDTO;
import com.hrms.entity.ProfessionalTraining;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfessionalTrainingMapper {

    @Mapping(target = "personnelId", source = "personnel.id")
    @Mapping(target = "personnelName", expression = "java(training.getPersonnel() != null ? training.getPersonnel().getFullName() : null)")
    @Mapping(target = "status", expression = "java(training.getStatus() != null ? training.getStatus().name() : null)")
    @Mapping(target = "formattedDuration", expression = "java(training.getFormattedDuration())")
    @Mapping(target = "isInProgress", expression = "java(training.isInProgress())")
    @Mapping(target = "isCompleted", expression = "java(training.isCompleted())")
    ProfessionalTrainingDTO toDTO(ProfessionalTraining training);

    List<ProfessionalTrainingDTO> toDTOList(List<ProfessionalTraining> trainings);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "status", expression = "java(mapStatus(dto.getStatus()))")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    ProfessionalTraining toEntity(ProfessionalTrainingCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "status", expression = "java(mapStatus(dto.getStatus()))")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ProfessionalTrainingUpdateDTO dto, @MappingTarget ProfessionalTraining training);

    default ProfessionalTraining.TrainingStatus mapStatus(String status) {
        if (status == null) return ProfessionalTraining.TrainingStatus.COMPLETED;
        try {
            return ProfessionalTraining.TrainingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ProfessionalTraining.TrainingStatus.COMPLETED;
        }
    }
}

