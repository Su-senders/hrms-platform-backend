package com.hrms.mapper;

import com.hrms.dto.TrainingCostCreateDTO;
import com.hrms.dto.TrainingCostDTO;
import com.hrms.dto.TrainingCostUpdateDTO;
import com.hrms.entity.TrainingCost;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingCostMapper {

    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "sessionTitle", source = "session.title")
    @Mapping(target = "costType", expression = "java(cost.getCostType() != null ? cost.getCostType().name() : null)")
    @Mapping(target = "paymentStatus", expression = "java(cost.getPaymentStatus() != null ? cost.getPaymentStatus().name() : null)")
    TrainingCostDTO toDTO(TrainingCost cost);

    List<TrainingCostDTO> toDTOList(List<TrainingCost> costs);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "session", ignore = true)
    @Mapping(target = "paymentStatus", expression = "java(mapPaymentStatus(dto.getPaymentStatus()))")
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "costType", expression = "java(mapCostType(dto.getCostType()))")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    TrainingCost toEntity(TrainingCostCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "session", ignore = true)
    @Mapping(target = "costType", expression = "java(mapCostType(dto.getCostType()))")
    @Mapping(target = "paymentStatus", expression = "java(mapPaymentStatus(dto.getPaymentStatus()))")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TrainingCostUpdateDTO dto, @MappingTarget TrainingCost cost);

    default TrainingCost.CostType mapCostType(String type) {
        if (type == null) return null;
        try {
            return TrainingCost.CostType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default TrainingCost.PaymentStatus mapPaymentStatus(String status) {
        if (status == null) return TrainingCost.PaymentStatus.PENDING;
        try {
            return TrainingCost.PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TrainingCost.PaymentStatus.PENDING;
        }
    }
}




