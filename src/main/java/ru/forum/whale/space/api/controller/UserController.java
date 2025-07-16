package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.UserDto;
import ru.forum.whale.space.api.dto.UserProfileDto;
import ru.forum.whale.space.api.dto.response.PageResponseDto;
import ru.forum.whale.space.api.service.UserService;

import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Операции с пользователями")
public class UserController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("username", "createdAt");
    private final UserService userService;

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

        return ResponseEntity.ok(pageResponseDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe() {
        return ResponseEntity.ok(userService.findYourself());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable long id) {
        UserDto userDto = userService.findById(id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getByName(@PathVariable String username) {
        UserDto userDto = userService.findByUsername(username);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileDto> update(@RequestBody @Valid UserProfileDto userProfileDto) {
        UserProfileDto updatedUser = userService.update(userProfileDto);
        return ResponseEntity.ok(updatedUser);
    }
}
