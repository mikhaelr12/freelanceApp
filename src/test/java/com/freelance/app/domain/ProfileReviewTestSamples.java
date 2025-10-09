package com.freelance.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProfileReviewTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProfileReview getProfileReviewSample1() {
        return new ProfileReview().id(1L).text("text1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static ProfileReview getProfileReviewSample2() {
        return new ProfileReview().id(2L).text("text2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static ProfileReview getProfileReviewRandomSampleGenerator() {
        return new ProfileReview()
            .id(longCount.incrementAndGet())
            .text(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
