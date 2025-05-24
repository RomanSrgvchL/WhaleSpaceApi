package ru.forum.whale.space.api.util;

import jakarta.validation.ConstraintViolation;
import org.springframework.validation.BindingResult;
import ru.forum.whale.space.api.exception.CustomValidationException;

import java.util.Set;

public final class ErrorUtil {
    private ErrorUtil() {}

    public static void recordErrors(StringBuilder errors, BindingResult bindingResult) {
        bindingResult.getFieldErrors().forEach(
                error -> errors
                        .append(error.getDefaultMessage())
                        .append("\n")
        );
    }

    public static <T> void recordErrors(StringBuilder errors, Set<ConstraintViolation<T>> violations) {
        for (ConstraintViolation<T> violation : violations) {
            errors.append(violation.getMessage()).append("\n");
        }
    }

    public static void buildMessageAndThrowValidationException(BindingResult bindingResult) {
        StringBuilder errors = new StringBuilder();
        ErrorUtil.recordErrors(errors, bindingResult);
        throw new CustomValidationException(errors.toString());
    }
}
