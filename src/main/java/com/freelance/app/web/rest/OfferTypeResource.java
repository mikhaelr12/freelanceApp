package com.freelance.app.web.rest;

import com.freelance.app.service.OfferTypeService;
import com.freelance.app.service.dto.OfferTypeShortDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link com.freelance.app.domain.OfferType}.
 */
@RestController
@RequestMapping("/api/offer-types")
public class OfferTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(OfferTypeResource.class);

    private final OfferTypeService offerTypeService;

    public OfferTypeResource(OfferTypeService offerTypeService) {
        this.offerTypeService = offerTypeService;
    }

    @GetMapping("/category/{categoryId}")
    public Mono<ResponseEntity<List<OfferTypeShortDTO>>> getAllOfferTypesForCategory(@PathVariable Long categoryId) {
        LOG.info("REST request to get all OfferTypes for category id {}", categoryId);
        return offerTypeService.getAllOfferTypesForCategory(categoryId).map(ResponseEntity::ok);
    }

    @GetMapping("/{subcategoryId}")
    public Mono<ResponseEntity<List<OfferTypeShortDTO>>> getAllOfferTypesForSubcategory(@PathVariable Long subcategoryId) {
        LOG.info("REST request to get all OfferTypes for subcategory id {}", subcategoryId);
        return offerTypeService.getAllOfferTypesForSubcategory(subcategoryId).map(ResponseEntity::ok);
    }
}
