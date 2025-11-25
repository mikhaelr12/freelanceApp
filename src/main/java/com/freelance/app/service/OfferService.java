package com.freelance.app.service;

import com.freelance.app.config.ApplicationProperties;
import com.freelance.app.domain.FileObject;
import com.freelance.app.domain.Offer;
import com.freelance.app.domain.OfferMedia;
import com.freelance.app.domain.criteria.OfferCriteria;
import com.freelance.app.domain.criteria.OfferMediaCriteria;
import com.freelance.app.repository.*;
import com.freelance.app.service.dto.FileObjectDTO;
import com.freelance.app.service.dto.OfferShortDTO;
import com.freelance.app.service.dto.ProfileDTO;
import com.freelance.app.util.MinioUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Offer}.
 */
@Service
@Transactional
public class OfferService {

    private static final Logger LOG = LoggerFactory.getLogger(OfferService.class);

    private final OfferRepository offerRepository;
    private final OfferTypeRepository offerTypeRepository;
    private final MinioUtil minioUtil;
    private final FileObjectRepository fileObjectRepository;
    private final OfferMediaRepository offerMediaRepository;
    private final ProfileService profileService;

    public OfferService(
        OfferRepository offerRepository,
        OfferTypeRepository offerTypeRepository,
        MinioUtil minioUtil,
        FileObjectRepository fileObjectRepository,
        OfferMediaRepository offerMediaRepository,
        ProfileService profileService
    ) {
        this.offerRepository = offerRepository;
        this.offerTypeRepository = offerTypeRepository;
        this.minioUtil = minioUtil;
        this.fileObjectRepository = fileObjectRepository;
        this.offerMediaRepository = offerMediaRepository;
        this.profileService = profileService;
    }

    public Mono<List<OfferShortDTO>> getOffers(OfferCriteria criteria, Pageable pageable) {
        return offerRepository
            .findByCriteria(criteria, pageable)
            .flatMap(offer ->
                Mono.zip(Mono.just(offer), profileService.findOne(offer.getOwnerId()), fetchImages(offer.getId())).map(tuple -> {
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

    private Mono<List<String>> fetchImages(Long offerId) {
        return offerMediaRepository
            .findByOffer(offerId)
            .flatMap(media -> fileObjectRepository.findById(media.getFileId()))
            .map(fileObject -> minioUtil.getImageAsBase64(fileObject.getBucket(), fileObject.getObjectKey()))
            .collectList();
    }
}
