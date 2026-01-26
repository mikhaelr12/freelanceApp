package com.freelance.app.service;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.Profile;
import com.freelance.app.repository.FavoriteOfferRepository;
import com.freelance.app.repository.OfferRepository;
import com.freelance.app.util.ProfileHelper;
import com.freelance.app.web.rest.errors.UnauthorizedAlertException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.FavoriteOffer}.
 */
@Service
@Transactional
public class FavoriteOfferService {

    private static final String ENTITY_NAME = "favoriteOffer";

    private final FavoriteOfferRepository favoriteOfferRepository;
    private final ProfileHelper profileHelper;
    private final OfferRepository offerRepository;

    public FavoriteOfferService(
        FavoriteOfferRepository favoriteOfferRepository,
        ProfileHelper profileHelper,
        OfferRepository offerRepository
    ) {
        this.favoriteOfferRepository = favoriteOfferRepository;
        this.profileHelper = profileHelper;
        this.offerRepository = offerRepository;
    }

    /**
     * Create new favorite offer.
     *
     * @param offerId the id of offer to be added to favorites.
     * @return the entity created.
     */
    public Mono<FavoriteOffer> createFavoriteOffer(Long offerId) {
        return profileHelper
            .getCurrentProfile()
            .zipWith(offerRepository.findById(offerId))
            .flatMap(tuple -> favoriteOfferRepository.save(new FavoriteOffer().offer(tuple.getT2()).profile(tuple.getT1())));
    }

    public Mono<Void> deleteFavoriteOffer(Long offerId) {
        return profileHelper
            .getCurrentProfile()
            .zipWith(favoriteOfferRepository.findById(offerId))
            .flatMap(tuple -> {
                Profile profile = tuple.getT1();
                FavoriteOffer favoriteOffer = tuple.getT2();

                if (!profile.getId().equals(favoriteOffer.getProfile().getId())) {
                    return Mono.error(
                        new UnauthorizedAlertException(
                            "Favorite offer does not belong to current user",
                            ENTITY_NAME,
                            "favoriteOfferDoesNotBelongToCurrentUser"
                        )
                    );
                }
                return favoriteOfferRepository.delete(favoriteOffer);
            })
            .then();
    }
}
