package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;
import ru.forum.whale.space.api.util.SessionUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionUtilService {
    private final UserRepository userRepository;

    public User findCurrentUser() {
        return userRepository.findById(SessionUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Текущий пользователь не найден"));
    }
}
