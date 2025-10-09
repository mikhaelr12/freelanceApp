package com.freelance.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class OfferPackageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static OfferPackage getOfferPackageSample1() {
        return new OfferPackage()
            .id(1L)
            .name("name1")
            .description("description1")
            .currency("currency1")
            .deliveryDays(1)
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static OfferPackage getOfferPackageSample2() {
        return new OfferPackage()
            .id(2L)
            .name("name2")
            .description("description2")
            .currency("currency2")
            .deliveryDays(2)
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static OfferPackage getOfferPackageRandomSampleGenerator() {
        return new OfferPackage()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .currency(UUID.randomUUID().toString())
            .deliveryDays(intCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
