package com.freelance.app.repository;

import com.freelance.app.domain.Offer;
import com.freelance.app.domain.Tag;
import com.freelance.app.domain.criteria.OfferCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.OfferRowMapper;
import com.freelance.app.repository.rowmapper.OfferTypeRowMapper;
import com.freelance.app.repository.rowmapper.ProfileRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Offer entity.
 */
@SuppressWarnings("unused")
class OfferRepositoryInternalImpl extends SimpleR2dbcRepository<Offer, Long> implements OfferRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProfileRowMapper profileMapper;
    private final OfferTypeRowMapper offertypeMapper;
    private final OfferRowMapper offerMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("offer", EntityManager.ENTITY_ALIAS);
    private static final Table ownerTable = Table.aliased("profile", "owner");
    private static final Table offertypeTable = Table.aliased("offer_type", "offertype");

    private static final EntityManager.LinkTable tagLink = new EntityManager.LinkTable("rel_offer__tag", "offer_id", "tag_id");

    public OfferRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProfileRowMapper profileMapper,
        OfferTypeRowMapper offertypeMapper,
        OfferRowMapper offerMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Offer.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.profileMapper = profileMapper;
        this.offertypeMapper = offertypeMapper;
        this.offerMapper = offerMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Offer> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Offer> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OfferSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProfileSqlHelper.getColumns(ownerTable, "owner"));
        columns.addAll(OfferTypeSqlHelper.getColumns(offertypeTable, "offertype"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(ownerTable)
            .on(Column.create("owner_id", entityTable))
            .equals(Column.create("id", ownerTable))
            .leftOuterJoin(offertypeTable)
            .on(Column.create("offertype_id", entityTable))
            .equals(Column.create("id", offertypeTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Offer.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Offer> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Offer> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Offer> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Offer> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Offer> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Offer process(Row row, RowMetadata metadata) {
        Offer entity = offerMapper.apply(row, "e");
        entity.setOwner(profileMapper.apply(row, "owner"));
        entity.setOffertype(offertypeMapper.apply(row, "offertype"));
        return entity;
    }

    @Override
    public <S extends Offer> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Offer> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager.updateLinkTable(tagLink, entity.getId(), entity.getTags().stream().map(Tag::getId)).then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(tagLink, entityId);
    }

    @Override
    public Flux<Offer> findByCriteria(OfferCriteria offerCriteria, Pageable page) {
        return createQuery(page, buildConditions(offerCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(OfferCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(OfferCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getDescription() != null) {
                builder.buildFilterConditionForField(criteria.getDescription(), entityTable.column("description"));
            }
            if (criteria.getRating() != null) {
                builder.buildFilterConditionForField(criteria.getRating(), entityTable.column("rating"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getVisibility() != null) {
                builder.buildFilterConditionForField(criteria.getVisibility(), entityTable.column("visibility"));
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
            if (criteria.getOwnerId() != null) {
                builder.buildFilterConditionForField(criteria.getOwnerId(), ownerTable.column("id"));
            }
            if (criteria.getOffertypeId() != null) {
                builder.buildFilterConditionForField(criteria.getOffertypeId(), offertypeTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
