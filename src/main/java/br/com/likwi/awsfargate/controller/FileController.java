package br.com.likwi.awsfargate.controller;

import br.com.likwi.awsfargate.model.File;
import br.com.likwi.awsfargate.model.URLResponse;
import br.com.likwi.awsfargate.repository.FileRepository;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {

    @Value("${aws.s3.bucket.file.name}")
    private String bucketName;

    private AmazonS3 amazonS3;
    private FileRepository fileRepository;

    public FileController(AmazonS3 amazonS3, FileRepository fileRepository) {
        this.amazonS3 = amazonS3;
        this.fileRepository = fileRepository;
    }

    @PostMapping
    public ResponseEntity<URLResponse> createURLFile() {

        Instant expirationDTime = Instant.now().plus(Duration.ofMinutes(5));
        String processId = UUID.randomUUID().toString();
        final GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
                this.bucketName, processId)
                .withMethod(HttpMethod.PUT)
                .withExpiration(Date.from(expirationDTime));

        URLResponse urlResponse = new URLResponse(
                this.amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString(),
                expirationDTime.getEpochSecond());

        return new ResponseEntity<URLResponse>(urlResponse, HttpStatus.OK);
    }

    @GetMapping
    public Iterable<File> findAll() {
        return this.fileRepository.findAll();
    }
    @GetMapping("/name")
    public Iterable<File> name(@RequestParam String name) {
        return this.fileRepository.findAllByCustomer(name);
    }
}
