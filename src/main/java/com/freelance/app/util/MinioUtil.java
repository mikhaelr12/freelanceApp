package com.freelance.app.util;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class MinioUtil {

    private static final int PART_SIZE = 10 * 1024 * 1024;
    private final MinioClient minio;

    public MinioUtil(MinioClient minio) {
        this.minio = minio;
    }

    public void createBucketIfMissing(String bucket) throws Exception {
        if (!minio.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minio.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    public void uploadFile(String bucketName, String objectName, InputStream inputStream)
        throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minio.putObject(
            PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(inputStream, inputStream.available(), -1).build()
        );
    }

    public InputStream download(String bucket, String object) throws Exception {
        return minio.getObject(GetObjectArgs.builder().bucket(bucket).object(object).build());
    }

    public void delete(String bucket, String object) throws Exception {
        minio.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(object).build());
    }

    public StatObjectResponse stat(String bucket, String object) throws Exception {
        return minio.statObject(StatObjectArgs.builder().bucket(bucket).object(object).build());
    }

    public String presignedGet(String bucket, String object, int seconds) throws Exception {
        return minio.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucket).object(object).expiry(seconds).build()
        );
    }

    public String getImageAsBase64(String bucket, String object) {
        try (InputStream stream = download(bucket, object)) {
            byte[] imageBytes = IOUtils.toByteArray(stream);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
