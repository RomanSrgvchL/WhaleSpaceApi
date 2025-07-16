package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.forum.whale.space.api.dto.response.UrlResponseDto;
import ru.forum.whale.space.api.service.MinioService;
import ru.forum.whale.space.api.util.StorageBucket;

import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "Файлы", description = "Генерация временных ссылок на файл или группу файлов")
public class FileController {
    private final MinioService minioService;

    @GetMapping("/presigned")
    public ResponseEntity<UrlResponseDto> getPresignedUrl(@RequestParam("fileName") String fileName,
                                                          @RequestParam("bucket") StorageBucket bucket) {
        String filePresignedUrl = minioService.generatePresignedUrl(bucket.getBucketName(), fileName);
        UrlResponseDto urlResponseDto = new UrlResponseDto(filePresignedUrl);
        return ResponseEntity.ok(urlResponseDto);
    }

    @GetMapping("/presigned/batch")
    public ResponseEntity<List<String>> getPresignedUrls(@RequestParam("fileNames") List<String> fileNames,
                                                         @RequestParam("bucket") StorageBucket bucket) {
        List<String> presignedUrls = minioService.generatePresignedUrls(bucket.getBucketName(), fileNames);
        return ResponseEntity.ok(presignedUrls);
    }
}
