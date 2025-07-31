package ru.forum.whale.space.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.forum.whale.space.api.model.Role;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;
import ru.forum.whale.space.api.security.CustomUserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static ru.forum.whale.space.api.util.TestUtil.PASSWORD;
import static ru.forum.whale.space.api.util.TestUtil.USERNAME;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_whenUserNotFound_thenThrowUsernameNotFoundException() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(USERNAME));

        assertEquals("Пользователь не найден", e.getMessage());
    }

    @Test
    void loadUserByUsername_thenReturnUserDetails() {
        User user = User.builder()
                .id(1L)
                .username(USERNAME)
                .password(PASSWORD)
                .role(Role.USER.getRoleName())
                .build();

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        CustomUserDetails result = (CustomUserDetails) customUserDetailsService.loadUserByUsername(USERNAME);

        assertEquals(user.getId(), result.getId());
        assertEquals(USERNAME, result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
    }
}