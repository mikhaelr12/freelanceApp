package com.freelance.app.websocket;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class ConversationBroadcaster {

    private final ConcurrentHashMap<Long, Sinks.Many<String>> sinks = new ConcurrentHashMap<>();

    private Sinks.Many<String> sink(Long userId) {
        return sinks.computeIfAbsent(userId, _ -> Sinks.many().multicast().onBackpressureBuffer());
    }

    public Flux<String> flux(Long userId) {
        return sink(userId).asFlux();
    }

    public void publish(Long userId, String json) {
        sink(userId).tryEmitNext(json);
    }
}
