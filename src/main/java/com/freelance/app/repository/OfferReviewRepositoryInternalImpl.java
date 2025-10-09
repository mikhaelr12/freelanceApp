package com.freelance.app.repository;

import com.freelance.app.domain.OfferReview;
import com.freelance.app.domain.criteria.OfferReviewCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.OfferReviewRowMapper;
import com.freelance.app.repository.rowmapper.OfferRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the OfferReview entity.
 */
@SuppressWarnings("unused")
class OfferReviewRepositoryInternalImpl extends SimpleR2dbcRepository<OfferReview, Long> implements OfferReviewRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OfferRowMapper offerMapper;
    private final ProfileRowMapper profileMapper;
    private final OfferReviewRowMapper offerreviewMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("offer_review", EntityManager.ENTITY_ALIAS);
    private static final Table offerTable = Table.aliased("offer", "offer");
    private static final Table reviewerTable = Table.aliased("profile", "reviewer");

    public OfferReviewRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OfferRowMapper offerMapper,
        ProfileRowMapper profileMapper,
        OfferReviewRowMapper offerreviewMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(OfferReview.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.offerMapper = offerMapper;
        this.profileMapper = profileMapper;
        this.offerreviewMapper = offerreviewMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<OfferReview> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<OfferReview> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OfferReviewSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OfferSqlHelper.getColumns(offerTable, "offer"));
        columns.addAll(ProfileSqlHelper.getColumns(reviewerTable, "reviewer"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(offerTable)
            .on(Column.create("offer_id", entityTable))
            .equals(Column.create("id", offerTable))
            .leftOuterJoin(reviewerTable)
            .on(Column.create("reviewer_id", entityTable))
            .equals(Column.create("id", reviewerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, OfferReview.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<OfferReview> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<OfferReview> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<OfferReview> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<OfferReview> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<OfferReview> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private OfferReview process(Row row, RowMetadata metadata) {
        OfferReview entity = offerreviewMapper.apply(row, "e");
        entity.setOffer(offerMapper.apply(row, "offer"));
        entity.setReviewer(profileMapper.apply(row, "reviewer"));
        return entity;
    }

    @Override
    public <S extends OfferReview> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<OfferReview> findByCriteria(OfferReviewCriteria offerReviewCriteria, Pageable page) {
        return createQuery(page, buildConditions(offerReviewCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(OfferReviewCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(OfferReviewCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getText() != null) {
                builder.buildFilterConditionForField(criteria.getText(), entityTable.column("text"));
            }
            if (criteria.getRating() != null) {
                builder.buildFilterConditionForField(criteria.getRating(), entityTable.column("rating"));
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
            if (criteria.getOfferId() != null) {
                builder.buildFilterConditionForField(criteria.getOfferId(), offerTable.column("id"));
            }
            if (criteria.getReviewerId() != null) {
                builder.buildFilterConditionForField(criteria.getReviewerId(), reviewerTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
