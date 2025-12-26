package com.freelance.app.util;

import com.freelance.app.repository.FileObjectRepository;
import com.freelance.app.repository.OfferMediaRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Component
public class ImageHelper {

    private final OfferMediaRepository offerMediaRepository;
    private final FileObjectRepository fileObjectRepository;
    private final MinioUtil minioUtil;

    public ImageHelper(OfferMediaRepository offerMediaRepository, FileObjectRepository fileObjectRepository, MinioUtil minioUtil) {
        this.offerMediaRepository = offerMediaRepository;
        this.fileObjectRepository = fileObjectRepository;
        this.minioUtil = minioUtil;
    }

    /**
     * Helper method for fetching images for an offer with its id from file object entity.
     *
     * @param offerId id of the offer to find the images.
     * @return list of images in base64 along with id from file object entity.
     */
    public Mono<Map<Long, String>> fetchOfferMediaImagesWithId(Long offerId) {
        return offerMediaRepository
            .findByOffer(offerId)
            .flatMap(media -> fileObjectRepository.findById(media.getFileId()).map(fileObject -> Tuples.of(fileObject.getId(), fileObject)))
            .flatMap(tuple ->
                Mono.fromCallable(() -> minioUtil.getImageAsBase64(tuple.getT2().getBucket(), tuple.getT2().getObjectKey()))
                    .subscribeOn(Schedulers.boundedElastic())
                    .map(base64 -> Tuples.of(tuple.getT1(), base64))
            )
            .collectMap(Tuple2::getT1, Tuple2::getT2);
    }

    /**
     * Helper method for fetching offer images.
     *
     * @param offerId id of the offer to fetch the images.
     * @return list of images in base64.
     */
    public Mono<List<String>> fetchOfferMediaImages(Long offerId) {
        return offerMediaRepository
            .findByOffer(offerId)
            .flatMap(media -> fileObjectRepository.findById(media.getFileId()))
            .map(fileObject -> minioUtil.getImageAsBase64(fileObject.getBucket(), fileObject.getObjectKey()))
            .collectList();
    }
}
