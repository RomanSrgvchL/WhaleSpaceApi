package ru.forum.whale.space.api.exception;

public class CannotDeleteException extends RuntimeException {
    public CannotDeleteException(String message) {
        super(message);
    }
}
