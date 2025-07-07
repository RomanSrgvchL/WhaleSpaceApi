package ru.forum.whale.space.api.exception;

public class MinioDeleteException extends RuntimeException {
    public MinioDeleteException(String message) {
        super(message);
    }
}
