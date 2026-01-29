package com.freelance.app.service;

import static java.util.Objects.isNull;

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
    public Mono<Offer> createOffer(OfferDTO dto) {
        return offerTypeRepository
            .findById(dto.getOfferTypeId())
            .flatMap(offer ->
                tagRepository
                    .findAllById(dto.getTagIds())
                    .switchIfEmpty(Mono.error(new NotFoundAlertException("No tags found", "Tags", "tagNotFound")))
                    .collect(Collectors.toSet())
                    .flatMap(tags ->
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
                                        .offertype(offer)
                                        .tags(tags)
                                )
                            )
                    )
            );
    }

    public Mono<Offer> updateOffer(OfferDTO dto, Long offerId) {
        return offerRepository
            .findById(offerId)
            .flatMap(offer -> {
                if (!isNull(dto.getName())) {
                    offer.setName(dto.getName());
                }
                if (!isNull(dto.getDescription())) {
                    offer.setDescription(dto.getDescription());
                }
                if (!isNull(dto.getTagIds())) {
                    offer.setTags(fetchTags(dto.getTagIds()));
                }
                if (!isNull(dto.getOfferTypeId())) {
                    offer.setOffertypeId(dto.getOfferTypeId());
                }
                return Mono.just(offer);
            });
    }

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
