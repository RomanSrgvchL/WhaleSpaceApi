package ru.forum.whale.space.api.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthExceptionHandler implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    public static final String BAD_CREDENTIALS = "Неверное имя пользователя или пароль";
    public static final String UNAUTHORIZED = "Пользователь не аутентифицирован";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String message = authException instanceof BadCredentialsException ? BAD_CREDENTIALS : UNAUTHORIZED;

        ResponseDto responseDto = ResponseDto.buildFailure(message);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        objectMapper.writeValue(response.getWriter(), responseDto);
    }
}
