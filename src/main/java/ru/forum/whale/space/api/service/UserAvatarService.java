package ru.forum.whale.space.api.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.exception.*;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;
import ru.forum.whale.space.api.enums.StorageBucket;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAvatarService {
    private final UserRepository userRepository;
    private final SessionUtilService sessionUtilService;
    private final MinioService minioService;
    private static final int MIN_AVATAR_WIDTH = 400;
    private static final int MIN_AVATAR_HEIGHT = 400;

    private static final String FOLDER_PATTERN = "avatar-%d";

    private  static final String avatarBucket = StorageBucket.USER_AVATARS_BUCKET.getBucketName();

    @PostConstruct
    private void initAvatarBucket() {
        minioService.initBucket(avatarBucket);
    }

    @Transactional
    public String uploadAvatar(MultipartFile file) {
        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new IllegalOperationException("Файл должен быть формата PNG или JPG/JPEG");
        }

        byte[] imageBytes = minioService.validateImage(file, MIN_AVATAR_WIDTH, MIN_AVATAR_HEIGHT);

        User currentUser = sessionUtilService.findCurrentUser();

        try {
            String avatarFileName = FOLDER_PATTERN.formatted(currentUser.getId());

            minioService.loadFile(avatarBucket, avatarFileName, file, imageBytes);

            currentUser.setAvatarFileName(avatarFileName);
            userRepository.save(currentUser);

            return avatarFileName;
        } catch (Exception e) {
            throw new MinioUploadException("Не удалось загрузить аватар: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteAvatar() {
        User currentUser = sessionUtilService.findCurrentUser();

        String avatarFileName = currentUser.getAvatarFileName();

        if (avatarFileName == null) {
            throw new IllegalOperationException("Ошибка при удалении: аватар не установлен");
        }

        minioService.deleteFile(avatarBucket, avatarFileName);

        currentUser.setAvatarFileName(null);
        userRepository.save(currentUser);
    }
}
