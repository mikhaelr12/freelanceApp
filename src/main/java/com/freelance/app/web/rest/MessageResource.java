package com.freelance.app.web.rest;

import com.freelance.app.domain.Message;
import com.freelance.app.service.MessageService;
import com.freelance.app.service.dto.MessageShortDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link com.freelance.app.domain.Message}.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(MessageResource.class);

    private final MessageService messageService;

    public MessageResource(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{conversationId}")
    public Mono<ResponseEntity<List<MessageShortDTO>>> getAllConversationMessages(@PathVariable Long conversationId) {
        LOG.debug("REST request to get all Messages for conversation with id: {}", conversationId);
        return messageService.getAllConversationMessages(conversationId).map(ResponseEntity::ok);
    }

    @PatchMapping("/edit-message/{messageId}")
    public Mono<ResponseEntity<Message>> editMessage(@PathVariable Long messageId, @RequestBody String editedMessage) {
        LOG.debug("REST request to edit message with id: {}", messageId);
        return messageService.editMessage(messageId, editedMessage).map(ResponseEntity::ok);
    }
}
