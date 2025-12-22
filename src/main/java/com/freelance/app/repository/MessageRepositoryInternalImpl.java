package com.freelance.app.repository;

import com.freelance.app.domain.Message;
import com.freelance.app.domain.criteria.MessageCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.ConversationRowMapper;
import com.freelance.app.repository.rowmapper.MessageRowMapper;
import com.freelance.app.repository.rowmapper.UserRowMapper;
import com.freelance.app.repository.sqlhelper.ConversationSqlHelper;
import com.freelance.app.repository.sqlhelper.MessageSqlHelper;
import com.freelance.app.repository.sqlhelper.UserSqlHelper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.*;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Message entity.
 */
@SuppressWarnings("unused")
class MessageRepositoryInternalImpl extends SimpleR2dbcRepository<Message, Long> implements MessageRepositoryInternal {

    private final DatabaseClient db;
    private final EntityManager entityManager;
    private final ConversationRowMapper conversationMapper;
    private final UserRowMapper userMapper;
    private final MessageRowMapper messageMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("message", EntityManager.ENTITY_ALIAS);
    private static final Table conversationTable = Table.aliased("conversation", "conversation");
    private static final Table senderTable = Table.aliased("jhi_user", "sender");

    public MessageRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ConversationRowMapper conversationMapper,
        UserRowMapper userMapper,
        MessageRowMapper messageMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Message.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.entityManager = entityManager;
        this.conversationMapper = conversationMapper;
        this.userMapper = userMapper;
        this.messageMapper = messageMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Message> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    DatabaseClient.GenericExecuteSpec createQuery(Pageable pageable, Condition condition, List<Expression> columns) {
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(conversationTable)
            .on(Column.create("conversation_id", entityTable))
            .equals(Column.create("id", conversationTable))
            .leftOuterJoin(senderTable)
            .on(Column.create("sender_id", entityTable))
            .equals(Column.create("id", senderTable));
        String select = entityManager.createSelect(selectFrom, Message.class, pageable, condition);
        return db.sql(select);
    }

    RowsFetchSpec<Message> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MessageSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ConversationSqlHelper.getColumns(conversationTable, "conversation"));
        columns.addAll(UserSqlHelper.getColumns(senderTable, "sender"));
        return createQuery(pageable, whereClause, columns).map(this::process);
    }

    @Override
    public @NotNull Flux<Message> findAll() {
        return findAllBy(null);
    }

    @Override
    public @NotNull Mono<Message> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Message> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Message> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Message> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Message process(Row row, RowMetadata metadata) {
        Message entity = messageMapper.apply(row, "e");
        entity.setConversation(conversationMapper.apply(row, "conversation"));
        entity.setSender(userMapper.apply(row, "sender"));
        return entity;
    }

    @Override
    public <S extends Message> @NotNull Mono<S> save(@NotNull S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Message> findByCriteria(MessageCriteria messageCriteria, Pageable page) {
        return createQuery(page, buildConditions(messageCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(MessageCriteria criteria) {
        return createQuery(null, buildConditions(criteria), List.of(Functions.count(Expressions.asterisk())))
            .map((row, rowMetadata) -> row.get(0, Long.class))
            .one();
    }

    private Condition buildConditions(MessageCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getBody() != null) {
                builder.buildFilterConditionForField(criteria.getBody(), entityTable.column("body"));
            }
            if (criteria.getSentAt() != null) {
                builder.buildFilterConditionForField(criteria.getSentAt(), entityTable.column("sent_at"));
            }
            if (criteria.getConversationId() != null) {
                builder.buildFilterConditionForField(criteria.getConversationId(), conversationTable.column("id"));
            }
            if (criteria.getSenderId() != null) {
                builder.buildFilterConditionForField(criteria.getSenderId(), senderTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
