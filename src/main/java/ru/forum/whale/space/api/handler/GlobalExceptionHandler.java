package ru.forum.whale.space.api.handler;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.forum.whale.space.api.dto.response.UserResponseDto;
import ru.forum.whale.space.api.exception.IllegalOperationException;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<UserResponseDto> handleValidationException(ValidationException e) {
        UserResponseDto response = new UserResponseDto(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalOperationException.class)
    public ResponseEntity<UserResponseDto> handleIllegalOperationException(IllegalOperationException e) {
        UserResponseDto response = new UserResponseDto(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<UserResponseDto> handleResourceNotFoundException(ResourceNotFoundException e) {
        UserResponseDto response = new UserResponseDto(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<UserResponseDto> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        UserResponseDto response = new UserResponseDto(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<UserResponseDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        UserResponseDto response = new UserResponseDto(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<UserResponseDto> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        UserResponseDto response = new UserResponseDto(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
