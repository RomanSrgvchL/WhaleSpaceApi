package ru.forum.whale.space.api.util;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.exception.IllegalOperationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static ru.forum.whale.space.api.util.TestUtil.createMockFiles;
import static ru.forum.whale.space.api.util.TestUtil.createMockMultipartFile;

class FileUtilTest {
    @Test
    void validateFiles_whenFilesCountExceeds_thenThrowIllegalOperationException() {
        List<MultipartFile> files = createMockFiles(4);

        IllegalOperationException e = assertThrows(IllegalOperationException.class,
                () -> FileUtil.validateFiles(files));

        assertEquals("Можно прикрепить не более 3 файлов", e.getMessage());
    }

    @Test
    void validateFiles_whenUnsupportedContentType_thenThrowIllegalOperationException() {
        List<MultipartFile> files = new ArrayList<>();

        files.add(createMockMultipartFile(IMAGE_PNG_VALUE));
        files.add(createMockMultipartFile(IMAGE_GIF_VALUE));

        IllegalOperationException e = assertThrows(IllegalOperationException.class,
                () -> FileUtil.validateFiles(files));

        assertEquals("Файлы должны быть формата PNG или JPG/JPEG", e.getMessage());
    }

    @Test
    void validateFiles_thenDoesNotThrow() {
        List<MultipartFile> files = new ArrayList<>();

        files.add(createMockMultipartFile(IMAGE_PNG_VALUE));
        files.add(createMockMultipartFile(IMAGE_JPEG_VALUE));

        assertDoesNotThrow(() -> FileUtil.validateFiles(files));
    }

    @Test
    void validateFileContentType_whenUnsupportedContentType_thenThrowIllegalOperationException() {
        MultipartFile file = createMockMultipartFile(IMAGE_GIF_VALUE);

        IllegalOperationException e = assertThrows(IllegalOperationException.class,
                () -> FileUtil.validateFileContentType(file));

        assertEquals("Файл должен быть формата PNG или JPG/JPEG", e.getMessage());
    }

    @Test
    void validateFileContentType_thenDoesNotThrow() {
        MultipartFile file1 = createMockMultipartFile(IMAGE_PNG_VALUE);
        MultipartFile file2 = createMockMultipartFile(IMAGE_JPEG_VALUE);

        assertDoesNotThrow(() -> FileUtil.validateFileContentType(file1));
        assertDoesNotThrow(() -> FileUtil.validateFileContentType(file2));
    }
}