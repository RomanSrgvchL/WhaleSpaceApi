package ru.forum.whale.space.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.enums.StorageBucket;
import ru.forum.whale.space.api.exception.IllegalOperationException;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;
import ru.forum.whale.space.api.util.FileUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.forum.whale.space.api.util.TestUtil.CURRENT_USER_ID;
import static ru.forum.whale.space.api.util.TestUtil.createUser;

@ExtendWith(MockitoExtension.class)
class UserAvatarServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionUtilService sessionUtilService;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private UserAvatarService userAvatarService;

    @Test
    void upload_whenReturnAvatarFileName() {
        MultipartFile file = mock(MultipartFile.class);

        User currentUser = createUser(CURRENT_USER_ID);

        String avatarFileName = "avatar-%d".formatted(currentUser.getId());

        byte[] imageBytes = new byte[0];

        when(minioService.validateImage(file, UserAvatarService.MIN_AVATAR_WIDTH,  UserAvatarService.MIN_AVATAR_HEIGHT))
                .thenReturn(imageBytes);
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);

        try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
            String result = userAvatarService.upload(file);

            mockedFileUtil.verify(() -> FileUtil.validateFileContentType(file));
            verify(minioService).loadFile(StorageBucket.USER_AVATARS_BUCKET.getBucketName(),
                    avatarFileName, file, imageBytes);
            verify(userRepository).save(currentUser);

            assertEquals(avatarFileName, result);
        }
    }

    @Test
    void delete_thenAvatarDoesNotUploaded_whenThrowIllegalOperationException() {
        User currentUser = createUser(CURRENT_USER_ID);

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);

        IllegalOperationException e = assertThrows(IllegalOperationException.class, userAvatarService::delete);

        assertEquals("Ошибка при удалении: аватар не установлен", e.getMessage());
    }

    @Test
    void delete_whenDeleteAvatar() {
        String avatarFileName = "avatar-%d".formatted(CURRENT_USER_ID);

        User currentUser = User.builder()
                .id(CURRENT_USER_ID)
                .avatarFileName(avatarFileName)
                .build();

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);

        userAvatarService.delete();

        verify(minioService).deleteFile(StorageBucket.USER_AVATARS_BUCKET.getBucketName(), avatarFileName);
        verify(userRepository).save(currentUser);

        assertNull(currentUser.getAvatarFileName());
    }
}