package com.freelance.app.service;

import com.freelance.app.domain.Offer;
import com.freelance.app.domain.OfferPackage;
import com.freelance.app.domain.Profile;
import com.freelance.app.repository.OfferPackageRepository;
import com.freelance.app.repository.OfferRepository;
import com.freelance.app.service.dto.OfferPackageDTO;
import com.freelance.app.util.ProfileHelper;
import com.freelance.app.web.rest.errors.NotFoundAlertException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.OfferPackage}.
 */
@Service
@Transactional
public class OfferPackageService {

    private static final Logger LOG = LoggerFactory.getLogger(OfferPackageService.class);

    private final OfferPackageRepository offerPackageRepository;
    private final ProfileHelper profileHelper;
    private final OfferRepository offerRepository;

    public OfferPackageService(
        OfferPackageRepository offerPackageRepository,
        ProfileHelper profileHelper,
        OfferRepository offerRepository
    ) {
        this.offerPackageRepository = offerPackageRepository;
        this.profileHelper = profileHelper;
        this.offerRepository = offerRepository;
    }

    public Mono<OfferPackage> createOfferPackage(Long offerId, OfferPackageDTO dto) {
        return profileHelper
            .getCurrentProfile()
            .zipWith(offerRepository.findById(offerId))
            .flatMap(tuple -> {
                Profile profile = tuple.getT1();
                Offer offer = tuple.getT2();
                return offerPackageRepository.save(
                    new OfferPackage()
                        .name(dto.name())
                        .description(dto.description())
                        .price(dto.price())
                        .deliveryDays(dto.deliveryDays())
                        .packageTier(dto.packageTier())
                        .offer(offer)
                        .active(true)
                        .createdBy(profile.getFirstName() + " " + profile.getLastName())
                );
            });
    }

    public Mono<List<OfferPackageDTO>> getAllOfferPackagesForOffer(Long offerId) {
        return offerPackageRepository.findAllByOfferId(offerId).collectList();
    }

    public Mono<Void> deleteOfferPackage(Long offerId) {
        return offerPackageRepository
            .findById(offerId)
            .switchIfEmpty(Mono.error(new NotFoundAlertException("Offer package not found", "offerPackage", "offerPackageNotFound")))
            .flatMap(_ -> offerPackageRepository.deleteById(offerId))
            .then();
    }
}
