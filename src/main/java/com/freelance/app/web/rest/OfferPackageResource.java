package com.freelance.app.web.rest;

import com.freelance.app.domain.OfferPackage;
import com.freelance.app.service.OfferPackageService;
import com.freelance.app.service.dto.OfferPackageDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link com.freelance.app.domain.OfferPackage}.
 */
@RestController
@RequestMapping("/api/offer-packages")
public class OfferPackageResource {

    private static final Logger LOG = LoggerFactory.getLogger(OfferPackageResource.class);

    private static final String ENTITY_NAME = "offerPackage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OfferPackageService offerPackageService;

    public OfferPackageResource(OfferPackageService offerPackageService) {
        this.offerPackageService = offerPackageService;
    }

    @PostMapping("/{offerId}")
    public Mono<ResponseEntity<OfferPackage>> createOfferPackage(
        @PathVariable Long offerId,
        @Valid @RequestBody OfferPackageDTO offerPackage
    ) {
        return offerPackageService.createOfferPackage(offerId, offerPackage).map(ResponseEntity::ok);
    }

    @GetMapping("/{offerId}")
    public Mono<ResponseEntity<List<OfferPackageDTO>>> getOfferPackage(@PathVariable Long offerId) {
        return offerPackageService.getAllOfferPackagesForOffer(offerId).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{offerId}")
    public Mono<ResponseEntity<Void>> deleteOfferPackage(@PathVariable Long offerId) {
        return offerPackageService.deleteOfferPackage(offerId).map(ResponseEntity::ok);
    }
}
