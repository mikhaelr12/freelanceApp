package com.freelance.app.service;

import com.freelance.app.domain.Offer;
import com.freelance.app.domain.OfferMedia;
import com.freelance.app.domain.enumeration.MediaKind;
import com.freelance.app.repository.OfferMediaRepository;
import com.freelance.app.repository.OfferRepository;
import com.freelance.app.security.SecurityUtils;
import com.freelance.app.service.dto.OfferMediaDTO;
import com.freelance.app.util.FileProcessUtil;
import com.freelance.app.util.ImageHelper;
import com.freelance.app.web.rest.errors.NotFoundAlertException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.OfferMedia}.
 */
@Service
@Transactional
public class OfferMediaService {

    private static final Logger LOG = LoggerFactory.getLogger(OfferMediaService.class);

    private final OfferMediaRepository offerMediaRepository;
    private final OfferRepository offerRepository;
    private final FileProcessUtil fileProcessUtil;
    private final ImageHelper imageHelper;

    public OfferMediaService(
        OfferMediaRepository offerMediaRepository,
        OfferRepository offerRepository,
        FileProcessUtil fileProcessUtil,
        ImageHelper imageHelper
    ) {
        this.offerMediaRepository = offerMediaRepository;
        this.offerRepository = offerRepository;
        this.fileProcessUtil = fileProcessUtil;
        this.imageHelper = imageHelper;
    }

    public Mono<Void> uploadOfferMedia(Flux<FilePart> images, Long offerId) {
        return SecurityUtils.getCurrentUserLogin()
            .zipWith(offerRepository.findById(offerId))
            .flatMapMany(tuple -> {
                String login = tuple.getT1();
                Offer offer = tuple.getT2();
                return images.flatMap(image ->
                    fileProcessUtil
                        .processFile(image, login, "offer-media")
                        .flatMap(file ->
                            offerMediaRepository.save(
                                new OfferMedia()
                                    .mediaKind(
                                        file.getContentType() != null && file.getContentType().contains("image")
                                            ? MediaKind.IMAGE
                                            : MediaKind.VIDEO
                                    )
                                    .isPrimary(false)
                                    .offer(offer)
                                    .file(file)
                            )
                        )
                );
            })
            .then();
    }

    public Mono<Void> deleteOfferMedia(Long offerId, List<Long> mediaIds) {
        return offerMediaRepository
            .findByOffer(offerId)
            .collectList()
            .flatMap(existingMedia -> {
                List<Long> existingFileObjects = existingMedia.stream().map(OfferMedia::getFileId).toList();
                return offerMediaRepository.deleteAllById(mediaIds).doOnNext(_ -> fileProcessUtil.deleteFiles(existingFileObjects).then());
            })
            .then();
    }

    public Mono<List<OfferMediaDTO>> getAllOfferMedia(Long offerId) {
        return imageHelper
            .fetchOfferMediaImagesWithId(offerId)
            .flatMap(images -> {
                List<OfferMediaDTO> dtos = images.entrySet().stream().map(e -> new OfferMediaDTO(e.getKey(), e.getValue())).toList();
                return Mono.just(dtos);
            })
            .switchIfEmpty(Mono.error(new NotFoundAlertException("No media found for this offer", "OfferMedia", "noMediaFoundForOffer")));
    }
}
