package ru.forum.whale.space.api.service;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.UserDto;
import ru.forum.whale.space.api.exception.*;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SessionUtilService sessionUtilService;
    private final ModelMapper modelMapper;
    private final MinioClient minioClient;

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

    public Page<UserDto> findAll(Sort sort, int page, int size) {
        Page<User> usersPage = userRepository.findAll(PageRequest.of(page, size, sort));
        return usersPage.map(this::convertToUserDto);
    }

    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        return convertToUserDto(user);
    }

    public UserDto findYourself() {
        return convertToUserDto(sessionUtilService.findCurrentUser());
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

    private UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
