package ru.forum.whale.space.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Role;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;
import ru.forum.whale.space.api.util.SessionUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static ru.forum.whale.space.api.util.TestUtil.CURRENT_USER_ID;
import static ru.forum.whale.space.api.util.TestUtil.createUser;

@ExtendWith(MockitoExtension.class)
class SessionUtilServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionUtilService sessionUtilService;

    @Test
    void findCurrentUser_whenUserNotFound_thenThrowResourceNotFoundException() {
        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.empty());

        try (MockedStatic<SessionUtil> mockedSessionUtil = mockStatic(SessionUtil.class)) {
            mockedSessionUtil.when(SessionUtil::getCurrentUserId).thenReturn(CURRENT_USER_ID);

            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    sessionUtilService::findCurrentUser);

            assertEquals("Текущий пользователь не найден", e.getMessage());
        }
    }

    @Test
    void findCurrentUser_thenReturnCurrentUser() {
        User currentUser = createUser(CURRENT_USER_ID);

        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(currentUser));

        try (MockedStatic<SessionUtil> mockedSessionUtil = mockStatic(SessionUtil.class)) {
            mockedSessionUtil.when(SessionUtil::getCurrentUserId).thenReturn(CURRENT_USER_ID);

            User result = sessionUtilService.findCurrentUser();

            assertEquals(currentUser, result);
        }
    }

    @Test
    void findAuthenticatedUser_whenUserIsAnonymous_thenReturnEmptyOptional() {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(Role.ANONYMOUS.getRoleName()));

        try (MockedStatic<SessionUtil> mockedSessionUtil = mockStatic(SessionUtil.class)) {
            mockedSessionUtil.when(SessionUtil::getCurrentAuthorities).thenReturn(authorities);

            Optional<User> result = sessionUtilService.findAuthenticatedUser();

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void findAuthenticatedUser_whenUserIsNotAnonymous_thenReturnCurrentUser() {
        User currentUser = createUser(CURRENT_USER_ID);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(Role.USER.getRoleName()));

        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(currentUser));

        try (MockedStatic<SessionUtil> mockedSessionUtil = mockStatic(SessionUtil.class)) {
            mockedSessionUtil.when(SessionUtil::getCurrentAuthorities).thenReturn(authorities);
            mockedSessionUtil.when(SessionUtil::getCurrentUserId).thenReturn(CURRENT_USER_ID);

            Optional<User> result = sessionUtilService.findAuthenticatedUser();

            assertTrue(result.isPresent());
            assertEquals(currentUser, result.get());
        }
    }
}