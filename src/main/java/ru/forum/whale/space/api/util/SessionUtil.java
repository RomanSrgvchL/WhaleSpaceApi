package ru.forum.whale.space.api.util;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.forum.whale.space.api.security.CustomUserDetails;

public final class SessionUtil {
    private SessionUtil() {
    }

    public static long getCurrentUserId() {
        return ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public static Collection<? extends GrantedAuthority> getCurrentAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }
}
