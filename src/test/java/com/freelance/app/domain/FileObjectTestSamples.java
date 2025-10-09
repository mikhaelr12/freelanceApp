package com.freelance.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FileObjectTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static FileObject getFileObjectSample1() {
        return new FileObject()
            .id(1L)
            .bucket("bucket1")
            .objectKey("objectKey1")
            .contentType("contentType1")
            .fileSize(1L)
            .checksum("checksum1")
            .durationSeconds(1)
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static FileObject getFileObjectSample2() {
        return new FileObject()
            .id(2L)
            .bucket("bucket2")
            .objectKey("objectKey2")
            .contentType("contentType2")
            .fileSize(2L)
            .checksum("checksum2")
            .durationSeconds(2)
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static FileObject getFileObjectRandomSampleGenerator() {
        return new FileObject()
            .id(longCount.incrementAndGet())
            .bucket(UUID.randomUUID().toString())
            .objectKey(UUID.randomUUID().toString())
            .contentType(UUID.randomUUID().toString())
            .fileSize(longCount.incrementAndGet())
            .checksum(UUID.randomUUID().toString())
            .durationSeconds(intCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
