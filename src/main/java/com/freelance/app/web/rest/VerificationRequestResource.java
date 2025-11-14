package com.freelance.app.web.rest;

import com.freelance.app.domain.VerificationRequest;
import com.freelance.app.domain.criteria.VerificationRequestCriteria;
import com.freelance.app.domain.enumeration.VerificationRequestStatus;
import com.freelance.app.service.VerificationRequestService;
import com.freelance.app.service.dto.VerificationRequestDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link com.freelance.app.domain.VerificationRequest}.
 */
@RestController
@RequestMapping("/api/verification-requests")
public class VerificationRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(VerificationRequestResource.class);

    private final VerificationRequestService verificationRequestService;

    public VerificationRequestResource(VerificationRequestService verificationRequestService) {
        this.verificationRequestService = verificationRequestService;
    }

    @GetMapping("")
    public Mono<ResponseEntity<List<VerificationRequestDTO>>> getAllVerificationRequests(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        VerificationRequestCriteria criteria
    ) {
        return verificationRequestService
            .getAllVerificationRequests(pageable, criteria)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
            .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/request-verification", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Void>> requestVerification(@RequestPart("verification-photo") FilePart verificationPhoto) {
        LOG.debug("REST request for verification {}", verificationPhoto);
        return verificationRequestService.requestVerification(verificationPhoto).then(Mono.just(ResponseEntity.ok().build()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update-status/{id}")
    public Mono<ResponseEntity<Void>> updateStatus(
        @PathVariable("id") final Long id,
        @RequestBody VerificationRequestStatus status,
        @RequestBody(required = false) String message
    ) {
        return verificationRequestService.updateRequestStatus(id, status, message).then(Mono.just(ResponseEntity.ok().build()));
    }

    @PatchMapping("/cancel-request/{id}")
    public Mono<ResponseEntity<Void>> cancelRequest(@PathVariable("id") final Long id) {
        return verificationRequestService.cancelRequest(id).then(Mono.just(ResponseEntity.ok().build()));
    }

    @GetMapping("/my")
    public Mono<ResponseEntity<List<VerificationRequestDTO>>> getMyVerificationRequests(Pageable pageable) {}
}
