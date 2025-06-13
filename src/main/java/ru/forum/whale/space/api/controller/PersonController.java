package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.PersonDto;
import ru.forum.whale.space.api.dto.response.AvatarResponseDto;
import ru.forum.whale.space.api.dto.response.PageResponseDto;
import ru.forum.whale.space.api.dto.response.UserResponseDto;
import ru.forum.whale.space.api.service.PersonService;

import java.util.Set;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Операции с пользователями")
public class PersonController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("username", "createdAt");
    private final PersonService personService;

    @GetMapping
    public ResponseEntity<PageResponseDto<PersonDto>> getAll(
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size) {
        if (page < 0 || size <= 0) {
            throw new ValidationException("Неверные значения параметров, допустимо: page ≥ 0, size > 0");
        }

        Sort.Direction direction = "asc".equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Page<PersonDto> peoplePage;

        if (ALLOWED_SORT_FIELDS.contains(sort)) {
            peoplePage = personService.findAll(Sort.by(direction, sort), page, size);
        } else {
            peoplePage = personService.findAll(Sort.by(direction, "createdAt"), page, size);
        }

        PageResponseDto<PersonDto> pageResponseDto = PageResponseDto.<PersonDto>builder()
                .content(peoplePage.getContent())
                .page(page)
                .size(size)
                .totalPages(peoplePage.getTotalPages())
                .totalElements(peoplePage.getTotalElements())
                .isLast(peoplePage.isLast())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(pageResponseDto);
    }

    @GetMapping("/me")
    public ResponseEntity<PersonDto> getMe() {
        return ResponseEntity.status(HttpStatus.OK).body(personService.findYourself());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<PersonDto> getByName(@PathVariable("username") String name) {
        PersonDto personDto = personService.findByUsername(name);
        return ResponseEntity.status(HttpStatus.OK).body(personDto);
    }

    @GetMapping("/avatar/{filename}")
    public ResponseEntity<AvatarResponseDto> getAvatarUrl(@PathVariable String filename) {
        String avatarUrl = personService.generateAvatarUrl(filename);

        AvatarResponseDto avatarResponseDto = new AvatarResponseDto(true,
                "Временная ссылка на аватар успешно сгенерирована!", avatarUrl);
        return ResponseEntity.status(HttpStatus.OK).body(avatarResponseDto);
    }

    @PostMapping("/avatar")
    public ResponseEntity<AvatarResponseDto> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                          HttpServletRequest request) {
        String avatarFileName = personService.uploadAvatar(file, request);

        AvatarResponseDto avatarResponseDto = new AvatarResponseDto(true, "Аватар успешно загружен!",
                avatarFileName);
        return ResponseEntity.status(HttpStatus.CREATED).body(avatarResponseDto);
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<UserResponseDto> deleteAvatar(HttpServletRequest request) {
        personService.deleteAvatar(request);

        UserResponseDto userResponseDto = new UserResponseDto(true, "Аватар успешно удалён!");
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }
}
