package com.freelance.app.web.rest;

import com.freelance.app.domain.Offer;
import com.freelance.app.domain.criteria.OfferCriteria;
import com.freelance.app.service.OfferService;
import com.freelance.app.service.dto.OfferDTO;
import com.freelance.app.service.dto.OfferShortDTO;
import com.freelance.app.service.dto.OfferUpdateDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/offers")
public class OfferResource {

    private final OfferService offerService;

    public OfferResource(OfferService offerService) {
        this.offerService = offerService;
    }

    /**
     * {@code GET /offers} : Create a new offer
     *
     * @param criteria the criteria to search by.
     * @param pageable the amount to be fetched.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body containing list of the offers,
     * otherwise status {@code 400 (NOT FOUND)}  if there are not any found.
     */
    @GetMapping
    public Mono<ResponseEntity<List<OfferShortDTO>>> findAll(
        OfferCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        return offerService
            .countByCriteria(criteria)
            .zipWith(offerService.getOffers(criteria, pageable))
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

    @PostMapping
    public Mono<ResponseEntity<Offer>> createOffer(@Valid @RequestBody OfferDTO dto) {
        return offerService.createOffer(dto).map(ResponseEntity::ok);
    }

    @PatchMapping("/{offerId}")
    public Mono<ResponseEntity<Offer>> updateOffer(@RequestBody OfferUpdateDTO dto, @PathVariable Long offerId) {
        return offerService.updateOffer(dto, offerId).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{offerId}")
    public Mono<ResponseEntity<Void>> deleteOffer(@PathVariable Long offerId) {
        return offerService.deleteOffer(offerId).map(ResponseEntity::ok);
    }
}
