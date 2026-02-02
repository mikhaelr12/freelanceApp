package com.freelance.app.web.rest;

import com.freelance.app.service.SubcategoryService;
import com.freelance.app.service.dto.SubcategoryDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link com.freelance.app.domain.Subcategory}.
 */
@RestController
@RequestMapping("/api/subcategories")
public class SubcategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(SubcategoryResource.class);

    private final SubcategoryService subcategoryService;

    public SubcategoryResource(SubcategoryService subcategoryService) {
        this.subcategoryService = subcategoryService;
    }

    @Cacheable("categories")
    @GetMapping
    public Mono<ResponseEntity<List<SubcategoryDTO>>> getAllSubcategories() {
        LOG.info("REST request to get all Subcategories");
        return subcategoryService.findAllDTO().map(ResponseEntity::ok);
    }
}
