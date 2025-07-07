package ru.forum.whale.space.api.exception;

public class MinioUploadException extends RuntimeException {
    public MinioUploadException(String message) {
        super(message);
    }
}
