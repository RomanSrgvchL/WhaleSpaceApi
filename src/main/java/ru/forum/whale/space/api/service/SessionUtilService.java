package ru.forum.whale.space.api.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Role;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;
import ru.forum.whale.space.api.util.SessionUtil;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SessionUtilService {
    private final UserRepository userRepository;

    public User findCurrentUser() {
        return userRepository.findById(SessionUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Текущий пользователь не найден"));
    }

    public Optional<User> findAuthenticatedUser() {
        var authorities = SessionUtil.getCurrentAuthorities();

        boolean isAnonymous = authorities.stream()
                .anyMatch(auth -> Role.ANONYMOUS.getRoleName().equals(auth.getAuthority()));

        if (isAnonymous) {
            return Optional.empty();
        }

        return userRepository.findById(SessionUtil.getCurrentUserId());
    }
}
