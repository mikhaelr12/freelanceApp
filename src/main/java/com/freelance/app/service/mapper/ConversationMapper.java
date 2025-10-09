package com.freelance.app.service.mapper;

import com.freelance.app.domain.Conversation;
import com.freelance.app.domain.Order;
import com.freelance.app.service.dto.ConversationDTO;
import com.freelance.app.service.dto.OrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Conversation} and its DTO {@link ConversationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ConversationMapper extends EntityMapper<ConversationDTO, Conversation> {
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    ConversationDTO toDto(Conversation s);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);
}
