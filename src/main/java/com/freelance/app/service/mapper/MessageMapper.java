package com.freelance.app.service.mapper;

import com.freelance.app.domain.Conversation;
import com.freelance.app.domain.Message;
import com.freelance.app.domain.User;
import com.freelance.app.service.dto.ConversationDTO;
import com.freelance.app.service.dto.MessageDTO;
import com.freelance.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Message} and its DTO {@link MessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface MessageMapper extends EntityMapper<MessageDTO, Message> {
    @Mapping(target = "conversation", source = "conversation", qualifiedByName = "conversationId")
    @Mapping(target = "sender", source = "sender", qualifiedByName = "userLogin")
    MessageDTO toDto(Message s);

    @Named("conversationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ConversationDTO toDtoConversationId(Conversation conversation);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
