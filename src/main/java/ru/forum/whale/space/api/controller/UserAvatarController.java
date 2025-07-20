package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.docs.useravatar.DeleteAvatarDocs;
import ru.forum.whale.space.api.docs.useravatar.UploadAvatarDocs;
import ru.forum.whale.space.api.dto.response.FileNameResponseDto;
import ru.forum.whale.space.api.service.UserAvatarService;

@RestController
@RequestMapping("/user/avatar")
@RequiredArgsConstructor
@Tag(name = "Аватар пользователя", description = "Операции с аватаром текущего пользователя (загрузка/удаление)")
public class UserAvatarController {
    private final UserAvatarService userAvatarService;

    @UploadAvatarDocs
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileNameResponseDto> uploadAvatar(@RequestPart("file") MultipartFile file) {
        String avatarFileName = userAvatarService.uploadAvatar(file);
        FileNameResponseDto fileNameResponseDto = new FileNameResponseDto(avatarFileName);
        return ResponseEntity.status(HttpStatus.CREATED).body(fileNameResponseDto);
    }

    @DeleteAvatarDocs
    @DeleteMapping
    public ResponseEntity<Void> deleteAvatar() {
        userAvatarService.deleteAvatar();
        return ResponseEntity.noContent().build();
    }
}
