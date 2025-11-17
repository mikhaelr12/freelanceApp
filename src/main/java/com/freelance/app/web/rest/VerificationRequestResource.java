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
import org.springframework.http.server.reactive.ServerHttpRequest;
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

    /**
     * {@code GET  /verification-requests} : get all the skills.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of requests in body.
     */
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

    /**
     * {@code GET /verification-requests/my} : Get requests of the currently logged user.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of requests in body.
     * */
    @GetMapping("/my")
    public Mono<ResponseEntity<List<VerificationRequestDTO>>> getMyVerificationRequests(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        VerificationRequestCriteria criteria
    ) {
        return verificationRequestService
            .getMyVerificationRequests(pageable, criteria)
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    /**
     * {@code POST /verification-requests/request-verification} : Create a new verification request.
     *
     * @param verificationPhoto picture with the user and ID.
     * */
    @PostMapping(value = "/request-verification", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Void>> requestVerification(@RequestPart("verification-photo") FilePart verificationPhoto) {
        LOG.debug("REST request for verification {}", verificationPhoto);
        return verificationRequestService.requestVerification(verificationPhoto).then(Mono.just(ResponseEntity.ok().build()));
    }

    /**
     * {@code PATCH /verification-requests/update-status/{id}/{status}} : Partial update of the status of the verification request.
     *
     * @param id the id of the request.
     * @param status new status of the request.
     * @param message message provided in case of declined status.
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update-status/{id}/{status}")
    public Mono<ResponseEntity<Void>> updateStatus(
        @PathVariable("id") final Long id,
        @PathVariable VerificationRequestStatus status,
        @RequestBody(required = false) String message
    ) {
        return verificationRequestService.updateRequestStatus(id, status, message).then(Mono.just(ResponseEntity.ok().build()));
    }

    /**
     * {@code PATCH /verification-requests/cancel-request/{id}} : Partial update with canceling the request.
     *
     * @param id the id of the request to be canceled.
     * */
    @PatchMapping("/cancel-request/{id}")
    public Mono<ResponseEntity<Void>> cancelRequest(@PathVariable("id") final Long id) {
        return verificationRequestService.cancelRequest(id).then(Mono.just(ResponseEntity.ok().build()));
    }
}
