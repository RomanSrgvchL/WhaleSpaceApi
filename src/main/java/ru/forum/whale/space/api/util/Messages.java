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

    public static final String DISCUSSION_NOT_BLANK = "Тема не должна быть пустой";
    public static final String DISCUSSION_TITLE_RANGE = "Длина темы должна быть в диапазоне от 5 до 100 символов";

    public static final String COMMENT_NOT_BLANK = "Комментарий не должен быть пустым";
    public static final String COMMENT_TOO_LONG = "Длина комментария не должна превышать 1000 символов";

    public static final String POST_NOT_BLANK = "Комментарий не должен быть пустым";
    public static final String POST_TOO_LONG = "Длина поста не должна превышать 2000 символов";
    public static final String POST_IMAGES_LIMIT = "В посте не может быть больше 3 изображений";

    public static final String USERNAME_NOT_BLANK = "Имя пользователя не должно быть пустым";
    public static final String USERNAME_TOO_LONG = "Имя пользователя не должно содержать более 20 символов";
    public static final String USERNAME_CANNOT_CONTAIN = "Имя пользователя не должно содержать символы ; \\ / ? & #";

    public static final String PASSWORD_NOT_BLANK = "Пароль не должен быть пустым";
    public static final String PASSWORD_TOO_LONG = "Пароль не должен содержать более 100 символов";

    public static final String BIO_TOO_LONG = "Био не должен содержать более 120 символов";

    public static final String MSG_IMAGES_LIMIT = "В сообщении не может быть больше 3 изображений";
}
