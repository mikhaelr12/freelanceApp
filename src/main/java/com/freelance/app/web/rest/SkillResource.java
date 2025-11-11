package com.freelance.app.web.rest;

import com.freelance.app.domain.criteria.SkillCriteria;
import com.freelance.app.repository.SkillRepository;
import com.freelance.app.service.SkillService;
import com.freelance.app.service.dto.SkillDTO;
import com.freelance.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.freelance.app.domain.Skill}.
 */
@RestController
@RequestMapping("/api/skills")
public class SkillResource {

    private static final Logger LOG = LoggerFactory.getLogger(SkillResource.class);

    private static final String ENTITY_NAME = "skill";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SkillService skillService;

    private final SkillRepository skillRepository;

    public SkillResource(SkillService skillService, SkillRepository skillRepository) {
        this.skillService = skillService;
        this.skillRepository = skillRepository;
    }

    /**
     * {@code POST  /skills} : Create a new skill.
     *
     * @param skillDTO the skillDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new skillDTO, or with status {@code 400 (Bad Request)} if the skill has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SkillDTO>> createSkill(@Valid @RequestBody SkillDTO skillDTO) {
        LOG.debug("REST request to save Skill : {}", skillDTO);
        if (skillDTO.getId() != null) {
            throw new BadRequestAlertException("A new skill cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return skillService
            .save(skillDTO)
            .handle((result, sink) -> {
                try {
                    sink.next(
                        ResponseEntity.created(new URI("/api/skills/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
                } catch (URISyntaxException e) {
                    sink.error(new RuntimeException(e));
                }
            });
    }

    /**
     * {@code PUT  /skills/:id} : Updates an existing skill.
     *
     * @param id the id of the skillDTO to save.
     * @param skillDTO the skillDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated skillDTO,
     * or with status {@code 400 (Bad Request)} if the skillDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the skillDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SkillDTO>> updateSkill(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SkillDTO skillDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Skill : {}, {}", id, skillDTO);
        if (skillDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, skillDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return skillRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return skillService
                    .update(skillDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /skills/:id} : Partial updates given fields of an existing skill, field will ignore if it is null
     *
     * @param id the id of the skillDTO to save.
     * @param skillDTO the skillDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated skillDTO,
     * or with status {@code 400 (Bad Request)} if the skillDTO is not valid,
     * or with status {@code 404 (Not Found)} if the skillDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the skillDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SkillDTO>> partialUpdateSkill(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SkillDTO skillDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Skill partially : {}, {}", id, skillDTO);
        if (skillDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, skillDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return skillRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<SkillDTO> result = skillService.partialUpdate(skillDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /skills} : get all the skills.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of skills in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<SkillDTO>>> getAllSkills(
        SkillCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Skills by criteria: {}", criteria);
        return skillService
            .countByCriteria(criteria)
            .zipWith(skillService.findByCriteria(criteria, pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /skills/count} : count all the skills.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countSkills(SkillCriteria criteria) {
        LOG.debug("REST request to count Skills by criteria: {}", criteria);
        return skillService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /skills/:id} : get the "id" skill.
     *
     * @param id the id of the skillDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the skillDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SkillDTO>> getSkill(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Skill : {}", id);
        Mono<SkillDTO> skillDTO = skillService.findOne(id);
        return ResponseUtil.wrapOrNotFound(skillDTO);
    }

    /**
     * {@code DELETE  /skills/:id} : delete the "id" skill.
     *
     * @param id the id of the skillDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSkill(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Skill : {}", id);
        return skillService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
