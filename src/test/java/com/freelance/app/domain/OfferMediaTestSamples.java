package com.freelance.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OfferMediaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static OfferMedia getOfferMediaSample1() {
        return new OfferMedia().id(1L).caption("caption1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static OfferMedia getOfferMediaSample2() {
        return new OfferMedia().id(2L).caption("caption2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static OfferMedia getOfferMediaRandomSampleGenerator() {
        return new OfferMedia()
            .id(longCount.incrementAndGet())
            .caption(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
