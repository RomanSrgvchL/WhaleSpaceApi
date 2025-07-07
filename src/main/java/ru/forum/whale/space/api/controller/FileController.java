package ru.forum.whale.space.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.forum.whale.space.api.service.MinioService;
import ru.forum.whale.space.api.util.StorageBucket;

import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final MinioService minioService;

    @GetMapping("/presigned")
    public ResponseEntity<String> getPresignedUrl(@RequestParam("filename") String filename,
                                                  @RequestParam("bucket") StorageBucket bucket) {
        String bucketName = bucket.getBucketName();

        String presignedUrl = minioService.generatePresignedUrl(bucketName, filename);

        return ResponseEntity.status(HttpStatus.OK).body(presignedUrl);
    }

    @GetMapping("/presigned/batch")
    public ResponseEntity<List<String>> getPresignedUrls(@RequestParam("filenames") List<String> filenames,
                                                         @RequestParam("bucket") StorageBucket bucket) {
        String bucketName = bucket.getBucketName();

        List<String> presignedUrls = minioService.generatePresignedUrls(bucketName, filenames);

        return ResponseEntity.status(HttpStatus.OK).body(presignedUrls);
    }
}
