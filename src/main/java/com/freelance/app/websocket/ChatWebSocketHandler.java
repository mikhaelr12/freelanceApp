package com.freelance.app.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.domain.Conversation;
import com.freelance.app.domain.Message;
import com.freelance.app.domain.Profile;
import com.freelance.app.repository.ConversationRepository;
import com.freelance.app.repository.MessageRepository;
import com.freelance.app.util.ProfileHelper;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper om;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ConversationBroadcaster broadcaster;
    private final ReactiveJwtDecoder jwtDecoder;
    private final ProfileHelper profileHelper;

    public ChatWebSocketHandler(
        ObjectMapper om,
        ConversationRepository conversationRepository,
        MessageRepository messageRepository,
        ConversationBroadcaster broadcaster,
        ReactiveJwtDecoder jwtDecoder,
        ProfileHelper profileHelper
    ) {
        this.om = om;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.broadcaster = broadcaster;
        this.jwtDecoder = jwtDecoder;
        this.profileHelper = profileHelper;
    }

    record WsIn(String type, Long receiverId, String body, String clientMsgId) {}

    record WsOut(
        String type,
        Long conversationId,
        Long id,
        Long senderId,
        Long receiverId,
        String body,
        Instant sentAt,
        String clientMsgId
    ) {}

    @Override
    public @NotNull Mono<Void> handle(@NotNull WebSocketSession session) {
        Mono<Long> senderIdMono = loginFromSession(session).flatMap(profileHelper::getProfileFromUserLogin).map(Profile::getId);

        return senderIdMono
            .flatMap(senderId -> {
                Flux<WebSocketMessage> outgoing = broadcaster.flux(senderId).map(session::textMessage);

                Mono<Void> incoming = session
                    .receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .flatMap(text -> {
                        if (text == null || text.isBlank()) return Mono.empty();
                        return Mono.fromCallable(() -> om.readValue(text, WsIn.class)).onErrorResume(_ -> Mono.empty());
                    })
                    .flatMap(req -> {
                        if (!"chat.send".equals(req.type())) return Mono.empty();

                        Long receiverId = req.receiverId();
                        if (receiverId == null || receiverId.equals(senderId)) return Mono.empty();

                        String body = req.body();
                        if (body == null || body.isBlank()) return Mono.empty();

                        return getOrCreateConversation(senderId, receiverId).flatMap(conv -> {
                            Message msg = new Message()
                                .conversationId(conv.getId())
                                .senderId(senderId)
                                .receiverId(receiverId)
                                .body(body)
                                .sentAt(Instant.now());

                            return messageRepository
                                .save(msg)
                                .flatMap(saved ->
                                    Mono.fromCallable(() ->
                                        om.writeValueAsString(
                                            new WsOut(
                                                "chat.message",
                                                conv.getId(),
                                                saved.getId(),
                                                senderId,
                                                receiverId,
                                                saved.getBody(),
                                                saved.getSentAt(),
                                                req.clientMsgId()
                                            )
                                        )
                                    )
                                )
                                .doOnNext(jsonOut -> {
                                    broadcaster.publish(senderId, jsonOut);
                                    broadcaster.publish(receiverId, jsonOut);
                                })
                                .then();
                        });
                    })
                    .onErrorResume(_ -> Mono.empty())
                    .then();

                return session.send(outgoing).and(incoming).onErrorResume(_ -> session.close());
            })
            .onErrorResume(_ -> session.close());
    }

    private Mono<Conversation> getOrCreateConversation(Long a, Long b) {
        return conversationRepository
            .findBetween(a, b)
            .switchIfEmpty(
                conversationRepository.save(new Conversation().createdAt(Instant.now()).id(null).participantAId(a).participantBId(b))
            );
    }

    private Mono<String> loginFromSession(WebSocketSession session) {
        String token = extractQueryParam(session.getHandshakeInfo().getUri().getQuery());
        if (token == null || token.isBlank()) return Mono.error(new IllegalAccessException("Missing token"));
        return jwtDecoder.decode(token).map(JwtClaimAccessor::getSubject);
    }

    private static String extractQueryParam(String query) {
        if (query == null || query.isBlank()) return null;
        for (String p : query.split("&")) {
            int idx = p.indexOf('=');
            if (idx > 0 && "token".equals(p.substring(0, idx))) return p.substring(idx + 1);
        }
        return null;
    }
}
