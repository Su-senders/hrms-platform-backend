package com.hrms.mapper;

import com.hrms.dto.TrainingSessionCreateDTO;
import com.hrms.dto.TrainingSessionDTO;
import com.hrms.dto.TrainingSessionUpdateDTO;
import com.hrms.entity.TrainingSession;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingSessionMapper {

    @Mapping(target = "trainingId", source = "training.id")
    @Mapping(target = "trainingTitle", source = "training.title")
    @Mapping(target = "trainerId", source = "trainer.id")
    @Mapping(target = "trainerName", expression = "java(session.getTrainer() != null ? session.getTrainer().getFullName() : null)")
    @Mapping(target = "coTrainerIds", expression = "java(mapCoTrainerIds(session))")
    @Mapping(target = "coTrainerNames", expression = "java(mapCoTrainerNames(session))")
    @Mapping(target = "organizingStructureId", source = "organizingStructure.id")
    @Mapping(target = "organizingStructureName", source = "organizingStructure.name")
    @Mapping(target = "status", expression = "java(session.getStatus() != null ? session.getStatus().name() : null)")
    @Mapping(target = "enrolledCount", expression = "java(session.getEnrolledCount())")
    @Mapping(target = "availableSlots", expression = "java(session.getAvailableSlots())")
    @Mapping(target = "isFull", expression = "java(session.isFull())")
    @Mapping(target = "isEnrollmentOpen", expression = "java(session.isEnrollmentOpen())")
    @Mapping(target = "canStart", expression = "java(session.canStart())")
    TrainingSessionDTO toDTO(TrainingSession session);

    List<TrainingSessionDTO> toDTOList(List<TrainingSession> sessions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "training", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "coTrainers", ignore = true)
    @Mapping(target = "organizingStructure", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "costs", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalCost", ignore = true)
    @Mapping(target = "costPerParticipant", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    TrainingSession toEntity(TrainingSessionCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "training", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "coTrainers", ignore = true)
    @Mapping(target = "organizingStructure", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "costs", ignore = true)
    @Mapping(target = "totalCost", ignore = true)
    @Mapping(target = "costPerParticipant", ignore = true)
    @Mapping(target = "status", expression = "java(mapSessionStatus(dto.getStatus()))")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TrainingSessionUpdateDTO dto, @MappingTarget TrainingSession session);

    default TrainingSession.SessionStatus mapSessionStatus(String status) {
        if (status == null) return null;
        try {
            return TrainingSession.SessionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default List<Long> mapCoTrainerIds(TrainingSession session) {
        if (session.getCoTrainers() == null) return null;
        return session.getCoTrainers().stream()
                .map(trainer -> trainer.getId())
                .collect(Collectors.toList());
    }

    default List<String> mapCoTrainerNames(TrainingSession session) {
        if (session.getCoTrainers() == null) return null;
        return session.getCoTrainers().stream()
                .map(trainer -> trainer.getFullName())
                .collect(Collectors.toList());
    }
}




