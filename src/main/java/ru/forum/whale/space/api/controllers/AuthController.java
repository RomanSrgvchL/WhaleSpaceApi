package ru.forum.whale.space.api.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.request.UserRequestDto;
import ru.forum.whale.space.api.dto.response.UserResponseDto;
import ru.forum.whale.space.api.services.AuthService;
import ru.forum.whale.space.api.util.ErrorsUtil;
import ru.forum.whale.space.api.util.PersonValidator;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PersonValidator personValidator;

    @GetMapping("/check")
    public ResponseEntity<UserResponseDto> checkLoginStatus () {
        UserResponseDto userResponseDto = new UserResponseDto(true, "User is authenticated");
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> performLogin(@RequestBody @Valid UserRequestDto userRequestDto,
                                                        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        UserResponseDto userResponseDto = authService.login(userRequestDto, request);

        return new ResponseEntity<>(userResponseDto,
                userResponseDto.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> performRegistration(@RequestBody @Valid UserRequestDto userRequestDto,
                                                               BindingResult bindingResult) {
        personValidator.validate(userRequestDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        UserResponseDto userResponseDto = authService.register(userRequestDto);

        return new ResponseEntity<>(userResponseDto,
                userResponseDto.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT);
    }

    private ResponseEntity<UserResponseDto> buildValidationErrorResponse(BindingResult bindingResult) {
        StringBuilder errors = new StringBuilder();
        ErrorsUtil.recordErrors(errors, bindingResult);
        return new ResponseEntity<>(new UserResponseDto(false, errors.toString()),
                HttpStatus.BAD_REQUEST);
    }
}
