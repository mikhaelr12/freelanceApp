package com.freelance.app.repository;

import com.freelance.app.domain.Subcategory;
import com.freelance.app.domain.criteria.SubcategoryCriteria;
import com.freelance.app.repository.rowmapper.CategoryRowMapper;
import com.freelance.app.repository.rowmapper.ColumnConverter;
import com.freelance.app.repository.rowmapper.SubcategoryRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Subcategory entity.
 */
@SuppressWarnings("unused")
class SubcategoryRepositoryInternalImpl extends SimpleR2dbcRepository<Subcategory, Long> implements SubcategoryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CategoryRowMapper categoryMapper;
    private final SubcategoryRowMapper subcategoryMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("subcategory", EntityManager.ENTITY_ALIAS);
    private static final Table categoryTable = Table.aliased("category", "category");

    public SubcategoryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CategoryRowMapper categoryMapper,
        SubcategoryRowMapper subcategoryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Subcategory.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.categoryMapper = categoryMapper;
        this.subcategoryMapper = subcategoryMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Subcategory> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Subcategory> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = SubcategorySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CategorySqlHelper.getColumns(categoryTable, "category"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(categoryTable)
            .on(Column.create("category_id", entityTable))
            .equals(Column.create("id", categoryTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Subcategory.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Subcategory> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Subcategory> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Subcategory> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Subcategory> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Subcategory> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Subcategory process(Row row, RowMetadata metadata) {
        Subcategory entity = subcategoryMapper.apply(row, "e");
        entity.setCategory(categoryMapper.apply(row, "category"));
        return entity;
    }

    @Override
    public <S extends Subcategory> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Subcategory> findByCriteria(SubcategoryCriteria subcategoryCriteria, Pageable page) {
        return createQuery(page, buildConditions(subcategoryCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(SubcategoryCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(SubcategoryCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
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
