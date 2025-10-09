package com.freelance.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DeliveryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Delivery getDeliverySample1() {
        return new Delivery().id(1L).note("note1");
    }

    public static Delivery getDeliverySample2() {
        return new Delivery().id(2L).note("note2");
    }

    public static Delivery getDeliveryRandomSampleGenerator() {
        return new Delivery().id(longCount.incrementAndGet()).note(UUID.randomUUID().toString());
    }
}
