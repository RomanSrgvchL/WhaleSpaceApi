package ru.forum.whale.space.api.exception;

public class InvalidInputDataException extends RuntimeException {
    public InvalidInputDataException(String message) {
        super(message);
    }
}
