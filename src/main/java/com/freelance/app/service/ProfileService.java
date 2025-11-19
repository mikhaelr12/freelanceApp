package com.freelance.app.service;

import com.freelance.app.config.ApplicationProperties;
import com.freelance.app.domain.FileObject;
import com.freelance.app.domain.Profile;
import com.freelance.app.domain.Skill;
import com.freelance.app.domain.criteria.ProfileCriteria;
import com.freelance.app.repository.FileObjectRepository;
import com.freelance.app.repository.ProfileRepository;
import com.freelance.app.repository.SkillRepository;
import com.freelance.app.repository.UserRepository;
import com.freelance.app.security.SecurityUtils;
import com.freelance.app.service.dto.ProfileCreationDTO;
import com.freelance.app.service.dto.ProfileDTO;
import com.freelance.app.service.dto.ProfileEditDTO;
import com.freelance.app.util.MinioUtil;
import com.freelance.app.util.ProfileHelper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Profile}.
 */
@Service
@Transactional
public class ProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;

    private final UserRepository userRepository;

    private final SkillRepository skillRepository;
    private final MinioUtil minioUtil;
    private final ApplicationProperties applicationProperties;
    private final FileObjectRepository fileObjectRepository;
    private final ProfileHelper profileHelper;

    public ProfileService(
        ProfileRepository profileRepository,
        UserRepository userRepository,
        SkillRepository skillRepository,
        MinioUtil minioUtil,
        ApplicationProperties applicationProperties,
        FileObjectRepository fileObjectRepository,
        ProfileHelper profileHelper
    ) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.minioUtil = minioUtil;
        this.applicationProperties = applicationProperties;
        this.fileObjectRepository = fileObjectRepository;
        this.profileHelper = profileHelper;
    }

    /**
     * Update a profile.
     *
     * @param dto the entity to update.
     * @return the persisted entity.
     */
    @Transactional
    public Mono<Void> update(ProfileEditDTO dto, Long profileId) {
        LOG.debug("Request to update Profile : {}", dto);

        return profileRepository
            .findById(profileId)
            .switchIfEmpty(Mono.error(new IllegalStateException("Profile not found with id: " + profileId)))
            .flatMap(profile -> {
                if (dto.firstName() != null) profile.setFirstName(dto.firstName());
                if (dto.lastName() != null) profile.setLastName(dto.lastName());
                if (dto.description() != null) profile.setDescription(dto.description());

                Mono<Profile> afterSkills = (dto.skills() == null)
                    ? Mono.just(profile)
                    : updateSkills(dto.skills(), profile).thenReturn(profile);

                return afterSkills.flatMap(profileRepository::save);
            })
            .then();
    }

    /**
     * Find profiles by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProfileDTO> findByCriteria(ProfileCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Profiles by Criteria");
        return null;
    }

    /**
     * Find the count of profiles by criteria.
     *
     * @param criteria filtering criteria
     * @return the count of profiles
     */
    public Mono<Long> countByCriteria(ProfileCriteria criteria) {
        LOG.debug("Request to get the count of all Profiles by Criteria");
        return profileRepository.countByCriteria(criteria);
    }

    /**
     * Get one profile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProfileDTO> findOne(Long id) {
        LOG.debug("Request to get Profile : {}", id);
        return profileRepository
            .findOne(id)
            .flatMap(profile ->
                fileObjectRepository
                    .findById(profile.getProfilePictureId())
                    .flatMap(fileObject -> {
                        try {
                            profile.setImageBase64(minioUtil.getImageAsBase64(fileObject.getBucket(), fileObject.getObjectKey()));
                        } catch (Exception e) {
                            return Mono.error(new RuntimeException(e));
                        }
                        return Mono.just(profile);
                    })
            );
    }

    /**
     * Delete the profile by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        return profileRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
            .then(profileRepository.deleteById(id));
    }

    @Transactional
    public Mono<Profile> createProfile(ProfileCreationDTO dto) {
        return SecurityUtils.getCurrentUserLogin()
            .switchIfEmpty(Mono.error(new IllegalStateException("No authenticated user")))
            .flatMap(login ->
                userRepository
                    .findOneByLogin(login)
                    .switchIfEmpty(Mono.error(new IllegalStateException("User not found: " + login)))
                    .flatMap(user ->
                        skillRepository
                            .findAllById(dto.skillIds() == null ? List.of() : dto.skillIds())
                            .collectList()
                            .flatMap(foundSkills -> {
                                if (Objects.nonNull(dto.skillIds()) && foundSkills.size() != dto.skillIds().size()) {
                                    var missing = new HashSet<>(dto.skillIds());
                                    foundSkills.forEach(s -> missing.remove(s.getId()));
                                }

                                Profile p = new Profile()
                                    .firstName(dto.firstName())
                                    .lastName(dto.lastName())
                                    .createdDate(Instant.now())
                                    .createdBy(login)
                                    .profileType(dto.profileType())
                                    .verified(false);
                                if (dto.description() != null) p.setDescription(dto.description());
                                p.setUser(user);
                                p.setSkills(new HashSet<>(foundSkills));

                                return profileRepository.save(p);
                            })
                    )
            );
    }

    public Mono<Void> uploadProfilePicture(FilePart file) {
        return profileHelper
            .getCurrentProfile()
            .flatMap(profile -> {
                return processProfilePicture(file, profile)
                    .flatMap(fileObjectRepository::save)
                    .flatMap(fileObject -> {
                        profile.setProfilePictureId(fileObject.getId());
                        return profileRepository.save(profile);
                    });
            })
            .then();
    }

    private Mono<Set<Skill>> updateSkills(Set<Long> skillIds, Profile profile) {
        Set<Long> ids = (skillIds == null) ? Set.of() : skillIds;
        Set<Skill> existing = profile.getSkills() == null ? Set.of() : profile.getSkills();

        return skillRepository
            .findAllById(ids)
            .collect(Collectors.toSet())
            .map(found -> {
                Set<Skill> toAdd = new HashSet<>(found);
                toAdd.removeAll(existing);
                Set<Skill> toRemove = new HashSet<>(existing);
                toRemove.removeAll(found);

                Set<Skill> newSkills = new HashSet<>(existing);
                newSkills.removeAll(toRemove);
                newSkills.addAll(toAdd);

                profile.setSkills(newSkills);
                return newSkills;
            });
    }

    private Mono<FileObject> processProfilePicture(FilePart profilePicture, Profile profile) {
        final String bucket = applicationProperties.getMinio().getBucketName();
        final String original = Optional.of(profilePicture.filename()).orElse("file.bin");
        final String ext = original.contains(".") ? original.substring(original.lastIndexOf('.') + 1) : "bin";
        final String contentType = Optional.ofNullable(profilePicture.headers().getContentType())
            .map(MediaType::toString)
            .orElse("application/octet-stream");
        final String objectKey = "users/%s/profile-pictures/%s.%s".formatted(profile.getUser().getLogin(), UUID.randomUUID(), ext);

        return DataBufferUtils.join(profilePicture.content()).flatMap(buf -> {
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
