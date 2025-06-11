package com.isa.notizie_finanza.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.isa.notizie_finanza.config.AwsProperties;
import com.isa.notizie_finanza.exception.FileUploadException;


@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    private final AwsProperties awsProperties;

    public S3Service(AmazonS3 amazonS3, AwsProperties awsProperties) {
        this.amazonS3 = amazonS3;
        this.awsProperties = awsProperties;
    }

    public String uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(awsProperties.getS3BucketName(), fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new FileUploadException("Errore durante il caricamento del file", e);

        }

        return amazonS3.getUrl(awsProperties.getS3BucketName(), fileName).toString();
    }
}
