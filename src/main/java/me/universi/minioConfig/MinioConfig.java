package me.universi.minioConfig;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import me.universi.Sys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
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
@Conditional(MinioEnabledCondition.class)
public class MinioConfig {

    @Value("${minio.enabled}")
    public boolean enabled;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.url}")
    private String minioUrl;
    @Value("${minio.bucket}")
    public String bucketName;
    @Value("${minio.region}")
    private String region;
    @Value("${minio.policy}")
    private String policy;

    public static MinioConfig getInstance() {
        try {
            return Sys.context.getBean("minioConfig", MinioConfig.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isMinioEnabled() {
        return getInstance() != null && getInstance().enabled;
    }

    @Bean
    public MinioClient minioClient() throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException,
    InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {
        MinioClient minioClient =
                MinioClient.builder()
                        .credentials(accessKey, secretKey)
                        .endpoint(minioUrl)
                        .region(region)
                        .build();
                        
        if(!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()))
        {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).region(region).build());
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
        }

        return minioClient;
    }

    public String getUrl() { return this.minioUrl; }
}