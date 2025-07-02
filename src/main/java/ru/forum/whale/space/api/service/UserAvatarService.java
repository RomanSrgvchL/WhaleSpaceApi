package ru.forum.whale.space.api.service;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.exception.*;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAvatarService {
    private final UserRepository userRepository;
    private final MinioClient minioClient;
    private final SessionUtilService sessionUtilService;

    @Value("${minio.avatar-bucket}")
    private String avatarBucket;

    @PostConstruct
    private void initAvatarBucket() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(avatarBucket)
                            .build()
            );
            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(avatarBucket)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new GeneralMinioException("Ошибка при инициализации MinIO бакета: " + e.getMessage());
        }
    }

    public String generateAvatarUrl(String filename) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(avatarBucket)
                    .object(filename)
                    .build());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Аватар не найден");
        }
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(avatarBucket)
                            .object(filename)
                            .expiry(5, TimeUnit.MINUTES)
                            .build());
        } catch (Exception e) {
            throw new GeneralMinioException("Ошибка при генерации временной ссылки на аватар: " + e.getMessage());
        }
    }

    @Transactional
    public String uploadAvatar(MultipartFile file) {
        User currentUser = sessionUtilService.findCurrentUser();

        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new IllegalOperationException("Файл должен быть формата PNG или JPG/JPEG");
        }

        try {
            String avatarFileName = "avatar-" + currentUser.getId();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(avatarFileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );

            currentUser.setAvatarFileName(avatarFileName);
            userRepository.save(currentUser);

            return avatarFileName;
        } catch (Exception e) {
            throw new AvatarUploadException("Не удалось загрузить аватар: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteAvatar() {
        User currentUser = sessionUtilService.findCurrentUser();

        String avatarFileName = currentUser.getAvatarFileName();

        if (avatarFileName == null) {
            throw new IllegalOperationException("Ошибка при удалении: аватар не установлен");
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(avatarFileName)
                            .build()
            );

            currentUser.setAvatarFileName(null);
            userRepository.save(currentUser);
        } catch (Exception e) {
            throw new AvatarDeleteException("Не удалось удалить аватар: " + e.getMessage());
        }
    }
}
