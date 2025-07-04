package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.request.UserAuthRequestDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;
import ru.forum.whale.space.api.service.AuthService;
import ru.forum.whale.space.api.util.ErrorUtil;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Аутентифкационные операции")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/check")
    public ResponseEntity<ResponseDto> checkLoginStatus() {
        ResponseDto response = ResponseDto.buildSuccess("Пользователь аутентифицирован");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> performLogin(@RequestBody @Valid UserAuthRequestDto userAuthRequestDto,
                                                    BindingResult bindingResult, HttpServletRequest request) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);

        ResponseDto response = authService.login(userAuthRequestDto, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> performRegistration(@RequestBody @Valid UserAuthRequestDto userAuthRequestDto,
                                                           BindingResult bindingResult) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);

        ResponseDto response = authService.register(userAuthRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
