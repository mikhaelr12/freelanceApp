package com.freelance.app.service;

import com.freelance.app.repository.SubcategoryRepository;
import com.freelance.app.service.dto.SubcategoryDTO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Subcategory}.
 */
@Service
@Transactional
public class SubcategoryService {

    private final SubcategoryRepository subcategoryRepository;

    public SubcategoryService(SubcategoryRepository subcategoryRepository) {
        this.subcategoryRepository = subcategoryRepository;
    }

    public Mono<List<SubcategoryDTO>> findAllDTO() {
        return subcategoryRepository.findAllDTO().collectList();
    }
}
