package com.freelance.app.repository;

import com.freelance.app.domain.Conversation;
import com.freelance.app.domain.criteria.ConversationCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.ConversationRowMapper;
import com.freelance.app.repository.rowmapper.OrderRowMapper;
import com.freelance.app.repository.sqlhelper.ConversationSqlHelper;
import com.freelance.app.repository.sqlhelper.OrderSqlHelper;
import com.freelance.app.service.dto.ConversationDTO;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
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
 * Spring Data R2DBC custom repository implementation for the Conversation entity.
 */
@SuppressWarnings("unused")
class ConversationRepositoryInternalImpl extends SimpleR2dbcRepository<Conversation, Long> implements ConversationRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OrderRowMapper orderMapper;
    private final ConversationRowMapper conversationMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("conversation", EntityManager.ENTITY_ALIAS);
    private static final Table orderTable = Table.aliased("jhi_order", "e_order");

    public ConversationRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrderRowMapper orderMapper,
        ConversationRowMapper conversationMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Conversation.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.orderMapper = orderMapper;
        this.conversationMapper = conversationMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Conversation> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Conversation> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ConversationSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OrderSqlHelper.getColumns(orderTable, "order"));
        return createQuery(pageable, whereClause, columns).map(this::process);
    }

    DatabaseClient.GenericExecuteSpec createQuery(Pageable pageable, Condition whereClause, List<Expression> columns) {
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable));
        String select = entityManager.createSelect(selectFrom, Conversation.class, pageable, whereClause);
        return db.sql(select);
    }

    @Override
    public Flux<Conversation> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Conversation> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Conversation process(Row row, RowMetadata metadata) {
        Conversation entity = conversationMapper.apply(row, "e");
        entity.setOrder(orderMapper.apply(row, "order"));
        return entity;
    }

    @Override
    public <S extends Conversation> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<ConversationDTO> findByCriteria(ConversationCriteria conversationCriteria, Pageable page) {
        List<Expression> columns = ConversationSqlHelper.getColumnsDTO(entityTable, EntityManager.ENTITY_ALIAS);
        return createQuery(page, buildConditions(conversationCriteria), columns)
            .map((row, rowMetadata) -> conversationMapper.applyDto(row, "e"))
            .all();
    }

    @Override
    public Mono<Long> countByCriteria(ConversationCriteria criteria) {
        return createCountQuery(buildConditions(criteria)).one();
    }

    @Override
    public Mono<ConversationDTO> findDTOById(Long id) {
        List<Expression> columns = ConversationSqlHelper.getColumnsDTO(entityTable, EntityManager.ENTITY_ALIAS);
        Condition whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause, columns).map((row, rowMetadata) -> conversationMapper.applyDto(row, "e")).one();
    }

    private RowsFetchSpec<Long> createCountQuery(Condition whereClause) {
        return createQuery(null, whereClause, List.of(Functions.count(Expressions.asterisk()))).map((row, metadata) ->
            row.get(0, Long.class)
        );
    }

    private Condition buildConditions(ConversationCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getCreatedAt() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedAt(), entityTable.column("created_at"));
            }
            if (criteria.getOrderId() != null) {
                builder.buildFilterConditionForField(criteria.getOrderId(), orderTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
