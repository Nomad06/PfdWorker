package com.bubalex.aws;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class AwsS3ServiceImpl implements AwsS3Service {
    @Override
    public byte[] getObject() {
        S3Client s3Client = S3Client.builder()
                .region(Region.US_WEST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("SOME_KEY", "SOME_SECRET")
                ))
                .build();
        ResponseInputStream<GetObjectResponse> objectResponse = s3Client.getObject(GetObjectRequest.builder()
                .bucket("test-bucket")
                .key("HelloWorld.pdf")
                .build());
        return new byte[0];
    }
}
