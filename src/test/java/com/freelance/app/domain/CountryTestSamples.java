package com.freelance.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CountryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Country getCountrySample1() {
        return new Country()
            .id(1L)
            .name("name1")
            .iso2("iso21")
            .iso3("iso31")
            .region("region1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Country getCountrySample2() {
        return new Country()
            .id(2L)
            .name("name2")
            .iso2("iso22")
            .iso3("iso32")
            .region("region2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Country getCountryRandomSampleGenerator() {
        return new Country()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .iso2(UUID.randomUUID().toString())
            .iso3(UUID.randomUUID().toString())
            .region(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
