package ru.forum.whale.space.api.util;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.forum.whale.space.api.model.Person;
import ru.forum.whale.space.api.security.PersonDetails;

public final class SessionUtil {
    private SessionUtil() {}

    public static Person getCurrentUser() {
        return ((PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
    }
}
