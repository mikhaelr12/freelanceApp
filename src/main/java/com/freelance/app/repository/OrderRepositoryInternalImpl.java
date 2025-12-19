package com.freelance.app.repository;

import com.freelance.app.domain.Order;
import com.freelance.app.domain.criteria.OrderCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.OfferPackageRowMapper;
import com.freelance.app.repository.rowmapper.OrderRowMapper;
import com.freelance.app.repository.rowmapper.UserRowMapper;
import com.freelance.app.repository.sqlhelper.UserSqlHelper;
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
 * Spring Data R2DBC custom repository implementation for the Order entity.
 */
@SuppressWarnings("unused")
class OrderRepositoryInternalImpl extends SimpleR2dbcRepository<Order, Long> implements OrderRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final OfferPackageRowMapper offerpackageMapper;
    private final OrderRowMapper orderMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("jhi_order", EntityManager.ENTITY_ALIAS);
    private static final Table buyerTable = Table.aliased("jhi_user", "buyer");
    private static final Table sellerTable = Table.aliased("jhi_user", "seller");
    private static final Table offerpackageTable = Table.aliased("offer_package", "offerpackage");

    public OrderRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        OfferPackageRowMapper offerpackageMapper,
        OrderRowMapper orderMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Order.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.offerpackageMapper = offerpackageMapper;
        this.orderMapper = orderMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Order> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Order> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OrderSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(buyerTable, "buyer"));
        columns.addAll(UserSqlHelper.getColumns(sellerTable, "seller"));
        columns.addAll(OfferPackageSqlHelper.getColumns(offerpackageTable, "offerpackage"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(buyerTable)
            .on(Column.create("buyer_id", entityTable))
            .equals(Column.create("id", buyerTable))
            .leftOuterJoin(sellerTable)
            .on(Column.create("seller_id", entityTable))
            .equals(Column.create("id", sellerTable))
            .leftOuterJoin(offerpackageTable)
            .on(Column.create("offerpackage_id", entityTable))
            .equals(Column.create("id", offerpackageTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Order.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Order> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Order> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Order> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Order> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Order> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Order process(Row row, RowMetadata metadata) {
        Order entity = orderMapper.apply(row, "e");
        entity.setBuyer(userMapper.apply(row, "buyer"));
        entity.setSeller(userMapper.apply(row, "seller"));
        entity.setOfferpackage(offerpackageMapper.apply(row, "offerpackage"));
        return entity;
    }

    @Override
    public <S extends Order> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Order> findByCriteria(OrderCriteria orderCriteria, Pageable page) {
        return createQuery(page, buildConditions(orderCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(OrderCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(OrderCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getTotalAmount() != null) {
                builder.buildFilterConditionForField(criteria.getTotalAmount(), entityTable.column("total_amount"));
            }
            if (criteria.getCurrency() != null) {
                builder.buildFilterConditionForField(criteria.getCurrency(), entityTable.column("currency"));
            }
            if (criteria.getCreatedDate() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedDate(), entityTable.column("created_date"));
            }
            if (criteria.getLastModifiedDate() != null) {
                builder.buildFilterConditionForField(criteria.getLastModifiedDate(), entityTable.column("last_modified_date"));
            }
            if (criteria.getCreatedBy() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedBy(), entityTable.column("created_by"));
            }
            if (criteria.getLastModifiedBy() != null) {
                builder.buildFilterConditionForField(criteria.getLastModifiedBy(), entityTable.column("last_modified_by"));
            }
            if (criteria.getBuyerId() != null) {
                builder.buildFilterConditionForField(criteria.getBuyerId(), buyerTable.column("id"));
            }
            if (criteria.getSellerId() != null) {
                builder.buildFilterConditionForField(criteria.getSellerId(), sellerTable.column("id"));
            }
            if (criteria.getOfferpackageId() != null) {
                builder.buildFilterConditionForField(criteria.getOfferpackageId(), offerpackageTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
