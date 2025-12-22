package com.freelance.app.repository;

import com.freelance.app.domain.Skill;
import com.freelance.app.domain.criteria.SkillCriteria;
import com.freelance.app.repository.rowmapper.CategoryRowMapper;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.SkillRowMapper;
import com.freelance.app.repository.sqlhelper.CategorySqlHelper;
import com.freelance.app.repository.sqlhelper.SkillSqlHelper;
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
 * Spring Data R2DBC custom repository implementation for the Skill entity.
 */
@SuppressWarnings("unused")
class SkillRepositoryInternalImpl extends SimpleR2dbcRepository<Skill, Long> implements SkillRepositoryInternal {

    private final DatabaseClient db;
    private final EntityManager entityManager;
    private final CategoryRowMapper categoryMapper;
    private final SkillRowMapper skillMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("skill", EntityManager.ENTITY_ALIAS);
    private static final Table categoryTable = Table.aliased("category", "category");

    public SkillRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CategoryRowMapper categoryMapper,
        SkillRowMapper skillMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Skill.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.entityManager = entityManager;
        this.categoryMapper = categoryMapper;
        this.skillMapper = skillMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Skill> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Skill> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = SkillSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CategorySqlHelper.getColumns(categoryTable, "category"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(categoryTable)
            .on(Column.create("category_id", entityTable))
            .equals(Column.create("id", categoryTable));
        String select = entityManager.createSelect(selectFrom, Skill.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public @NotNull Flux<Skill> findAll() {
        return findAllBy(null);
    }

    @Override
    public @NotNull Mono<Skill> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Skill> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Skill> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Skill> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Skill process(Row row, RowMetadata metadata) {
        Skill entity = skillMapper.apply(row, "e");
        entity.setCategory(categoryMapper.apply(row, "category"));
        return entity;
    }

    @Override
    public <S extends Skill> @NotNull Mono<S> save(@NotNull S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Skill> findByCriteria(SkillCriteria skillCriteria, Pageable page) {
        return createQuery(page, buildConditions(skillCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(SkillCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(SkillCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
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
            if (criteria.getCategoryId() != null) {
                builder.buildFilterConditionForField(criteria.getCategoryId(), categoryTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
