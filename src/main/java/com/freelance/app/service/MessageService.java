package com.freelance.app.service;

import com.freelance.app.domain.Message;
import com.freelance.app.domain.Profile;
import com.freelance.app.repository.MessageRepository;
import com.freelance.app.service.dto.MessageShortDTO;
import com.freelance.app.util.ProfileHelper;
import com.freelance.app.web.rest.errors.UnauthorizedAlertException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Message}.
 */
@Service
@Transactional
public class MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final ProfileHelper profileHelper;

    public MessageService(MessageRepository messageRepository, ProfileHelper profileHelper) {
        this.messageRepository = messageRepository;
        this.profileHelper = profileHelper;
    }

    public Mono<List<MessageShortDTO>> getAllConversationMessages(Long conversationId) {
        LOG.debug("Service to get all messages for conversation with id: {}", conversationId);
        return messageRepository.findAllConversationMessages(conversationId).collectList();
    }

    public Mono<Message> editMessage(Long messageId, String editedMessage) {
        return profileHelper
            .getCurrentProfile()
            .zipWith(messageRepository.findById(messageId))
            .flatMap(tuple -> {
                Message message = tuple.getT2();
                Profile profile = tuple.getT1();
                if (!Objects.equals(profile.getId(), message.getSender().getId())) {
                    return Mono.error(
                        new UnauthorizedAlertException(
                            "Message does not belong to current profile",
                            "Message",
                            "messageDoesNotBelogToCurrentProfile"
                        )
                    );
                }
                message.setBody(editedMessage);
                return messageRepository.save(message);
            });
    }
}
