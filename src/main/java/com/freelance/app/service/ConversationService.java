package com.freelance.app.service;

import com.freelance.app.domain.Conversation;
import com.freelance.app.domain.Message;
import com.freelance.app.domain.Profile;
import com.freelance.app.repository.ConversationRepository;
import com.freelance.app.repository.MessageRepository;
import com.freelance.app.repository.ProfileRepository;
import com.freelance.app.service.dto.ConversationDTO;
import com.freelance.app.util.ProfileHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Conversation}.
 */
@Service
@Transactional
public class ConversationService {

    private static final Logger LOG = LoggerFactory.getLogger(ConversationService.class);

    private final ConversationRepository conversationRepository;
    private final ProfileHelper profileHelper;
    private final MessageRepository messageRepository;
    private final ProfileRepository profileRepository;

    public ConversationService(
        ConversationRepository conversationRepository,
        ProfileHelper profileHelper,
        MessageRepository messageRepository,
        ProfileRepository profileRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.profileHelper = profileHelper;
        this.messageRepository = messageRepository;
        this.profileRepository = profileRepository;
    }

    public Mono<List<ConversationDTO>> getAllMyConversations() {
        return profileHelper
            .getCurrentProfile()
            .flatMap(me ->
                conversationRepository
                    .findConversations(me.getId())
                    .collectList()
                    .flatMap(convos -> {
                        if (convos.isEmpty()) return Mono.just(List.of());

                        Long[] convoIds = convos.stream().map(Conversation::getId).toArray(Long[]::new);

                        Long[] otherIds = convos
                            .stream()
                            .map(c -> otherParticipantId(c, me.getId()))
                            .filter(Objects::nonNull)
                            .distinct()
                            .toArray(Long[]::new);

                        Mono<List<Message>> lastMsgsMono = messageRepository.findLatestForEachConversation(convoIds).collectList();

                        Mono<Map<Long, String>> namesMono = otherIds.length == 0
                            ? Mono.just(Map.of())
                            : profileRepository.findAllByIds(otherIds).collectMap(Profile::getId, this::displayNameSafe);

                        return Mono.zip(lastMsgsMono, namesMono)
                            .map(tuple -> processConversations(convos, tuple.getT1(), tuple.getT2(), me.getId()))
                            .doOnError(e -> LOG.error("getAllMyConversations failed", e));
                    })
            );
    }

    private List<ConversationDTO> processConversations(
        List<Conversation> conversations,
        List<Message> lastMessages,
        Map<Long, String> namesById,
        Long myId
    ) {
        Map<Long, Message> lastByConvoId = lastMessages
            .stream()
            .filter(m -> m.getConversationId() != null)
            .collect(Collectors.toMap(Message::getConversationId, m -> m, (a, _) -> a));

        List<ConversationDTO> out = new ArrayList<>(conversations.size());

        for (Conversation c : conversations) {
            Long otherId = otherParticipantId(c, myId);
            Message last = lastByConvoId.get(c.getId());

            out.add(
                new ConversationDTO(
                    c.getId(),
                    last != null ? last.getBody() : null,
                    true,
                    otherId != null ? namesById.getOrDefault(otherId, "Unknown") : "Unknown"
                )
            );
        }

        return out;
    }

    private Long otherParticipantId(Conversation c, Long myId) {
        Long a = c.getParticipantAId();
        Long b = c.getParticipantBId();
        if (myId.equals(a)) return b;
        if (myId.equals(b)) return a;
        return null;
    }

    private String displayNameSafe(Profile p) {
        String first = p.getFirstName();
        String last = p.getLastName();
        String full = (first == null ? "" : first.trim()) + " " + (last == null ? "" : last.trim());
        full = full.trim();
        return full.isEmpty() ? "Unknown" : full;
    }
}
