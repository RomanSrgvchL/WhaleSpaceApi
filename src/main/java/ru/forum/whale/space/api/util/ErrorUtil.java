package ru.forum.whale.space.api.util;

import jakarta.validation.ValidationException;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.Comparator;
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

    private static void recordErrors(StringBuilder errors, BindingResult bindingResult) {
        bindingResult.getFieldErrors().forEach(
                error -> errors
                        .append(error.getDefaultMessage())
                        .append("\n")
        );
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
