package com.freelance.app.repository;

import com.freelance.app.domain.Message;
import com.freelance.app.domain.criteria.MessageCriteria;
import com.freelance.app.service.dto.MessageShortDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Message entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, Long>, MessageRepositoryInternal {
    Flux<Message> findAllBy(Pageable pageable);

    @Override
    <S extends Message> @NotNull Mono<S> save(@NotNull S entity);

    @Override
    @NotNull
    Flux<Message> findAll();

    @Override
    @NotNull
    Mono<Message> findById(@NotNull Long id);

    @Override
    @NotNull
    Mono<Void> deleteById(@NotNull Long id);

    @Query(
        """
        SELECT * FROM message m
        WHERE m.conversation_id = ANY(:conversationIds)
        ORDER BY m.sent_at DESC
        LIMIT 1;
        """
    )
    Flux<Message> findLatestForEachConversation(Long[] conversationIds);
}

interface MessageRepositoryInternal {
    <S extends Message> Mono<S> save(S entity);

    Flux<Message> findAllBy(Pageable pageable);

    Flux<Message> findAll();

    Mono<Message> findById(Long id);

    Flux<Message> findByCriteria(MessageCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(MessageCriteria criteria);

    Mono<Void> deleteById(Long id);

    Flux<MessageShortDTO> findAllConversationMessages(Long conversationId);
}
