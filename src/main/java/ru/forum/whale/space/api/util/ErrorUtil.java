package ru.forum.whale.space.api.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public final class ErrorUtil {
    private ErrorUtil() {
    }

    public static void ifHasErrorsBuildMessageAndThrowValidationException(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            ErrorUtil.recordErrors(errors, bindingResult);
            throw new ValidationException(errors.toString());
        }
    }

    public static <T> String buildMessage(Set<ConstraintViolation<T>> violation) {
        StringBuilder errors = new StringBuilder();
        recordErrors(errors, violation);
        return errors.toString();
    }

    private static void recordErrors(StringBuilder errors, BindingResult bindingResult) {
        bindingResult.getFieldErrors().forEach(
                error -> errors
                        .append(error.getDefaultMessage())
                        .append("\n")
        );
        sortErrorsByLength(errors);
    }

    private static <T> void recordErrors(StringBuilder errors, Set<ConstraintViolation<T>> violations) {
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
}
