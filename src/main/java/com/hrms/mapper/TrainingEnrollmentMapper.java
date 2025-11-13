package com.hrms.mapper;

import com.hrms.dto.TrainingEnrollmentCreateDTO;
import com.hrms.dto.TrainingEnrollmentDTO;
import com.hrms.dto.TrainingEnrollmentUpdateDTO;
import com.hrms.entity.TrainingEnrollment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingEnrollmentMapper {

    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "sessionTitle", source = "session.title")
    @Mapping(target = "personnelId", source = "personnel.id")
    @Mapping(target = "personnelName", expression = "java(enrollment.getPersonnel() != null ? enrollment.getPersonnel().getFullName() : null)")
    @Mapping(target = "personnelMatricule", source = "personnel.matricule")
    @Mapping(target = "status", expression = "java(enrollment.getStatus() != null ? enrollment.getStatus().name() : null)")
    TrainingEnrollmentDTO toDTO(TrainingEnrollment enrollment);

    List<TrainingEnrollmentDTO> toDTOList(List<TrainingEnrollment> enrollments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "session", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "enrollmentDate", ignore = true)
    @Mapping(target = "approvalDate", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "certificateIssued", ignore = true)
    @Mapping(target = "certificateNumber", ignore = true)
    @Mapping(target = "certificateDate", ignore = true)
    @Mapping(target = "evaluation", ignore = true)
    @Mapping(target = "score", ignore = true)
    @Mapping(target = "attendanceRate", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    TrainingEnrollment toEntity(TrainingEnrollmentCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "session", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "enrollmentDate", ignore = true)
    @Mapping(target = "approvalDate", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "certificateDate", ignore = true)
    @Mapping(target = "status", expression = "java(mapEnrollmentStatus(dto.getStatus()))")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TrainingEnrollmentUpdateDTO dto, @MappingTarget TrainingEnrollment enrollment);

    default TrainingEnrollment.EnrollmentStatus mapEnrollmentStatus(String status) {
        if (status == null) return null;
        try {
            return TrainingEnrollment.EnrollmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}




