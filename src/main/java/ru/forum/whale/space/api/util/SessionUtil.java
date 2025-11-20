package ru.forum.whale.space.api.util;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.forum.whale.space.api.security.CustomUserDetails;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SessionUtil {
    public static long getCurrentUserId() {
        return ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public static String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static Collection<? extends GrantedAuthority> getCurrentAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }
}
