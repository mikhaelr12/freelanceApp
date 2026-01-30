package com.freelance.app.service;

import com.freelance.app.repository.OfferTypeRepository;
import com.freelance.app.service.dto.OfferTypeShortDTO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.OfferType}.
 */
@Service
@Transactional
public class OfferTypeService {

    private final OfferTypeRepository offerTypeRepository;

    public OfferTypeService(OfferTypeRepository offerTypeRepository) {
        this.offerTypeRepository = offerTypeRepository;
    }

    public Mono<List<OfferTypeShortDTO>> getAllOfferTypesForSubcategory(Long subcategoryId) {
        return offerTypeRepository.findAllOfferTypesForSubcategory(subcategoryId).collectList();
    }
}
