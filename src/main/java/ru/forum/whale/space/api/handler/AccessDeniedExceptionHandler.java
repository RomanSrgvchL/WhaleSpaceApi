package ru.forum.whale.space.api.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.io.IOException;

import static org.apache.commons.codec.CharEncoding.UTF_8;

@Component
@RequiredArgsConstructor
public class AccessDeniedExceptionHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;
    private static final String FORBIDDEN = "Недостаточно прав для выполнения данной операции";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        ResponseDto responseDto = ResponseDto.buildFailure(FORBIDDEN);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        objectMapper.writeValue(response.getWriter(), responseDto);
    }
}
