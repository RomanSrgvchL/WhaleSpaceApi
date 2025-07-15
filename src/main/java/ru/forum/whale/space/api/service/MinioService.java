package ru.forum.whale.space.api.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.exception.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {
    private final MinioClient minioClient;
    private static final int MIN_IMAGE_WIDTH = 150;
    private static final int MIN_IMAGE_HEIGHT = 150;

    public void initBucket(String bucketName) {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new GeneralMinioException("Ошибка при инициализации MinIO бакета: " + e.getMessage());
        }
    }

    public String generatePresignedUrl(String bucketName, String fileName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            throw new ResourceNotFoundException(String.format("Файл '%s' не найден", fileName));
        }
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(12, TimeUnit.HOURS)
                            .build());
        } catch (Exception e) {
            throw new GeneralMinioException(String.format("Ошибка при генерации временной ссылки на файл '%s': %s",
                    fileName, e.getMessage()));
        }
    }

    public List<String> generatePresignedUrls(String bucketName, List<String> fileNames) {
        List<String> presignedUrls = new ArrayList<>();

        for (var fileName : fileNames) {
            presignedUrls.add(generatePresignedUrl(bucketName, fileName));
        }

        return presignedUrls;
    }

    public List<String> uploadImages(String bucketName, List<MultipartFile> files, String folder) {
        List<String> fileNames = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                byte[] imageBytes = validateImage(file, MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT);

                String fileName = folder + "/" + UUID.randomUUID();

                loadFile(bucketName, fileName, file, imageBytes);

                fileNames.add(fileName);
            }
            return fileNames;
        } catch (IllegalOperationException e) {
            deleteFiles(bucketName, fileNames);
            throw e;
        } catch (Exception e) {
            deleteFiles(bucketName, fileNames);
            throw new MinioUploadException("Ошибка загрузки файлов: " + e.getMessage());
        }
    }

    public void deleteFile(String bucketName, String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new MinioDeleteException(String.format("Не удалось удалить файл '%s': %s", fileName, e.getMessage()));
        }
    }

    public void deleteFiles(String bucketName, List<String> fileNames) {
        for (String fileName : fileNames) {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .build()
                );
            } catch (Exception e) {
                log.error("Ошибка при удалении файла {}: {}", fileName, e.getMessage());
            }
        }
    }

    public byte[] validateImage(MultipartFile file, int minImageWidth, int minImageHeight) {
        byte[] imageBytes;

        try (InputStream inputStream = file.getInputStream()) {
            imageBytes = inputStream.readAllBytes();

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (image == null) {
                throw new IllegalOperationException("Невалидный файл изображения");
            }

            if (image.getWidth() < minImageWidth || image.getHeight() < minImageHeight) {
                throw new IllegalOperationException(String.format("Минимальный размер изображения — %dx%d пикселей",
                        minImageWidth, minImageHeight));
            }
        } catch (IOException e) {
            throw new MinioUploadException("Ошибка чтения файла: " + e.getMessage());
        }

        return imageBytes;
    }

    public void loadFile(String bucketName, String fileName, MultipartFile file, byte[] bytes) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(new ByteArrayInputStream(bytes), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new MinioUploadException(String.format("Не удалось загрузить файл '%s': '%s' ",
                    fileName, e.getMessage()));
        }
    }
}
