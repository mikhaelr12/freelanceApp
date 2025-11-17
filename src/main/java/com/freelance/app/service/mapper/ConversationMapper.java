package com.freelance.app.service.mapper;

import com.freelance.app.domain.Conversation;
import com.freelance.app.domain.Order;
import com.freelance.app.service.dto.ConversationDTO;
import com.freelance.app.service.dto.OrderDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for the entity {@link Conversation} and its DTO {@link ConversationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ConversationMapper extends EntityMapper<ConversationDTO, Conversation> {
    @BeanMapping(ignoreByDefault = true)
    Order toEntity(OrderDTO orderDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    void partialUpdate(@MappingTarget Conversation entity, ConversationDTO dto);
}
