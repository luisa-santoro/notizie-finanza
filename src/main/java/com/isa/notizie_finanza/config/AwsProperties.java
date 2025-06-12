package com.isa.notizie_finanza.config;

import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class AwsProperties {

    private final Dotenv dotenv = Dotenv.configure()
            .directory("./")         // directory in cui si trova .env
            .filename(".env")        // nome del file .env
            .load();

    public String getAccessKey() {
        return dotenv.get("AWS_ACCESS_KEY");
    }

    public String getSecretKey() {
        return dotenv.get("AWS_SECRET_KEY");
    }

    public String getRegion() {
        return dotenv.get("AWS_REGION");
    }

    public String getS3BucketName() {
        return dotenv.get("AWS_BUCKET_NAME");
    }
}


