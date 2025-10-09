package com.freelance.app.service.mapper;

import com.freelance.app.domain.Order;
import com.freelance.app.domain.Requirement;
import com.freelance.app.service.dto.OrderDTO;
import com.freelance.app.service.dto.RequirementDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Requirement} and its DTO {@link RequirementDTO}.
 */
@Mapper(componentModel = "spring")
public interface RequirementMapper extends EntityMapper<RequirementDTO, Requirement> {
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    RequirementDTO toDto(Requirement s);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);
}
