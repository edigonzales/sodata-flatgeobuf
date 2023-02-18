package ch.so.agi.sodata.fgb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class AmazonS3StorageService implements StorageService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${app.s3AccessKey}")
    private String s3AccessKey;

    @Value("${app.s3SecretKey}")
    private String s3SecretKey;

    @Value("${app.s3Bucket}")
    private String s3Bucket;

    @Value("${app.s3Region}")
    private String s3Region;

    private S3Client s3client;
    
    @PostConstruct
    public void init() {
        AwsCredentialsProvider creds = StaticCredentialsProvider
                .create(AwsBasicCredentials.create(s3AccessKey, s3SecretKey));
        Region region = Region.of(s3Region);
        s3client = S3Client.builder().credentialsProvider(creds).region(region).build();
    }

    @Override
    public void store(File file, String filename, String location) throws IOException {
        try {
            String fileKey = location + "/" + filename;
            RequestBody requestBody = RequestBody.fromInputStream(new FileInputStream(file), file.length());
            PutObjectResponse resp = s3client.putObject(PutObjectRequest.builder().bucket(s3Bucket).key(fileKey).build(), requestBody);
            s3client.putObjectAcl(PutObjectAclRequest.builder().bucket(s3Bucket).key(fileKey).acl(ObjectCannedACL.PUBLIC_READ).build());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Could not store uploaded file: " + e.getMessage());
        }  
    }

    @Override
    public File load(String filename) {
        return null;
    }

    @Override
    public void delete(String filename) {

    }

}
