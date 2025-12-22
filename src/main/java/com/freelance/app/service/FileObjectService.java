package com.freelance.app.service;

import com.freelance.app.domain.FileObject;
import com.freelance.app.domain.criteria.FileObjectCriteria;
import com.freelance.app.repository.FileObjectRepository;
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

    public FileObjectService(FileObjectRepository fileObjectRepository) {
        this.fileObjectRepository = fileObjectRepository;
    }

    /**
     * Save a fileObject.
     *
     * @param fileObject the entity to save.
     * @return the persisted entity.
     */
    public Mono<FileObject> save(FileObject fileObject) {
        LOG.debug("Request to save FileObject : {}", fileObject);
        return fileObjectRepository.save(fileObject);
    }

    /**
     * Update a fileObject.
     *
     * @param fileObject the entity to save.
     * @return the persisted entity.
     */
    public Mono<FileObject> update(FileObject fileObject) {
        LOG.debug("Request to update FileObject : {}", fileObject);
        return fileObjectRepository.save(fileObject);
    }

    /**
     * Partially update a fileObject.
     *
     * @param fileObject the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<FileObject> partialUpdate(FileObject fileObject) {
        LOG.debug("Request to partially update FileObject : {}", fileObject);

        return fileObjectRepository
            .findById(fileObject.getId())
            .map(existingFileObject -> {
                if (fileObject.getBucket() != null) {
                    existingFileObject.setBucket(fileObject.getBucket());
                }
                if (fileObject.getObjectKey() != null) {
                    existingFileObject.setObjectKey(fileObject.getObjectKey());
                }
                if (fileObject.getContentType() != null) {
                    existingFileObject.setContentType(fileObject.getContentType());
                }
                if (fileObject.getFileSize() != null) {
                    existingFileObject.setFileSize(fileObject.getFileSize());
                }
                if (fileObject.getChecksum() != null) {
                    existingFileObject.setChecksum(fileObject.getChecksum());
                }
                if (fileObject.getDurationSeconds() != null) {
                    existingFileObject.setDurationSeconds(fileObject.getDurationSeconds());
                }
                if (fileObject.getCreatedDate() != null) {
                    existingFileObject.setCreatedDate(fileObject.getCreatedDate());
                }
                if (fileObject.getLastModifiedDate() != null) {
                    existingFileObject.setLastModifiedDate(fileObject.getLastModifiedDate());
                }
                if (fileObject.getCreatedBy() != null) {
                    existingFileObject.setCreatedBy(fileObject.getCreatedBy());
                }
                if (fileObject.getLastModifiedBy() != null) {
                    existingFileObject.setLastModifiedBy(fileObject.getLastModifiedBy());
                }

                return existingFileObject;
            })
            .flatMap(fileObjectRepository::save);
    }

    /**
     * Find fileObjects by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FileObject> findByCriteria(FileObjectCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all FileObjects by Criteria");
        return fileObjectRepository.findByCriteria(criteria, pageable);
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
     *  Get all the fileObjects where VerificationRequest is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FileObject> findAllWhereVerificationRequestIsNull() {
        LOG.debug("Request to get all fileObjects where VerificationRequest is null");
        return fileObjectRepository.findAllWhereVerificationRequestIsNull();
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
    public Mono<FileObject> findOne(Long id) {
        LOG.debug("Request to get FileObject : {}", id);
        return fileObjectRepository.findById(id);
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
