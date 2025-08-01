package ru.forum.whale.space.api.util;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.exception.IllegalOperationException;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtil {
    public static void validateFiles(List<MultipartFile> files) {
        if (files != null && !files.isEmpty()) {
            if (files.size() > 3) {
                throw new IllegalOperationException("Можно прикрепить не более 3 файлов");
            } else {
                for (var file : files) {
                    if (isInvalidContentType(file)) {
                        throw new IllegalOperationException("Файлы должны быть формата PNG или JPG/JPEG");
                    }
                }
            }
        }
    }

    public static void validateFileContentType(MultipartFile file) {
        if (isInvalidContentType(file)) {
            throw new IllegalOperationException("Файл должен быть формата PNG или JPG/JPEG");
        }
    }

    private static boolean isInvalidContentType(MultipartFile file) {
        String contentType = file.getContentType();
        return !(IMAGE_JPEG_VALUE.equals(contentType) || IMAGE_PNG_VALUE.equals(contentType));
    }
}
