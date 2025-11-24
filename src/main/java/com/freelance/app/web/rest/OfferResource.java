package com.freelance.app.web.rest;

import com.freelance.app.domain.criteria.OfferCriteria;
import com.freelance.app.service.OfferService;
import com.freelance.app.service.dto.OfferShortDTO;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/offers")
public class OfferResource {

    private final OfferService offerService;

    public OfferResource(OfferService offerService) {
        this.offerService = offerService;
    }

    public Mono<ResponseEntity<List<OfferShortDTO>>> findAll(
        OfferCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        return offerService
            .getOffers(criteria, pageable)
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
