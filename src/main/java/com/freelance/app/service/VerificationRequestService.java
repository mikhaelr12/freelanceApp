package com.freelance.app.service;

import com.freelance.app.domain.VerificationRequest;
import com.freelance.app.domain.criteria.VerificationRequestCriteria;
import com.freelance.app.domain.enumeration.VerificationRequestStatus;
import com.freelance.app.repository.ProfileRepository;
import com.freelance.app.repository.VerificationRequestRepository;
import com.freelance.app.security.SecurityUtils;
import com.freelance.app.service.dto.VerificationRequestDTO;
import com.freelance.app.util.FileProcessUtil;
import com.freelance.app.util.ProfileHelper;
import com.freelance.app.web.rest.errors.BadRequestAlertException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.VerificationRequest}.
 */
@Service
@Transactional
public class VerificationRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(VerificationRequestService.class);

    private final VerificationRequestRepository verificationRequestRepository;
    private final ProfileHelper profileHelper;
    private final ProfileRepository profileRepository;
    private final FileProcessUtil fileProcessUtil;

    public VerificationRequestService(
        VerificationRequestRepository verificationRequestRepository,
        ProfileHelper profileHelper,
        ProfileRepository profileRepository,
        FileProcessUtil fileProcessUtil
    ) {
        this.verificationRequestRepository = verificationRequestRepository;
        this.profileHelper = profileHelper;
        this.profileRepository = profileRepository;
        this.fileProcessUtil = fileProcessUtil;
    }

    /**
     * Get all verification requests
     *
     * @param pageable pagination information
     * @param criteria criteria to search by
     */
    @Transactional(readOnly = true)
    public Mono<List<VerificationRequestDTO>> getAllVerificationRequests(Pageable pageable, VerificationRequestCriteria criteria) {
        return verificationRequestRepository.findByCriteriaDTO(criteria, pageable).collectList();
    }

    /**
     * Get my verification requests
     *
     * @param pageable pagination information
     * @param criteria criteria to search by
     */
    @Transactional(readOnly = true)
    public Mono<List<VerificationRequestDTO>> getMyVerificationRequests(Pageable pageable, VerificationRequestCriteria criteria) {
        return profileHelper
            .getCurrentProfile()
            .flatMap(profile -> {
                LongFilter profileId = new LongFilter();
                profileId.setEquals(profile.getId());
                criteria.setProfileId(profileId);
                return verificationRequestRepository.findByCriteriaDTO(criteria, pageable).collectList();
            });
    }

    /**
     * Create a request for profile verification
     *
     * @param verificationPhoto the picture of the user sent for verification
     */
    @Transactional
    public Mono<Void> requestVerification(FilePart verificationPhoto) {
        LOG.debug("Requesting verification");
        return profileHelper
            .getCurrentProfile()
            .flatMap(profile -> {
                if (Boolean.TRUE.equals(profile.getVerified())) {
                    return Mono.error(
                        new BadRequestAlertException("User already verified ", profile.getUser().getLogin(), "userAlreadyVerified")
                    );
                }
                if (
                    Objects.equals(verificationPhoto.headers().getContentType(), MediaType.IMAGE_JPEG) &&
                    Objects.equals(verificationPhoto.headers().getContentType(), MediaType.IMAGE_PNG)
                ) {
                    return fileProcessUtil
                        .processFile(verificationPhoto, profile.getUser().getLogin(), "verification")
                        .flatMap(file ->
                            verificationRequestRepository.save(
                                new VerificationRequest()
                                    .profile(profile)
                                    .fileObject(file)
                                    .status(VerificationRequestStatus.PENDING)
                                    .createdBy(profile.getUser().getLogin())
                            )
                        );
                } else {
                    return Mono.error(
                        new BadRequestAlertException(
                            "Wrong file extension",
                            Objects.requireNonNull(verificationPhoto.headers().getContentType()).toString(),
                            "fileExtension"
                        )
                    );
                }
            })
            .then();
    }

    /**
     * Update status of a verification request
     *
     * @param id      id of the request
     * @param status  new status for the request
     * @param message reason if the request is rejected
     */
    @Transactional
    public Mono<Void> updateRequestStatus(Long id, VerificationRequestStatus status, String message) {
        LOG.debug("Request to update request status with id {}", id);
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(login ->
                verificationRequestRepository
                    .findById(id)
                    .switchIfEmpty(
                        Mono.error(new BadRequestAlertException("Request not found ", id.toString(), "verificationRequestNotFound"))
                    )
                    .flatMap(verificationRequest -> {
                        if (verificationRequest.getStatus().equals(VerificationRequestStatus.CANCELED)) {
                            return Mono.error(
                                new BadRequestAlertException(
                                    "The request is cancelled, can not change it",
                                    "",
                                    "verificationRequestCanceled"
                                )
                            );
                        }
                        if (status == VerificationRequestStatus.REJECTED) {
                            verificationRequest.setStatus(VerificationRequestStatus.REJECTED);
                            verificationRequest.setMessage(message);
                        } else if (status == VerificationRequestStatus.COMPLETED) {
                            verificationRequest.setStatus(VerificationRequestStatus.COMPLETED);
                            verificationRequest.setMessage(null);
                        } else {
                            verificationRequest.setStatus(status);
                        }

                        return verificationRequestRepository
                            .save(verificationRequest)
                            .flatMap(saved -> {
                                if (status == VerificationRequestStatus.COMPLETED) {
                                    return verifyProfile(saved).thenReturn(saved);
                                }
                                return Mono.just(saved);
                            });
                    })
            )
            .then();
    }

    /**
     * Cancel a verification request
     *
     * @param id id of the request to be canceled
     */
    @Transactional
    public Mono<Void> cancelRequest(Long id) {
        LOG.debug("Request to cancel request with id {}", id);
        return profileHelper
            .getCurrentProfile()
            .flatMap(profile ->
                verificationRequestRepository
                    .findById(id)
                    .flatMap(request -> {
                        if (!request.getProfileId().equals(profile.getId())) {
                            return Mono.error(
                                new BadRequestAlertException("Request does not belong to", profile.getUser().getLogin(), "requestNotFound")
                            );
                        }
                        request.setStatus(VerificationRequestStatus.CANCELED);
                        return verificationRequestRepository.save(request);
                    })
            )
            .then();
    }

    private Mono<Void> verifyProfile(VerificationRequest verificationRequest) {
        return profileRepository
            .findById(verificationRequest.getProfileId())
            .flatMap(profile -> {
                profile.setVerified(true);
                return profileRepository.save(profile);
            })
            .then();
    }
}
