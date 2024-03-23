package io.muzoo.ssc.project.backend.User;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
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
        // Extracting the file extension
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf('.')) : "";
        // Incorporating the username, current timestamp, and file extension in the file key
        String fileKey = username + "_" + System.currentTimeMillis() + fileExtension;

        try {
            File tempFile = Files.createTempFile(null, fileExtension).toFile();
            file.transferTo(tempFile);

            // Create a PutObjectRequest, setting the ACL to PublicRead
            s3client.putObject(new PutObjectRequest(bucketName, fileKey, tempFile)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            // It's a good practice to delete the temporary file after the upload
            tempFile.delete();

            return fileKey; // Returning the file key for reference
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file", e);
        }
    }
}
