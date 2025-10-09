package com.freelance.app.repository;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.criteria.FavoriteOfferCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.FavoriteOfferRowMapper;
import com.freelance.app.repository.rowmapper.OfferRowMapper;
import com.freelance.app.repository.rowmapper.ProfileRowMapper;
import com.freelance.app.repository.sqlhelper.FavoriteOfferSqlHelper;
import com.freelance.app.repository.sqlhelper.OfferSqlHelper;
import com.freelance.app.repository.sqlhelper.ProfileSqlHelper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
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
 * Spring Data R2DBC custom repository implementation for the FavoriteOffer entity.
 */
@SuppressWarnings("unused")
class FavoriteOfferRepositoryInternalImpl extends SimpleR2dbcRepository<FavoriteOffer, Long> implements FavoriteOfferRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProfileRowMapper profileMapper;
    private final OfferRowMapper offerMapper;
    private final FavoriteOfferRowMapper favoriteofferMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("favorite_offer", EntityManager.ENTITY_ALIAS);
    private static final Table profileTable = Table.aliased("profile", "e_profile");
    private static final Table offerTable = Table.aliased("offer", "offer");

    public FavoriteOfferRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProfileRowMapper profileMapper,
        OfferRowMapper offerMapper,
        FavoriteOfferRowMapper favoriteofferMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(FavoriteOffer.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.profileMapper = profileMapper;
        this.offerMapper = offerMapper;
        this.favoriteofferMapper = favoriteofferMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<FavoriteOffer> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<FavoriteOffer> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = FavoriteOfferSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProfileSqlHelper.getColumns(profileTable, "profile"));
        columns.addAll(OfferSqlHelper.getColumns(offerTable, "offer"));
        return createQuery(pageable, whereClause, columns).map(this::process);
    }

    DatabaseClient.GenericExecuteSpec createQuery(Pageable pageable, Condition whereClause, List<Expression> columns) {
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(profileTable)
            .on(Column.create("profile_id", entityTable))
            .equals(Column.create("id", profileTable))
            .leftOuterJoin(offerTable)
            .on(Column.create("offer_id", entityTable))
            .equals(Column.create("id", offerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, FavoriteOffer.class, pageable, whereClause);
        return db.sql(select);
    }

    @Override
    public Flux<FavoriteOffer> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<FavoriteOffer> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private FavoriteOffer process(Row row, RowMetadata metadata) {
        FavoriteOffer entity = favoriteofferMapper.apply(row, "e");
        entity.setProfile(profileMapper.apply(row, "profile"));
        entity.setOffer(offerMapper.apply(row, "offer"));
        return entity;
    }

    @Override
    public <S extends FavoriteOffer> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<FavoriteOffer> findByCriteria(FavoriteOfferCriteria favoriteOfferCriteria, Pageable page) {
        return createQuery(page, buildConditions(favoriteOfferCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(FavoriteOfferCriteria criteria) {
        return createCountQuery(buildConditions(criteria)).one();
    }

    private RowsFetchSpec<Long> createCountQuery(Condition whereClause) {
        return createQuery(null, whereClause, List.of(Functions.count(Expressions.asterisk()))).map((row, metadata) ->
            row.get(0, Long.class)
        );
    }

    private Condition buildConditions(FavoriteOfferCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getCreatedAt() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedAt(), entityTable.column("created_at"));
            }
            if (criteria.getProfileId() != null) {
                builder.buildFilterConditionForField(criteria.getProfileId(), profileTable.column("id"));
            }
            if (criteria.getOfferId() != null) {
                builder.buildFilterConditionForField(criteria.getOfferId(), offerTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
