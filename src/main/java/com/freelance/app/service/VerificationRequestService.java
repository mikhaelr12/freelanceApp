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
import com.freelance.app.util.MinioUtil;
import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
     * Create a request for profile verification
     *
     * @param verificationPhoto the picture of the user send for verification
     * */
    @Transactional
    public Mono<Void> requestVerification(FilePart verificationPhoto) {
        LOG.debug("Requesting verification");
        return SecurityUtils.getCurrentUserLogin()
            .switchIfEmpty(Mono.error(new IllegalStateException("No authenticated user")))
            .flatMap(login ->
                userRepository
                    .findOneByLogin(login)
                    .switchIfEmpty(Mono.error(new IllegalStateException("User not found: " + login)))
                    .flatMap(user ->
                        profileRepository
                            .findByUserId(user.getId())
                            .flatMap(profile -> {
                                if (Boolean.TRUE.equals(profile.getVerified())) {
                                    return Mono.error(new IllegalStateException("User already verified: " + login));
                                }
                                return processVerificationPicture(verificationPhoto, login)
                                    .flatMap(file ->
                                        verificationRequestRepository.save(
                                            new VerificationRequest()
                                                .profile(profile)
                                                .fileObject(file)
                                                .status(VerificationRequestStatus.PENDING)
                                                .createdBy(login)
                                                .createdDate(Instant.now())
                                        )
                                    )
                                    .then();
                            })
                    )
            )
            .then();
    }

    /**
     * Verify a profile
     *
     * @param id the id of the profile to verify
     * */
    public Mono<Void> verifyProfile(Long id) {
        LOG.debug("Verifying profile with id {}", id);
        return profileRepository
            .findById(id)
            .flatMap(profile -> {
                profile.setVerified(true);
                return profileRepository.save(profile);
            })
            .then();
    }

    public Mono<Void> updateRequestStatus(Long id, VerificationRequestStatus status) {
        LOG.debug("Request to update request status with id {}", id);
        return verificationRequestRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Request not found with id: " + id)))
            .flatMap(verificationRequest -> {
                verificationRequest.setStatus(status);
                return verificationRequestRepository.save(verificationRequest);
            })
            .then();
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
