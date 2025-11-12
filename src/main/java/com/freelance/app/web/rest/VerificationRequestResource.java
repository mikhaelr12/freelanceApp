package com.freelance.app.web.rest;

import com.freelance.app.domain.VerificationRequest;
import com.freelance.app.domain.enumeration.VerificationRequestStatus;
import com.freelance.app.service.VerificationRequestService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    public Mono<ResponseEntity<List<VerificationRequest>>> getAllVerificationRequests() {
        return null;
    }

    @PostMapping(value = "/request-verification", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Void>> requestVerification(@RequestPart("verification-photo") FilePart verificationPhoto) {
        LOG.debug("REST request for verification {}", verificationPhoto);
        return verificationRequestService.requestVerification(verificationPhoto).then(Mono.just(ResponseEntity.ok().build()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/verify-profile/{id}")
    public Mono<ResponseEntity<Void>> verifyProfile(@PathVariable("id") final Long id) {
        return verificationRequestService.verifyProfile(id).then(Mono.just(ResponseEntity.ok().build()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update-status/{id}")
    public Mono<ResponseEntity<Void>> updateStatus(@PathVariable("id") final Long id, @RequestBody VerificationRequestStatus status) {
        return verificationRequestService.updateRequestStatus(id, status).then(Mono.just(ResponseEntity.ok().build()));
    }
}
