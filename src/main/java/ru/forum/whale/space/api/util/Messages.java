package ru.forum.whale.space.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Messages {
    public static final String PAGE_POSITIVE_OR_ZERO = "Число страниц должно быть >= 0";
    public static final String SIZE_POSITIVE = "Размер страницы должен быть > 0";

    public static final String ID_POSITIVE = "ID должен быть > 0";

    public static final String MSG_NOT_BLANK = "Сообщение не должно быть пустым";
    public static final String MSG_TOO_LONG = "Длина сообщения не должна превышать 200 символов";
}
