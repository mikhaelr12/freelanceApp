package com.freelance.app.service;

import com.freelance.app.domain.Offer;
import com.freelance.app.domain.OfferMedia;
import com.freelance.app.domain.Tag;
import com.freelance.app.domain.criteria.OfferCriteria;
import com.freelance.app.domain.enumeration.OfferStatus;
import com.freelance.app.repository.*;
import com.freelance.app.service.dto.OfferDTO;
import com.freelance.app.service.dto.OfferShortDTO;
import com.freelance.app.service.dto.ProfileDTO;
import com.freelance.app.util.ImageHelper;
import com.freelance.app.util.ProfileHelper;
import com.freelance.app.web.rest.errors.NotFoundAlertException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Offer}.
 */
@Service
@Transactional
public class OfferService {

    private final OfferRepository offerRepository;
    private final OfferTypeRepository offerTypeRepository;
    private final OfferMediaRepository offerMediaRepository;
    private final ProfileService profileService;
    private final ProfileHelper profileHelper;
    private final TagRepository tagRepository;
    private final ImageHelper imageHelper;
    private final FileObjectRepository fileObjectRepository;

    public OfferService(
        OfferRepository offerRepository,
        OfferTypeRepository offerTypeRepository,
        OfferMediaRepository offerMediaRepository,
        ProfileService profileService,
        ProfileHelper profileHelper,
        TagRepository tagRepository,
        ImageHelper imageHelper,
        FileObjectRepository fileObjectRepository
    ) {
        this.offerRepository = offerRepository;
        this.offerTypeRepository = offerTypeRepository;
        this.offerMediaRepository = offerMediaRepository;
        this.profileService = profileService;
        this.profileHelper = profileHelper;
        this.tagRepository = tagRepository;
        this.imageHelper = imageHelper;
        this.fileObjectRepository = fileObjectRepository;
    }

    /**
     * Get all offers for a category.
     *
     * @param criteria criteria to search by.
     * @param pageable number of items.
     * @return list of offers
     *
     */
    public Mono<List<OfferShortDTO>> getOffers(OfferCriteria criteria, Pageable pageable) {
        return offerRepository
            .findByCriteria(criteria, pageable)
            .flatMap(offer ->
                Mono.zip(
                    Mono.just(offer),
                    profileService.findOne(offer.getOwnerId()),
                    imageHelper.fetchOfferMediaImages(offer.getId())
                ).map(tuple -> {
                    Offer o = tuple.getT1();
                    ProfileDTO owner = tuple.getT2();
                    List<String> images = tuple.getT3();

                    return new OfferShortDTO()
                        .id(o.getId())
                        .name(o.getName())
                        .rating(o.getRating())
                        .owner(owner)
                        .offerImages(new HashSet<>(images));
                })
            )
            .collectList();
    }

    /**
     * Create new offer.
     *
     * @param dto the dto sent for creation.
     * @return entity.
     *
     */
    @Transactional
    public Mono<Offer> createOffer(OfferDTO dto) {
        Mono<Set<Tag>> tagsMono = dto.getTagIds() == null || dto.getTagIds().isEmpty()
            ? Mono.just(java.util.Collections.emptySet())
            : tagRepository.findAllById(dto.getTagIds()).collect(Collectors.toSet());

        return offerTypeRepository
            .findById(dto.getOfferTypeId())
            .switchIfEmpty(Mono.error(new NotFoundAlertException("OfferType not found", "offertype", "offerTypeNotFound")))
            .zipWith(tagsMono)
            .flatMap(tuple ->
                profileHelper
                    .getCurrentProfile()
                    .flatMap(profile ->
                        offerRepository.save(
                            new Offer()
                                .name(dto.getName())
                                .description(dto.getDescription())
                                .rating(0.0)
                                .status(OfferStatus.ACTIVE)
                                .visibility(true)
                                .owner(profile)
                                .offertype(tuple.getT1())
                                .tags(tuple.getT2())
                        )
                    )
            );
    }

    @Transactional
    public Mono<Offer> updateOffer(OfferDTO dto, Long offerId) {
        return offerRepository
            .findById(offerId)
            .flatMap(offer -> {
                Optional.ofNullable(dto.getName()).ifPresent(offer::setName);
                Optional.ofNullable(dto.getDescription()).ifPresent(offer::setDescription);
                Optional.ofNullable(dto.getTagIds()).ifPresent(tagIds -> offer.setTags(fetchTags(tagIds)));
                Optional.ofNullable(dto.getOfferTypeId()).ifPresent(offer::setOffertypeId);
                return offerRepository.save(offer);
            });
    }

    @Transactional
    public Mono<Void> deleteOffer(Long offerId) {
        return profileHelper
            .getCurrentProfile()
            .flatMap(profile ->
                offerRepository
                    .findById(offerId)
                    .flatMap(offer -> {
                        if (!offer.getOwnerId().equals(profile.getId())) {
                            return Mono.error(
                                new NotFoundAlertException(
                                    "Offer does not belong to current user",
                                    "OfferMedia",
                                    "offerDoesNotBelongToThisUser"
                                )
                            );
                        }
                        return offerMediaRepository
                            .findByOffer(offerId)
                            .map(OfferMedia::getFileId)
                            .distinct()
                            .collectList()
                            .flatMap(fileIds ->
                                fileObjectRepository
                                    .deleteAllById(fileIds)
                                    .then(offerMediaRepository.deleteAllByOffer(offerId))
                                    .then(offerRepository.deleteById(offerId))
                            );
                    })
            );
    }

    private Set<Tag> fetchTags(Set<Long> tagIds) {
        return tagRepository.findAllById(tagIds).collect(Collectors.toSet()).block();
    }
}
