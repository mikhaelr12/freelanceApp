package com.freelance.app.web.rest;

import com.freelance.app.service.ConversationService;
import com.freelance.app.service.dto.ConversationDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link com.freelance.app.domain.Conversation}.
 */
@RestController
@RequestMapping("/api/conversations")
public class ConversationResource {

    private static final Logger LOG = LoggerFactory.getLogger(ConversationResource.class);

    private final ConversationService conversationService;

    public ConversationResource(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("/my")
    public Mono<ResponseEntity<List<ConversationDTO>>> getAllMyConversations() {
        LOG.debug("REST request to get all conversations");
        return conversationService.getAllMyConversations().map(ResponseEntity::ok);
    }
}
