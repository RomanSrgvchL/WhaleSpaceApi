package ru.forum.whale.space.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {
    public static final String PAGE_MUST_BE_POSITIVE_OR_ZERO = "Число страниц должно быть >= 0";
    public static final String SIZE_MUST_BE_POSITIVE = "Размер страницы должен быть > 0";
}
