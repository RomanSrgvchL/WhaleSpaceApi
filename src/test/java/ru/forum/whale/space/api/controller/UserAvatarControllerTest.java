package ru.forum.whale.space.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.annotation.CustomWebMvcTest;
import ru.forum.whale.space.api.service.UserAvatarService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.forum.whale.space.api.util.TestUtil.createUnauthorizedResponse;

@CustomWebMvcTest(UserAvatarController.class)
class UserAvatarControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserAvatarService userAvatarService;

    private static final String BASE_URL = "/user/avatar";

    @WithMockUser
    @Test
    void uploadAvatar_thenUploadAvatar() throws Exception {
        MockMultipartFile postPart = createMockMultipartFile();

        String avatarFileName = "avatar";

        when(userAvatarService.upload(any(MultipartFile.class))).thenReturn(avatarFileName);

        mockMvc.perform(multipart(BASE_URL)
                        .file(postPart))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fileName").value(avatarFileName));
    }

    @Test
    void uploadAvatar_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        MockMultipartFile filePart = createMockMultipartFile();

        createUnauthorizedResponse(mockMvc.perform(multipart(BASE_URL)
                .file(filePart)));
    }

    @WithMockUser
    @Test
    void deleteAvatar_thenDeleteAvatar() throws Exception {
        mockMvc.perform(delete(BASE_URL))
                .andExpect(status().isNoContent());

        verify(userAvatarService).delete();
    }

    @Test
    void deleteAvatar_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(delete(BASE_URL)));
    }

    private MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "file",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                "not image".getBytes()
        );
    }
}