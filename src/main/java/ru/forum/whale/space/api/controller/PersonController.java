package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.PersonDto;
import ru.forum.whale.space.api.dto.response.AvatarResponseDto;
import ru.forum.whale.space.api.dto.response.UserResponseDto;
import ru.forum.whale.space.api.service.PersonService;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Операции с пользователями")
public class PersonController {
    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonDto>> getAll(@RequestParam(value = "sort", defaultValue = "-") String sort,
                                                  @RequestParam(value = "order", defaultValue = "-") String order) {
        List<PersonDto> people;

        if (Objects.equals(sort, "username") || Objects.equals(sort, "createdAt")) {
            if (Objects.equals(order, "asc")) {
                people = personService.findAll(Sort.by(sort).ascending());
            } else {
                people = personService.findAll(Sort.by(sort).descending());
            }
        } else {
            people = personService.findAll(Sort.by("createdAt").descending());
        }

        Iterator<PersonDto> iterator = people.iterator();
        while (iterator.hasNext()) {
            if (Objects.equals(iterator.next().getUsername(),
                    SecurityContextHolder.getContext().getAuthentication().getName())) {
                iterator.remove();
                break;
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(people);
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
