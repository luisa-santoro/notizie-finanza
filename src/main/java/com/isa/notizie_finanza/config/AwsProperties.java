package com.isa.notizie_finanza.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsProperties {

    public String getAccessKey() {
        return System.getenv("AWS_ACCESS_KEY_ID");
    }

    public String getSecretKey() {
        return System.getenv("AWS_SECRET_ACCESS_KEY");
    }

    public String getRegion() {
        return System.getenv("AWS_REGION");
    }

    public String getS3BucketName() {
        return System.getenv("AWS_BUCKET_NAME");
    }
}
