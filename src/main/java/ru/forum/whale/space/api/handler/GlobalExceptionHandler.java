package ru.forum.whale.space.api.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ru.forum.whale.space.api.dto.response.ResponseDto;
import ru.forum.whale.space.api.exception.*;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ResponseDto> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ResponseDto.buildFailure(message));
    }

    @ExceptionHandler({
            IllegalOperationException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HandlerMethodValidationException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ResponseDto> handleBadRequestExceptions(Exception e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto> handleResourceNotFoundException(ResourceNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ResponseDto> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler({
            GeneralMinioException.class,
            MinioUploadException.class,
            MinioDeleteException.class
    })
    public ResponseEntity<ResponseDto> handleMinioExceptions(Exception e) {
        log.error(e.getMessage(), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseDto> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, "Размер файла не должен превышать 3 МБ");
    }

    @ExceptionHandler(CannotDeleteException.class)
    public ResponseEntity<ResponseDto> handleCannotDeleteException(CannotDeleteException e) {
        return buildResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorUtil.buildErrorMessage(bindingResult));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto> handleValidationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorUtil.buildErrorMessage(violations));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleException(Exception e) {
        log.error("Неизвестная ошибка: {}", e.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Неизвестная ошибка: " + e.getMessage());
    }
}
