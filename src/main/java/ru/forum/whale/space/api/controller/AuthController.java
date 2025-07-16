package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.docs.auth.CheckAuthDocs;
import ru.forum.whale.space.api.docs.auth.LoginDocs;
import ru.forum.whale.space.api.docs.auth.RegisterDocs;
import ru.forum.whale.space.api.dto.request.UserAuthRequestDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;
import ru.forum.whale.space.api.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Аутентифкационные операции")
public class AuthController {
    private final AuthService authService;

    @CheckAuthDocs
    @GetMapping("/check")
    public ResponseEntity<ResponseDto> checkAuth() {
        ResponseDto response = ResponseDto.buildSuccess("Пользователь аутентифицирован");
        return ResponseEntity.ok(response);
    }

    @LoginDocs
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody @Valid UserAuthRequestDto userAuthRequestDto,
                                             HttpServletRequest request) {
        ResponseDto response = authService.login(userAuthRequestDto, request);
        return ResponseEntity.ok(response);
    }

    @RegisterDocs
    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody @Valid UserAuthRequestDto userAuthRequestDto) {
        ResponseDto response = authService.register(userAuthRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
