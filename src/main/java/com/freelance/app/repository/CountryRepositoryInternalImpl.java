package com.freelance.app.repository;

import com.freelance.app.domain.Country;
import com.freelance.app.domain.criteria.CountryCriteria;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.CountryRowMapper;
import com.freelance.app.repository.sqlhelper.CountrySqlHelper;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Country entity.
 */
@SuppressWarnings("unused")
class CountryRepositoryInternalImpl extends SimpleR2dbcRepository<Country, Long> implements CountryRepositoryInternal {

    private final DatabaseClient db;
    private final EntityManager entityManager;
    private final CountryRowMapper countryMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("country", EntityManager.ENTITY_ALIAS);

    public CountryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CountryRowMapper countryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Country.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.entityManager = entityManager;
        this.countryMapper = countryMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Country> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    DatabaseClient.GenericExecuteSpec createQuery(Pageable pageable, Condition condition, List<Expression> columns) {
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        String select = entityManager.createSelect(selectFrom, Country.class, pageable, condition);
        return db.sql(select);
    }

    RowsFetchSpec<Country> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CountrySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        return createQuery(pageable, whereClause, columns).map(this::process);
    }

    @Override
    public @NotNull Flux<Country> findAll() {
        return findAllBy(null);
    }

    @Override
    public @NotNull Mono<Country> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Country process(Row row, RowMetadata metadata) {
        return countryMapper.apply(row, "e");
    }

    @Override
    public <S extends Country> @NotNull Mono<S> save(@NotNull S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Country> findByCriteria(CountryCriteria countryCriteria, Pageable page) {
        return createQuery(page, buildConditions(countryCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(CountryCriteria criteria) {
        return createQuery(null, buildConditions(criteria), List.of(Functions.count(Expressions.asterisk())))
            .map((row, rowMetadata) -> row.get(0, Long.class))
            .one();
    }

    private Condition buildConditions(CountryCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getIso2() != null) {
                builder.buildFilterConditionForField(criteria.getIso2(), entityTable.column("iso_2"));
            }
            if (criteria.getIso3() != null) {
                builder.buildFilterConditionForField(criteria.getIso3(), entityTable.column("iso_3"));
            }
            if (criteria.getRegion() != null) {
                builder.buildFilterConditionForField(criteria.getRegion(), entityTable.column("region"));
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
            if (criteria.getActive() != null) {
                builder.buildFilterConditionForField(criteria.getActive(), entityTable.column("active"));
            }
        }
        return builder.buildConditions();
    }
}
