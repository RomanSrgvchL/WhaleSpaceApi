package ru.forum.whale.space.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.PersonDto;
import ru.forum.whale.space.api.dto.request.UserRequestDto;
import ru.forum.whale.space.api.dto.response.UserResponseDto;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.service.AuthService;
import ru.forum.whale.space.api.service.PersonService;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PersonService personService;

    @GetMapping("/check")
    public ResponseEntity<UserResponseDto> checkLoginStatus() {
        UserResponseDto userResponseDto = new UserResponseDto(true, "Пользователь аутентифицирован");
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> performLogin(@RequestBody @Valid UserRequestDto userRequestDto,
                                                        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            ErrorUtil.buildMessageAndThrowValidationException(bindingResult);
        }

        UserResponseDto userResponseDto = authService.login(userRequestDto, request);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> performRegistration(@RequestBody @Valid UserRequestDto userRequestDto,
                                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorUtil.buildMessageAndThrowValidationException(bindingResult);
        }

        Optional<PersonDto> personDto = personService.findByUsername(userRequestDto.getUsername());

        if (personDto.isPresent()) {
            throw new ResourceAlreadyExistsException("Это имя уже занято");
        }

        UserResponseDto userResponseDto = authService.register(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }
}
