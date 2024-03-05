package me.universi.minioConfig;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

@Configuration
public class MinioConfig {

    // @Value("${MINIO_ACESSKEY}")
    // private String accessKey;

    // @Value("${MINIO_SECRET}")
    // private String secretKey;

    // @Value("${MINIO_URL}")
    // private String minioUrl;

    // @Value("${MINIO_BUCKET}")
    // private String bucketName;

    // @Value("${MINIO_REGION}")
    // private String region;

    // @Value("$MINIO_POLICY")
    // private String policy;

    @Bean
    public MinioClient minioClient() throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, 
    InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {
        MinioClient minioClient =
                MinioClient.builder()
                        .credentials("9JwDwrutRQrywpBFH9ks", "kEtizmyDs70XSm0JVIbLACH2zDv1TOeCawhZ7qWb")
                        .endpoint("http://localhost:9000")
                        .region("us-east-1")
                        .build();
        if(!minioClient.bucketExists(BucketExistsArgs.builder().bucket("universime").build()))
        {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("universime").region("us-east-1").build());
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket("universime").config("public").build());
        }
        
        return minioClient;
    }
}

