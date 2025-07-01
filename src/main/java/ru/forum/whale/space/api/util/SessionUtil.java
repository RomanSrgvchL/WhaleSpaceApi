package ru.forum.whale.space.api.util;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.forum.whale.space.api.security.CustomUserDetails;

public final class SessionUtil {
    private SessionUtil() {
    }

    public static long getCurrentUserId() {
        return ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }
}
