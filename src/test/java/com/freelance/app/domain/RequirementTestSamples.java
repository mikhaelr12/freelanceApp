package com.freelance.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RequirementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Requirement getRequirementSample1() {
        return new Requirement().id(1L).prompt("prompt1").answer("answer1");
    }

    public static Requirement getRequirementSample2() {
        return new Requirement().id(2L).prompt("prompt2").answer("answer2");
    }

    public static Requirement getRequirementRandomSampleGenerator() {
        return new Requirement().id(longCount.incrementAndGet()).prompt(UUID.randomUUID().toString()).answer(UUID.randomUUID().toString());
    }
}
