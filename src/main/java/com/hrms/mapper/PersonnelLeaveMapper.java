package com.hrms.mapper;

import com.hrms.dto.PersonnelLeaveDTO;
import com.hrms.dto.PersonnelLeaveCreateDTO;
import com.hrms.dto.PersonnelLeaveUpdateDTO;
import com.hrms.entity.PersonnelLeave;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonnelLeaveMapper {

    @Mapping(target = "personnelId", source = "personnel.id")
    @Mapping(target = "personnelName", expression = "java(leave.getPersonnel() != null ? leave.getPersonnel().getFullName() : null)")
    @Mapping(target = "leaveReason", expression = "java(leave.getLeaveReason() != null ? leave.getLeaveReason().name() : null)")
    @Mapping(target = "status", expression = "java(leave.getStatus() != null ? leave.getStatus().name() : null)")
    @Mapping(target = "formattedDuration", expression = "java(leave.getFormattedDuration())")
    @Mapping(target = "isInProgress", expression = "java(leave.isInProgress())")
    @Mapping(target = "isCompleted", expression = "java(leave.isCompleted())")
    @Mapping(target = "isUpcoming", expression = "java(leave.isUpcoming())")
    PersonnelLeaveDTO toDTO(PersonnelLeave leave);

    List<PersonnelLeaveDTO> toDTOList(List<PersonnelLeave> leaves);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "leaveReason", expression = "java(mapLeaveReason(dto.getLeaveReason()))")
    @Mapping(target = "status", expression = "java(mapStatus(dto.getStatus()))")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    PersonnelLeave toEntity(PersonnelLeaveCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personnel", ignore = true)
    @Mapping(target = "leaveReason", expression = "java(mapLeaveReason(dto.getLeaveReason()))")
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
    void updateEntity(PersonnelLeaveUpdateDTO dto, @MappingTarget PersonnelLeave leave);

    default PersonnelLeave.LeaveReason mapLeaveReason(String reason) {
        if (reason == null) return null;
        try {
            return PersonnelLeave.LeaveReason.valueOf(reason.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default PersonnelLeave.LeaveStatus mapStatus(String status) {
        if (status == null) return PersonnelLeave.LeaveStatus.APPROVED;
        try {
            return PersonnelLeave.LeaveStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PersonnelLeave.LeaveStatus.APPROVED;
        }
    }
}

