package com.freelance.app.repository;

import com.freelance.app.domain.Delivery;
import com.freelance.app.domain.criteria.DeliveryCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.DeliveryRowMapper;
import com.freelance.app.repository.rowmapper.FileObjectRowMapper;
import com.freelance.app.repository.rowmapper.OrderRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Delivery entity.
 */
@SuppressWarnings("unused")
class DeliveryRepositoryInternalImpl extends SimpleR2dbcRepository<Delivery, Long> implements DeliveryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OrderRowMapper orderMapper;
    private final FileObjectRowMapper fileobjectMapper;
    private final DeliveryRowMapper deliveryMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("delivery", EntityManager.ENTITY_ALIAS);
    private static final Table orderTable = Table.aliased("jhi_order", "e_order");
    private static final Table fileTable = Table.aliased("file_object", "e_file");

    public DeliveryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrderRowMapper orderMapper,
        FileObjectRowMapper fileobjectMapper,
        DeliveryRowMapper deliveryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Delivery.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.orderMapper = orderMapper;
        this.fileobjectMapper = fileobjectMapper;
        this.deliveryMapper = deliveryMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Delivery> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Delivery> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = DeliverySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OrderSqlHelper.getColumns(orderTable, "order"));
        columns.addAll(FileObjectSqlHelper.getColumns(fileTable, "file"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable))
            .leftOuterJoin(fileTable)
            .on(Column.create("file_id", entityTable))
            .equals(Column.create("id", fileTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Delivery.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Delivery> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Delivery> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Delivery> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Delivery> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Delivery> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Delivery process(Row row, RowMetadata metadata) {
        Delivery entity = deliveryMapper.apply(row, "e");
        entity.setOrder(orderMapper.apply(row, "order"));
        entity.setFile(fileobjectMapper.apply(row, "file"));
        return entity;
    }

    @Override
    public <S extends Delivery> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Delivery> findByCriteria(DeliveryCriteria deliveryCriteria, Pageable page) {
        return createQuery(page, buildConditions(deliveryCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(DeliveryCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(DeliveryCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getNote() != null) {
                builder.buildFilterConditionForField(criteria.getNote(), entityTable.column("note"));
            }
            if (criteria.getDeliveredAt() != null) {
                builder.buildFilterConditionForField(criteria.getDeliveredAt(), entityTable.column("delivered_at"));
            }
            if (criteria.getOrderId() != null) {
                builder.buildFilterConditionForField(criteria.getOrderId(), orderTable.column("id"));
            }
            if (criteria.getFileId() != null) {
                builder.buildFilterConditionForField(criteria.getFileId(), fileTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
