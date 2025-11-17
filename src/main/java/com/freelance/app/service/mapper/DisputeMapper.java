package com.freelance.app.service.mapper;

import com.freelance.app.domain.Dispute;
import com.freelance.app.domain.Order;
import com.freelance.app.service.dto.DisputeDTO;
import com.freelance.app.service.dto.OrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Dispute} and its DTO {@link DisputeDTO}.
 */
@Mapper(componentModel = "spring")
public interface DisputeMapper extends EntityMapper<DisputeDTO, Dispute> {
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    DisputeDTO toDto(Dispute s);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    @BeanMapping(ignoreByDefault = true)
    Order toEntity(OrderDTO orderDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    void partialUpdate(@MappingTarget Dispute entity, DisputeDTO dto);
}
