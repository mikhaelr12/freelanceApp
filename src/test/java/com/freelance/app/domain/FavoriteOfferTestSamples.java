package com.freelance.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class FavoriteOfferTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FavoriteOffer getFavoriteOfferSample1() {
        return new FavoriteOffer().id(1L);
    }

    public static FavoriteOffer getFavoriteOfferSample2() {
        return new FavoriteOffer().id(2L);
    }

    public static FavoriteOffer getFavoriteOfferRandomSampleGenerator() {
        return new FavoriteOffer().id(longCount.incrementAndGet());
    }
}
