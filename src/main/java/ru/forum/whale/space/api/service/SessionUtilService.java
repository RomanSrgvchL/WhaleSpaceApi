package ru.forum.whale.space.api.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;
import ru.forum.whale.space.api.util.SessionUtil;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SessionUtilService {

    private final UserRepository userRepository;

    private final SimpleGrantedAuthority anonymous = new SimpleGrantedAuthority("ROLE_ANONYMOUS");

    public User findCurrentUser() {
        return userRepository.findById(SessionUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Текущий пользователь не найден"));
    }

    public Optional<User> findUserWithAnonymous() {
        var authorities = SessionUtil.getCurrentAuthorities();
        if (authorities.contains(anonymous)) {
            return Optional.empty();
        }
        return userRepository.findById(SessionUtil.getCurrentUserId());
    }
}
