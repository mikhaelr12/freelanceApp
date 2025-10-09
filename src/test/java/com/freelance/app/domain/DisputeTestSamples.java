package com.freelance.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DisputeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Dispute getDisputeSample1() {
        return new Dispute().id(1L).reason("reason1");
    }

    public static Dispute getDisputeSample2() {
        return new Dispute().id(2L).reason("reason2");
    }

    public static Dispute getDisputeRandomSampleGenerator() {
        return new Dispute().id(longCount.incrementAndGet()).reason(UUID.randomUUID().toString());
    }
}
