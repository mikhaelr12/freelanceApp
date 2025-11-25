package com.freelance.app.util;

import com.freelance.app.config.ApplicationProperties;
import com.freelance.app.domain.FileObject;
import com.freelance.app.repository.FileObjectRepository;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class FileProcessUtil {

    private final FileObjectRepository fileObjectRepository;
    private final ApplicationProperties applicationProperties;
    private final MinioUtil minioUtil;

    public FileProcessUtil(FileObjectRepository fileObjectRepository, ApplicationProperties applicationProperties, MinioUtil minioUtil) {
        this.fileObjectRepository = fileObjectRepository;
        this.applicationProperties = applicationProperties;
        this.minioUtil = minioUtil;
    }

    public Mono<FileObject> processFile(FilePart file, String login, String destination) {
        final String bucket = applicationProperties.getMinio().getBucketName();
        final String original = Optional.of(file.filename()).orElse("file.bin");
        final String ext = original.contains(".") ? original.substring(original.lastIndexOf('.') + 1) : "bin";
        final String contentType = Optional.ofNullable(file.headers().getContentType())
            .map(MediaType::toString)
            .orElse("application/octet-stream");
        final String objectKey = "users/%s/%s/%s.%s".formatted(login, destination, UUID.randomUUID(), ext);

        return DataBufferUtils.join(file.content()).flatMap(buf -> {
            byte[] bytes = new byte[buf.readableByteCount()];
            buf.read(bytes);
            DataBufferUtils.release(buf);

            String checksum = sha256(bytes);
            long size = bytes.length;

            Mono<Void> uploadMono = Mono.fromCallable(() -> {
                minioUtil.createBucketIfMissing(bucket);
                try (InputStream in = new ByteArrayInputStream(bytes)) {
                    minioUtil.uploadFile(bucket, objectKey, in);
                }
                return (Void) null;
            }).subscribeOn(Schedulers.boundedElastic());

            return uploadMono.then(
                fileObjectRepository.save(
                    new FileObject()
                        .bucket(bucket)
                        .objectKey(objectKey)
                        .contentType(contentType)
                        .fileSize(size)
                        .checksum(checksum)
                        .durationSeconds(0)
                        .createdDate(Instant.now())
                )
            );
        });
    }

    private static String sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return HexFormat.of().formatHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
