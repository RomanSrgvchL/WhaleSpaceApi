package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.docs.user.*;
import ru.forum.whale.space.api.dto.UserDto;
import ru.forum.whale.space.api.dto.UserProfileDto;
import ru.forum.whale.space.api.dto.response.PageResponseDto;
import ru.forum.whale.space.api.service.UserService;
import ru.forum.whale.space.api.util.Messages;
import ru.forum.whale.space.api.util.SortOrder;
import ru.forum.whale.space.api.util.UserSortFields;

@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Операции с пользователями")
public class UserController {
    private final UserService userService;

    @GetAllUsersDocs
    @GetMapping
    public ResponseEntity<PageResponseDto<UserDto>> getAll(
            @RequestParam(value = "sort", defaultValue = "CREATED_AT") UserSortFields sort,
            @RequestParam(value = "order", defaultValue = "DESC") SortOrder order,
            @RequestParam(value = "page", defaultValue = "0")
            @PositiveOrZero(message = Messages.PAGE_POSITIVE_OR_ZERO) int page,
            @RequestParam(value = "size", defaultValue = "6")
            @Positive(message = Messages.SIZE_POSITIVE) int size) {
        Page<UserDto> usersPage = userService.findAll(Sort.by(order.getDirection(), sort.getFieldName()), page, size);

        PageResponseDto<UserDto> pageResponseDto = PageResponseDto.<UserDto>builder()
                .content(usersPage.getContent())
                .page(page)
                .size(size)
                .totalPages(usersPage.getTotalPages())
                .totalElements(usersPage.getTotalElements())
                .isLast(usersPage.isLast())
                .build();

        return ResponseEntity.ok(pageResponseDto);
    }

    @GetMeDocs
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe() {
        return ResponseEntity.ok(userService.findYourself());
    }

    @GetUserByIdDocs
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable @Positive(message = Messages.ID_POSITIVE) long id) {
        UserDto userDto = userService.findById(id);
        return ResponseEntity.ok(userDto);
    }

    @GetUserByUsernameDocs
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getByUsername(@PathVariable String username) {
        UserDto userDto = userService.findByUsername(username);
        return ResponseEntity.ok(userDto);
    }

    @UpdateUserDocs
    @PatchMapping("/me")
    public ResponseEntity<UserProfileDto> update(@RequestBody @Valid UserProfileDto userProfileDto) {
        UserProfileDto updatedUser = userService.update(userProfileDto);
        return ResponseEntity.ok(updatedUser);
    }
}
