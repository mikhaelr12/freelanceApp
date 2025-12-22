package com.freelance.app.web.rest;

import com.freelance.app.domain.Offer;
import com.freelance.app.domain.criteria.OfferCriteria;
import com.freelance.app.service.OfferService;
import com.freelance.app.service.dto.OfferDTO;
import com.freelance.app.service.dto.OfferShortDTO;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        return offerService
            .getOffers(criteria, pageable)
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping
    public Mono<ResponseEntity<Offer>> createOffer(@RequestBody OfferDTO dto) {
        return offerService.createOffer(dto).map(ResponseEntity::ok);
    }

    @PutMapping("/{offerId}")
    public Mono<ResponseEntity<Offer>> updateOffer(@RequestBody OfferDTO dto, @PathVariable Long offerId) {
        return offerService.updateOffer(dto, offerId).map(ResponseEntity::ok);
    }

    @PostMapping(value = "/upload-images/{offerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Void>> uploadOfferImages(@RequestPart Flux<FilePart> images, @PathVariable Long offerId) {
        return offerService.uploadOfferImages(images, offerId).map(ResponseEntity::ok);
    }
}
