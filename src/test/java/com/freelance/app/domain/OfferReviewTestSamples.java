package com.freelance.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OfferReviewTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static OfferReview getOfferReviewSample1() {
        return new OfferReview().id(1L).text("text1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static OfferReview getOfferReviewSample2() {
        return new OfferReview().id(2L).text("text2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static OfferReview getOfferReviewRandomSampleGenerator() {
        return new OfferReview()
            .id(longCount.incrementAndGet())
            .text(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
