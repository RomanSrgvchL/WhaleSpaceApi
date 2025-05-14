package ru.forum.whale.space.api.util;

import org.springframework.validation.BindingResult;

public class ErrorsUtil {
    public static void recordErrors(StringBuilder errors, BindingResult bindingResult) {
        errors.append("Validation errors! ");
        bindingResult.getFieldErrors().forEach(
                error -> errors
                        .append(error.getField())
                        .append(": ")
                        .append(error.getDefaultMessage())
                        .append("; ")
        );
    }
}
