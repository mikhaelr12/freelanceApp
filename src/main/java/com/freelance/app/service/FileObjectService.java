package com.freelance.app.service;

import com.freelance.app.domain.criteria.FileObjectCriteria;
import com.freelance.app.repository.FileObjectRepository;
import com.freelance.app.service.dto.FileObjectDTO;
import com.freelance.app.util.MinioUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.FileObject}.
 */
@Service
@Transactional
public class FileObjectService {

    private static final Logger LOG = LoggerFactory.getLogger(FileObjectService.class);

    private final FileObjectRepository fileObjectRepository;

    private final MinioUtil minioUtil;

    public FileObjectService(FileObjectRepository fileObjectRepository, MinioUtil minioUtil) {
        this.fileObjectRepository = fileObjectRepository;
        this.minioUtil = minioUtil;
    }

    /**
     * Save a fileObject.
     *
     * @param fileObjectDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<FileObjectDTO> save(FileObjectDTO fileObjectDTO) {
        LOG.debug("Request to save FileObject : {}", fileObjectDTO);
        return null;
    }

    /**
     * Update a fileObject.
     *
     * @param fileObjectDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<FileObjectDTO> update(FileObjectDTO fileObjectDTO) {
        LOG.debug("Request to update FileObject : {}", fileObjectDTO);
        return null;
    }

    /**
     * Partially update a fileObject.
     *
     * @param fileObjectDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<FileObjectDTO> partialUpdate(FileObjectDTO fileObjectDTO) {
        LOG.debug("Request to partially update FileObject : {}", fileObjectDTO);

        return null;
    }

    /**
     * Find fileObjects by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FileObjectDTO> findByCriteria(FileObjectCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all FileObjects by Criteria");
        return null;
    }

    /**
     * Find the count of fileObjects by criteria.
     * @param criteria filtering criteria
     * @return the count of fileObjects
     */
    public Mono<Long> countByCriteria(FileObjectCriteria criteria) {
        LOG.debug("Request to get the count of all FileObjects by Criteria");
        return fileObjectRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of fileObjects available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return fileObjectRepository.count();
    }

    /**
     * Get one fileObject by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<FileObjectDTO> findOne(Long id) {
        LOG.debug("Request to get FileObject : {}", id);
        return null;
    }

    /**
     * Delete the fileObject by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete FileObject : {}", id);
        return fileObjectRepository.deleteById(id);
    }
}
