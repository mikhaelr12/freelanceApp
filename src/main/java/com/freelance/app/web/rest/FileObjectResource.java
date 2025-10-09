package com.freelance.app.web.rest;

import com.freelance.app.domain.criteria.FileObjectCriteria;
import com.freelance.app.repository.FileObjectRepository;
import com.freelance.app.service.FileObjectService;
import com.freelance.app.service.dto.FileObjectDTO;
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
 * REST controller for managing {@link com.freelance.app.domain.FileObject}.
 */
@RestController
@RequestMapping("/api/file-objects")
public class FileObjectResource {

    private static final Logger LOG = LoggerFactory.getLogger(FileObjectResource.class);

    private static final String ENTITY_NAME = "fileObject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FileObjectService fileObjectService;

    private final FileObjectRepository fileObjectRepository;

    public FileObjectResource(FileObjectService fileObjectService, FileObjectRepository fileObjectRepository) {
        this.fileObjectService = fileObjectService;
        this.fileObjectRepository = fileObjectRepository;
    }

    /**
     * {@code POST  /file-objects} : Create a new fileObject.
     *
     * @param fileObjectDTO the fileObjectDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fileObjectDTO, or with status {@code 400 (Bad Request)} if the fileObject has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<FileObjectDTO>> createFileObject(@Valid @RequestBody FileObjectDTO fileObjectDTO) {
        LOG.debug("REST request to save FileObject : {}", fileObjectDTO);
        if (fileObjectDTO.getId() != null) {
            return Mono.error(new BadRequestAlertException("A new fileObject cannot already have an ID", ENTITY_NAME, "idexists"));
        }
        return fileObjectService
            .save(fileObjectDTO)
            .handle((result, sink) -> {
                try {
                    sink.next(
                        ResponseEntity.created(new URI("/api/file-objects/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
                } catch (URISyntaxException e) {
                    sink.error(new RuntimeException(e));
                }
            });
    }

    /**
     * {@code PUT  /file-objects/:id} : Updates an existing fileObject.
     *
     * @param id the id of the fileObjectDTO to save.
     * @param fileObjectDTO the fileObjectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileObjectDTO,
     * or with status {@code 400 (Bad Request)} if the fileObjectDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fileObjectDTO couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<FileObjectDTO>> updateFileObject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FileObjectDTO fileObjectDTO
    ) {
        LOG.debug("REST request to update FileObject : {}, {}", id, fileObjectDTO);
        if (fileObjectDTO.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, fileObjectDTO.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return fileObjectRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return fileObjectService
                    .update(fileObjectDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /file-objects/:id} : Partial updates given fields of an existing fileObject, field will ignore if it is null
     *
     * @param id the id of the fileObjectDTO to save.
     * @param fileObjectDTO the fileObjectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileObjectDTO,
     * or with status {@code 400 (Bad Request)} if the fileObjectDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fileObjectDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fileObjectDTO couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<FileObjectDTO>> partialUpdateFileObject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FileObjectDTO fileObjectDTO
    ) {
        LOG.debug("REST request to partial update FileObject partially : {}, {}", id, fileObjectDTO);
        if (fileObjectDTO.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, fileObjectDTO.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return fileObjectRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<FileObjectDTO> result = fileObjectService.partialUpdate(fileObjectDTO);

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
     * {@code GET  /file-objects} : get all the fileObjects.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fileObjects in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<FileObjectDTO>>> getAllFileObjects(
        FileObjectCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get FileObjects by criteria: {}", criteria);
        return fileObjectService
            .countByCriteria(criteria)
            .zipWith(fileObjectService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /file-objects/count} : count all the fileObjects.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countFileObjects(FileObjectCriteria criteria) {
        LOG.debug("REST request to count FileObjects by criteria: {}", criteria);
        return fileObjectService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /file-objects/:id} : get the "id" fileObject.
     *
     * @param id the id of the fileObjectDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fileObjectDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FileObjectDTO>> getFileObject(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FileObject : {}", id);
        Mono<FileObjectDTO> fileObjectDTO = fileObjectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fileObjectDTO);
    }

    /**
     * {@code DELETE  /file-objects/:id} : delete the "id" fileObject.
     *
     * @param id the id of the fileObjectDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteFileObject(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FileObject : {}", id);
        return fileObjectService
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
