package com.freelance.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class VerificationRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static VerificationRequest getVerificationRequestSample1() {
        return new VerificationRequest().id(1L);
    }

    public static VerificationRequest getVerificationRequestSample2() {
        return new VerificationRequest().id(2L);
    }

    public static VerificationRequest getVerificationRequestRandomSampleGenerator() {
        return new VerificationRequest().id(longCount.incrementAndGet());
    }
}
