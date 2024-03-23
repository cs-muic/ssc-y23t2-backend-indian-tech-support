package io.muzoo.ssc.project.backend.User;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class StorageService {

    @Autowired
    private AmazonS3 s3client;

    @Value("${space.name}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String username) {
        String fileKey = username;
        try {
            File tempFile = Files.createTempFile(null, null).toFile();
            file.transferTo(tempFile);
            s3client.putObject(new PutObjectRequest(bucketName, fileKey, tempFile));
            return fileKey; // Return the S3 object key
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
