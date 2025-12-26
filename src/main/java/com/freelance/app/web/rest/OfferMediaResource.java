package com.freelance.app.web.rest;

import com.freelance.app.service.OfferMediaService;
import com.freelance.app.service.dto.OfferMediaDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link com.freelance.app.domain.OfferMedia}.
 */
@RestController
@RequestMapping("/api/offer-medias")
public class OfferMediaResource {

    private static final Logger LOG = LoggerFactory.getLogger(OfferMediaResource.class);

    private final OfferMediaService offerMediaService;

    public OfferMediaResource(OfferMediaService offerMediaService) {
        this.offerMediaService = offerMediaService;
    }

    @PostMapping(value = "/upload/{offerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Void>> uploadOfferMedia(@RequestPart Flux<FilePart> images, @PathVariable Long offerId) {
        return offerMediaService.uploadOfferMedia(images, offerId).map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "/delete/{offerId}")
    public Mono<ResponseEntity<Void>> deleteOfferMedia(@PathVariable Long offerId, @RequestBody List<Long> mediaIds) {
        return offerMediaService.deleteOfferMedia(offerId, mediaIds).map(ResponseEntity::ok);
    }

    @GetMapping("/retreive/{offerId}")
    public Mono<ResponseEntity<List<OfferMediaDTO>>> getAllOfferMedias(@PathVariable Long offerId) {
        return offerMediaService.getAllOfferMedia(offerId).map(ResponseEntity::ok);
    }
}
