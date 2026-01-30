package com.freelance.app.web.rest;

import com.freelance.app.service.OfferTypeService;
import com.freelance.app.service.dto.OfferTypeShortDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{subcategoryId}")
    public Mono<ResponseEntity<List<OfferTypeShortDTO>>> getAllOfferTypesForSubcategory(@PathVariable Long subcategoryId) {
        LOG.info("REST request to get all OfferTypes for subcategory id {}", subcategoryId);
        return offerTypeService.getAllOfferTypesForSubcategory(subcategoryId).map(ResponseEntity::ok);
    }
}
