package ru.forum.whale.space.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.forum.whale.space.api.annotation.CustomWebMvcTest;
import ru.forum.whale.space.api.enums.StorageBucket;
import ru.forum.whale.space.api.service.MinioService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.forum.whale.space.api.util.TestUtil.*;

@CustomWebMvcTest(FileController.class)
class FileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MinioService minioService;

    private static final String BASE_URL = "/files";

    @Test
    void getPresignedUrl_thenReturnGeneratedPresignedUrl() throws Exception {
        StorageBucket bucket = StorageBucket.USER_AVATARS_BUCKET;

        String filePresignedUrl = "url";

        when(minioService.generatePresignedUrl(bucket.getBucketName(), FILENAME)).thenReturn(filePresignedUrl);

        mockMvc.perform(get(BASE_URL + "/presigned")
                        .param("fileName", FILENAME)
                        .param("bucket", bucket.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.url").value(filePresignedUrl));
    }

    @Test
    void getPresignedUrl_whenInvalidBucket_thenReturnBadRequest() throws Exception {
        createBadRequestResponse(mockMvc.perform(get(BASE_URL + "/presigned")
                        .param("fileName", FILENAME)
                        .param("bucket", INVALID)));
    }

    @Test
    void getPresignedUrls_thenReturnGeneratedPresignedUrls() throws Exception {
        StorageBucket bucket = StorageBucket.USER_AVATARS_BUCKET;

        List<String> fileNames = List.of("file1", "file2");
        List<String> filePresignedUrl = List.of("url1", "url2");

        when(minioService.generatePresignedUrls(bucket.getBucketName(), fileNames)).thenReturn(filePresignedUrl);

        mockMvc.perform(get(BASE_URL + "/presigned/batch")
                        .param("fileNames", fileNames.toArray(new String[0]))
                        .param("bucket", bucket.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value(filePresignedUrl.getFirst()))
                .andExpect(jsonPath("$[1]").value(filePresignedUrl.getLast()));
    }


    @Test
    void getPresignedUrls_whenInvalidBucket_thenReturnBadRequest() throws Exception {
        List<String> fileNames = List.of("file");

        createBadRequestResponse(mockMvc.perform(get(BASE_URL + "/presigned/batch")
                .param("fileNames", fileNames.toArray(new String[0]))
                .param("bucket", INVALID)));
    }
}