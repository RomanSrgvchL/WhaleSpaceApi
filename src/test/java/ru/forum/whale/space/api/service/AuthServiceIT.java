package ru.forum.whale.space.api.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import ru.forum.whale.space.api.IntegrationTestBase;
import ru.forum.whale.space.api.dto.request.UserAuthRequestDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.model.Role;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;
import ru.forum.whale.space.api.security.CustomUserDetails;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static ru.forum.whale.space.api.util.TestUtil.*;

class AuthServiceIT extends IntegrationTestBase {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Nested
    class LoginTest {
        private User user;

        @BeforeEach
        void setUp() {
            user = createAndSaveUser();
        }

        @Test
        void login_whenInvalidUsernameOrPassword_thenThrowBadCredentialsException() {
            UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto("INVALID", "INVALID");

            HttpServletRequest request = mock(HttpServletRequest.class);

            assertThrows(BadCredentialsException.class,
                    () -> authService.login(userAuthRequestDto, request));
        }

        @Test
        void login_thenLoginUserAndReturnSuccessfulResponseDto() {
            UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(USERNAME, PASSWORD);

            MockHttpServletRequest request = new MockHttpServletRequest();

            ResponseDto response = authService.login(userAuthRequestDto, request);

            SecurityContext context = (SecurityContext) Objects.requireNonNull(request.getSession())
                    .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

            CustomUserDetails customUserDetails = (CustomUserDetails) context.getAuthentication().getPrincipal();

            assertEquals(customUserDetails.getId(), user.getId());
            assertEquals(customUserDetails.getUsername(), user.getUsername());
            assertEquals(customUserDetails.getPassword(), user.getPassword());
            assertEquals(customUserDetails.getRole(), user.getRole());
            assertTrue(response.isSuccess());
            assertEquals(response.getMessage(), "Вход выполнен успешно!");
        }
    }

    @Nested
    class RegisterTest {
        @Test
        void register_whenUserWithSameUsernameExists_thenThrowBadCredentialsException() {
            createAndSaveUser();

            UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(USERNAME, PASSWORD);

            ResourceAlreadyExistsException e = assertThrows(ResourceAlreadyExistsException.class,
                    () -> authService.register(userAuthRequestDto));

            assertEquals("Это имя уже занято", e.getMessage());
        }

        @Test
        void register_thenSaveUserAndReturnSuccessfulResponseDto() {
            UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(USERNAME, PASSWORD);

            ResponseDto response = authService.register(userAuthRequestDto);

            assertTrue(response.isSuccess());
            assertEquals(response.getMessage(), "Регистрация прошла успешно!");
        }
    }

    private User createAndSaveUser() {
        User user = User.builder()
                .username(USERNAME)
                .password(passwordEncoder.encode(PASSWORD))
                .role(Role.USER.getRoleName())
                .build();

        return userRepository.save(user);
    }
}
