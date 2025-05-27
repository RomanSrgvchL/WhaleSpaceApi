package ru.forum.whale.space.api.util;

import jakarta.validation.ConstraintViolation;
import org.springframework.validation.BindingResult;
import ru.forum.whale.space.api.exception.CustomValidationException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public final class ErrorUtil {
    private ErrorUtil() {}

    public static void recordErrors(StringBuilder errors, BindingResult bindingResult) {
        bindingResult.getFieldErrors().forEach(
                error -> errors
                        .append(error.getDefaultMessage())
                        .append("\n")
        );
        sortErrorsByLength(errors);
    }

    public static <T> void recordErrors(StringBuilder errors, Set<ConstraintViolation<T>> violations) {
        for (ConstraintViolation<T> violation : violations) {
            errors.append(violation.getMessage()).append("\n");
        }
        sortErrorsByLength(errors);
    }

    private static void sortErrorsByLength(StringBuilder errors) {
        String sortedErrors = Arrays.stream(errors.toString().split("\n"))
                .filter(str -> !str.isBlank())
                .sorted(Comparator.comparingInt(String::length))
                .collect(Collectors.joining("\n"));

        errors.setLength(0);
        errors.append(sortedErrors);
    }

    public static void buildMessageAndThrowValidationException(BindingResult bindingResult) {
        StringBuilder errors = new StringBuilder();
        ErrorUtil.recordErrors(errors, bindingResult);
        throw new CustomValidationException(errors.toString());
    }
}
