package ru.forum.whale.space.api.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.forum.whale.space.api.dto.response.UserResponseDto;
import ru.forum.whale.space.api.exception.InvalidInputDataException;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.CustomValidationException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<UserResponseDto> handleResourceNotFoundException(ResourceNotFoundException e) {
        UserResponseDto response = new UserResponseDto(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<UserResponseDto> handleCustomValidationException(CustomValidationException e) {
        UserResponseDto response = new UserResponseDto(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidInputDataException.class)
    public ResponseEntity<UserResponseDto> handleInvalidInputDataException(InvalidInputDataException e) {
        UserResponseDto response = new UserResponseDto(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<UserResponseDto> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        UserResponseDto response = new UserResponseDto(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
