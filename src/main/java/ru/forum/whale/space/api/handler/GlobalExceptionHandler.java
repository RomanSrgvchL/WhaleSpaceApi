package ru.forum.whale.space.api.handler;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ru.forum.whale.space.api.dto.response.ResponseDto;
import ru.forum.whale.space.api.exception.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseDto> handleValidationException(ValidationException e) {
        ResponseDto response = ResponseDto.buildFailure(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalOperationException.class)
    public ResponseEntity<ResponseDto> handleIllegalOperationException(IllegalOperationException e) {
        ResponseDto response = ResponseDto.buildFailure(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto> handleResourceNotFoundException(ResourceNotFoundException e) {
        ResponseDto response = ResponseDto.buildFailure(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ResponseDto> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        ResponseDto response = ResponseDto.buildFailure(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        ResponseDto response = ResponseDto.buildFailure(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDto> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        ResponseDto response = ResponseDto.buildFailure(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(GeneralMinioException.class)
    public ResponseEntity<ResponseDto> handleGeneralMinioException(GeneralMinioException e) {
        log.error(e.getMessage());
        ResponseDto response = ResponseDto.buildFailure(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(AvatarUploadException.class)
    public ResponseEntity<ResponseDto> handleAvatarUploadException(AvatarUploadException e) {
        log.error(e.getMessage());
        ResponseDto response = ResponseDto.buildFailure(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(AvatarDeleteException.class)
    public ResponseEntity<ResponseDto> handleAvatarDeleteException(AvatarDeleteException e) {
        log.error(e.getMessage());
        ResponseDto response = ResponseDto.buildFailure(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseDto> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        ResponseDto response = ResponseDto.buildFailure("Размер файла не должен превышать 3 МБ");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }
}
    