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

    public String generatePresignedUrl(String bucketName, String filename) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .build());
        } catch (Exception e) {
            throw new ResourceNotFoundException(String.format("Файл '%s' не найден", filename));
        }
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(filename)
                            .expiry(12, TimeUnit.HOURS)
                            .build());
        } catch (Exception e) {
            throw new GeneralMinioException(String.format("Ошибка при генерации временной ссылки на файл '%s': %s",
                    filename, e.getMessage()));
        }
    }

    public List<String> generatePresignedUrls(String bucketName, List<String> filenames) {
        List<String> presignedUrls = new ArrayList<>();

        for (var filename : filenames) {
            presignedUrls.add(generatePresignedUrl(bucketName, filename));
        }

        return presignedUrls;
    }

    public List<String> uploadImages(String bucketName, List<MultipartFile> files, String folder) {
        List<String> fileNames = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                byte[] imageBytes;
                try (InputStream inputStream = file.getInputStream()) {
                    imageBytes = inputStream.readAllBytes();
                }

                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                if (image == null) {
                    throw new IllegalOperationException("Невалидный файл изображения");
                }

                if (image.getWidth() < MIN_IMAGE_WIDTH || image.getHeight() < MIN_IMAGE_HEIGHT) {
                    throw new IllegalOperationException(String.format(
                            "Минимальный размер изображения — %dx%d пикселей",
                            MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT)
                    );
                }

                String fileName = folder + "/" + UUID.randomUUID();
                try (InputStream uploadStream = new ByteArrayInputStream(imageBytes)) {
                    minioClient.putObject(
                            PutObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(fileName)
                                    .stream(uploadStream, imageBytes.length, -1)
                                    .contentType(file.getContentType())
                                    .build()
                    );
                    fileNames.add(fileName);
                }
            }
            return fileNames;
        } catch (IllegalOperationException e) {
            deleteUploadedFiles(bucketName, fileNames);
            throw e;
        } catch (Exception e) {
            deleteUploadedFiles(bucketName, fileNames);
            throw new MinioUploadException("Ошибка загрузки файлов: " + e.getMessage());
        }
    }

    public void deleteFile(String bucketName, String filename) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );
        } catch (Exception e) {
            throw new MinioDeleteException(String.format("Не удалось удалить файл '%s': %s", filename, e.getMessage()));
        }
    }

    public void deleteUploadedFiles(String bucketName, List<String> fileNames) {
        for (String uploadedFileName : fileNames) {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(uploadedFileName)
                                .build()
                );
            } catch (Exception e) {
                log.error("Ошибка при удалении файла {}: {}", uploadedFileName, e.getMessage());
            }
        }
    }
}
