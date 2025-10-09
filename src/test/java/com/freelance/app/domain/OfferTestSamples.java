package com.freelance.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OfferTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Offer getOfferSample1() {
        return new Offer().id(1L).name("name1").description("description1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static Offer getOfferSample2() {
        return new Offer().id(2L).name("name2").description("description2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static Offer getOfferRandomSampleGenerator() {
        return new Offer()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
