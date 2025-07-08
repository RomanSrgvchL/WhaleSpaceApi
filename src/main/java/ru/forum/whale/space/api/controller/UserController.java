package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.UserDto;
import ru.forum.whale.space.api.dto.UserProfileDto;
import ru.forum.whale.space.api.dto.response.AvatarResponseDto;
import ru.forum.whale.space.api.dto.response.PageResponseDto;
import ru.forum.whale.space.api.service.UserAvatarService;
import ru.forum.whale.space.api.service.UserService;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Операции с пользователями")
public class UserController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("username", "createdAt");
    private final UserService userService;
    private final UserAvatarService userAvatarService;

    @GetMapping
    public ResponseEntity<PageResponseDto<UserDto>> getAll(
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size) {
        if (page < 0 || size <= 0) {
            throw new ValidationException("Неверные значения параметров, допустимо: page ≥ 0, size > 0");
        }

        Sort.Direction direction = "asc".equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC;

        if (!ALLOWED_SORT_FIELDS.contains(sort)) {
            sort = "createdAt";
        }

        Page<UserDto> usersPage = userService.findAll(Sort.by(direction, sort), page, size);

        PageResponseDto<UserDto> pageResponseDto = PageResponseDto.<UserDto>builder()
                .content(usersPage.getContent())
                .page(page)
                .size(size)
                .totalPages(usersPage.getTotalPages())
                .totalElements(usersPage.getTotalElements())
                .isLast(usersPage.isLast())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(pageResponseDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findYourself());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getByName(@PathVariable Long id) {
        UserDto userDto = userService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getByName(@PathVariable String username) {
        UserDto userDto = userService.findByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    @GetMapping("/avatar/{fileName}")
    public ResponseEntity<AvatarResponseDto> getAvatarUrl(@PathVariable String fileName) {
        String avatarUrl = userAvatarService.generateAvatarUrl(fileName);

        AvatarResponseDto avatarResponseDto = AvatarResponseDto.buildSuccess(
                "Временная ссылка на аватар успешно сгенерирована!", avatarUrl);
        return ResponseEntity.status(HttpStatus.OK).body(avatarResponseDto);
    }

    @PostMapping("/avatar")
    public ResponseEntity<AvatarResponseDto> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatarFileName = userAvatarService.uploadAvatar(file);

        AvatarResponseDto avatarResponseDto = AvatarResponseDto.buildSuccess("Аватар успешно загружен!",
                avatarFileName);
        return ResponseEntity.status(HttpStatus.CREATED).body(avatarResponseDto);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileDto> update(@RequestBody @Valid UserProfileDto userProfileDto,
                                                 BindingResult bindingResult) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);
        UserProfileDto updatedUser = userService.update(userProfileDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<Void> deleteAvatar() {
        userAvatarService.deleteAvatar();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
