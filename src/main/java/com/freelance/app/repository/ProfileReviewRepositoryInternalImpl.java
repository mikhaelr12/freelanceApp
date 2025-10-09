package com.freelance.app.repository;

import com.freelance.app.domain.ProfileReview;
import com.freelance.app.domain.criteria.ProfileReviewCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.ProfileReviewRowMapper;
import com.freelance.app.repository.rowmapper.ProfileRowMapper;
import com.freelance.app.repository.sqlhelper.ProfileReviewSqlHelper;
import com.freelance.app.repository.sqlhelper.ProfileSqlHelper;
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
 * Spring Data R2DBC custom repository implementation for the ProfileReview entity.
 */
@SuppressWarnings("unused")
class ProfileReviewRepositoryInternalImpl extends SimpleR2dbcRepository<ProfileReview, Long> implements ProfileReviewRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProfileRowMapper profileMapper;
    private final ProfileReviewRowMapper profilereviewMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("profile_review", EntityManager.ENTITY_ALIAS);
    private static final Table reviewerTable = Table.aliased("profile", "reviewer");
    private static final Table revieweeTable = Table.aliased("profile", "reviewee");

    public ProfileReviewRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProfileRowMapper profileMapper,
        ProfileReviewRowMapper profilereviewMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ProfileReview.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.profileMapper = profileMapper;
        this.profilereviewMapper = profilereviewMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<ProfileReview> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ProfileReview> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProfileReviewSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProfileSqlHelper.getColumns(reviewerTable, "reviewer"));
        columns.addAll(ProfileSqlHelper.getColumns(revieweeTable, "reviewee"));
        return createQuery(pageable, whereClause, columns).map(this::process);
    }

    DatabaseClient.GenericExecuteSpec createQuery(Pageable pageable, Condition whereClause, List<Expression> columns) {
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(reviewerTable)
            .on(Column.create("reviewer_id", entityTable))
            .equals(Column.create("id", reviewerTable))
            .leftOuterJoin(revieweeTable)
            .on(Column.create("reviewee_id", entityTable))
            .equals(Column.create("id", revieweeTable));
        String select = entityManager.createSelect(selectFrom, ProfileReview.class, pageable, whereClause);
        return db.sql(select);
    }

    @Override
    public Flux<ProfileReview> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ProfileReview> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ProfileReview process(Row row, RowMetadata metadata) {
        ProfileReview entity = profilereviewMapper.apply(row, "e");
        entity.setReviewer(profileMapper.apply(row, "reviewer"));
        entity.setReviewee(profileMapper.apply(row, "reviewee"));
        return entity;
    }

    @Override
    public <S extends ProfileReview> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<ProfileReview> findByCriteria(ProfileReviewCriteria profileReviewCriteria, Pageable page) {
        return createQuery(page, buildConditions(profileReviewCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ProfileReviewCriteria criteria) {
        return createCountQuery(buildConditions(criteria)).one();
    }

    private RowsFetchSpec<Long> createCountQuery(Condition whereClause) {
        return createQuery(null, whereClause, List.of(Functions.count(Expressions.asterisk()))).map((row, metadata) ->
            row.get(0, Long.class)
        );
    }

    private Condition buildConditions(ProfileReviewCriteria criteria) {
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
            if (criteria.getReviewerId() != null) {
                builder.buildFilterConditionForField(criteria.getReviewerId(), reviewerTable.column("id"));
            }
            if (criteria.getRevieweeId() != null) {
                builder.buildFilterConditionForField(criteria.getRevieweeId(), revieweeTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
