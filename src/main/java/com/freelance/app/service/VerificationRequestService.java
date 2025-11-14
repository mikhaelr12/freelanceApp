package com.freelance.app.service;

import com.freelance.app.config.ApplicationProperties;
import com.freelance.app.domain.FileObject;
import com.freelance.app.domain.VerificationRequest;
import com.freelance.app.domain.criteria.VerificationRequestCriteria;
import com.freelance.app.domain.enumeration.VerificationRequestStatus;
import com.freelance.app.repository.FileObjectRepository;
import com.freelance.app.repository.ProfileRepository;
import com.freelance.app.repository.UserRepository;
import com.freelance.app.repository.VerificationRequestRepository;
import com.freelance.app.security.SecurityUtils;
import com.freelance.app.service.dto.VerificationRequestDTO;
import com.freelance.app.util.MinioUtil;
import com.freelance.app.web.rest.errors.BadRequestAlertException;
import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.VerificationRequest}.
 */
@Service
@Transactional
public class VerificationRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(VerificationRequestService.class);

    private final VerificationRequestRepository verificationRequestRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ApplicationProperties applicationProperties;
    private final MinioUtil minioUtil;
    private final FileObjectRepository fileObjectRepository;

    public VerificationRequestService(
        VerificationRequestRepository verificationRequestRepository,
        UserRepository userRepository,
        ProfileRepository profileRepository,
        ApplicationProperties applicationProperties,
        MinioUtil minioUtil,
        FileObjectRepository fileObjectRepository
    ) {
        this.verificationRequestRepository = verificationRequestRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.applicationProperties = applicationProperties;
        this.minioUtil = minioUtil;
        this.fileObjectRepository = fileObjectRepository;
    }

    /**
     * Get all verification requests
     *
     * @param pageable pagination information
     * @param criteria criteria to search by
     * */
    public Mono<List<VerificationRequestDTO>> getAllVerificationRequests(Pageable pageable, VerificationRequestCriteria criteria) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(login ->
                userRepository
                    .findOneByLogin(login)
                    .flatMap(user ->
                        profileRepository
                            .findByUserId(user.getId())
                            .flatMap(profile -> {
                                criteria.setProfileId(new LongFilter(criteria.getProfileId()));
                                return verificationRequestRepository.findByCriteriaDTO(criteria, pageable).collectList();
                            })
                    )
            );
    }

    /**
     * Create a request for profile verification
     *
     * @param verificationPhoto the picture of the user send for verification
     * */
    @Transactional
    public Mono<Void> requestVerification(FilePart verificationPhoto) {
        LOG.debug("Requesting verification");
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(login ->
                userRepository
                    .findOneByLogin(login)
                    .switchIfEmpty(Mono.error(new BadRequestAlertException("User not found: ", login, "userNotFound")))
                    .flatMap(user ->
                        profileRepository
                            .findByUserId(user.getId())
                            .switchIfEmpty(
                                Mono.error(new BadRequestAlertException("Profile not found ", user.getId().toString(), "profileNotFound"))
                            )
                            .flatMap(profile -> {
                                if (Boolean.TRUE.equals(profile.getVerified())) {
                                    return Mono.error(new BadRequestAlertException("User already verified ", login, "userAlreadyVerified"));
                                }
                                return processVerificationPicture(verificationPhoto, login)
                                    .flatMap(file ->
                                        verificationRequestRepository.save(
                                            new VerificationRequest()
                                                .profile(profile)
                                                .fileObject(file)
                                                .status(VerificationRequestStatus.PENDING)
                                                .createdBy(login)
                                        )
                                    )
                                    .then();
                            })
                    )
            )
            .then();
    }

    /**
     * Update status of a verification request
     *
     * @param id id of the request
     * @param status new status for the request
     * @param message reason if the request is rejected
     * */
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
                        if (status.equals(VerificationRequestStatus.REJECTED)) {
                            verificationRequest.setStatus(VerificationRequestStatus.REJECTED);
                            verificationRequest.setMessage(message);
                        } else {
                            verificationRequest.setStatus(status);
                        }
                        verificationRequest.lastModifiedDate(Instant.now());
                        verificationRequest.lastModifiedBy(login);
                        return verificationRequestRepository.save(verificationRequest);
                    })
            )
            .then();
    }

    /**
     * Cancel a verification request
     *
     * @param id id of the request to be canceled
     * */
    public Mono<Void> cancelRequest(Long id) {
        LOG.debug("Request to cancel request with id {}", id);
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(login ->
                userRepository
                    .findOneByLogin(login)
                    .flatMap(user ->
                        profileRepository
                            .findByUserId(user.getId())
                            .flatMap(profile ->
                                verificationRequestRepository
                                    .findById(id)
                                    .flatMap(request -> {
                                        if (!request.getProfileId().equals(profile.getId())) {
                                            return Mono.error(
                                                new BadRequestAlertException("Request does not belong to ", login, "requestNotFound")
                                            );
                                        }
                                        request.setStatus(VerificationRequestStatus.CANCELED);
                                        return verificationRequestRepository.save(request);
                                    })
                            )
                    )
                    .then()
            );
    }

    private Mono<FileObject> processVerificationPicture(FilePart verificationPicture, String userLogin) {
        final String bucket = applicationProperties.getMinio().getBucketName();
        final String original = Optional.of(verificationPicture.filename()).orElse("file.bin");
        final String ext = original.contains(".") ? original.substring(original.lastIndexOf('.') + 1) : "bin";
        final String contentType = Optional.ofNullable(verificationPicture.headers().getContentType())
            .map(MediaType::toString)
            .orElse("application/octet-stream");
        final String objectKey = "users/%s/verification/%s.%s".formatted(userLogin, UUID.randomUUID(), ext);

        return DataBufferUtils.join(verificationPicture.content()).flatMap(buf -> {
            byte[] bytes = new byte[buf.readableByteCount()];
            buf.read(bytes);
            DataBufferUtils.release(buf);

            String checksum = sha256(bytes);
            long size = bytes.length;

            Mono<Void> uploadMono = Mono.fromCallable(() -> {
                minioUtil.createBucketIfMissing(bucket);
                try (InputStream in = new ByteArrayInputStream(bytes)) {
                    minioUtil.uploadFile(bucket, objectKey, in);
                }
                return (Void) null;
            }).subscribeOn(Schedulers.boundedElastic());

            return uploadMono.then(
                fileObjectRepository.save(
                    new FileObject()
                        .bucket(bucket)
                        .objectKey(objectKey)
                        .contentType(contentType)
                        .fileSize(size)
                        .checksum(checksum)
                        .durationSeconds(0)
                        .createdDate(Instant.now())
                )
            );
        });
    }

    private static String sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return HexFormat.of().formatHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
