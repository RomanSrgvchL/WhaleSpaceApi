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
import ru.forum.whale.space.api.util.FileUtil;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAvatarService {
    private final UserRepository userRepository;
    private final SessionUtilService sessionUtilService;
    private final MinioService minioService;

    private static final String FOLDER_PATTERN = "avatar-%d";
    public static final String USER_AVATARS_BUCKET = StorageBucket.USER_AVATARS_BUCKET.getBucketName();

    public static final int MIN_AVATAR_WIDTH = 400;
    public static final int MIN_AVATAR_HEIGHT = 400;

    @PostConstruct
    private void initUserAvatarsBucket() {
        minioService.initBucket(USER_AVATARS_BUCKET);
    }

    @Transactional
    public String upload(MultipartFile file) {
        FileUtil.validateFileContentType(file);

        byte[] imageBytes = minioService.validateImage(file, MIN_AVATAR_WIDTH, MIN_AVATAR_HEIGHT);

        User currentUser = sessionUtilService.findCurrentUser();

        String avatarFileName = FOLDER_PATTERN.formatted(currentUser.getId());

        minioService.loadFile(USER_AVATARS_BUCKET, avatarFileName, file, imageBytes);

        currentUser.setAvatarFileName(avatarFileName);
        userRepository.save(currentUser);

        return avatarFileName;
    }

    @Transactional
    public void delete() {
        User currentUser = sessionUtilService.findCurrentUser();

        String avatarFileName = currentUser.getAvatarFileName();

        if (avatarFileName == null) {
            throw new IllegalOperationException("Ошибка при удалении: аватар не установлен");
        }

        minioService.deleteFile(USER_AVATARS_BUCKET, avatarFileName);

        currentUser.setAvatarFileName(null);
        userRepository.save(currentUser);
    }
}
