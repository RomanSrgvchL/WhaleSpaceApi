package ru.forum.whale.space.api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import ru.forum.whale.space.api.dto.request.UserAuthRequestDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.model.Role;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.forum.whale.space.api.util.TestUtil.PASSWORD;
import static ru.forum.whale.space.api.util.TestUtil.USERNAME;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Nested
    class LoginTest {
        private HttpServletRequest request;
        private HttpSession oldSession;
        private HttpSession newSession;
        private SecurityContext securityContext;

        @BeforeEach
        void setUp() {
            request = mock(HttpServletRequest.class);
            oldSession = mock(HttpSession.class);
            newSession = mock(HttpSession.class);
            securityContext = mock(SecurityContext.class);
        }

        @Test
        void login_whenOldSessionIsNotNull_thenInvalidateSessionAndLoginUserAndReturnSuccessfulResponseDto() {
            UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(USERNAME, PASSWORD);

            var authRequest = new UsernamePasswordAuthenticationToken(userAuthRequestDto.getUsername(),
                    userAuthRequestDto.getPassword());

            when(authenticationManager.authenticate(authRequest)).thenReturn(authRequest);
            when(request.getSession(false)).thenReturn(oldSession);
            when(request.getSession(true)).thenReturn(newSession);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder
                         = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                ResponseDto response = authService.login(userAuthRequestDto, request);

                verify(oldSession).invalidate();
                verify(newSession).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        securityContext);
                verify(sessionService).saveSessionMetadata(newSession, request);

                assertTrue(response.isSuccess());
                assertEquals("Вход выполнен успешно!", response.getMessage());
            }
        }

        @Test
        void login_whenOldSessionIsNull_thenLoginUserAndReturnSuccessfulResponseDto() {
            UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(USERNAME, PASSWORD);

            var authRequest = new UsernamePasswordAuthenticationToken(userAuthRequestDto.getUsername(),
                    userAuthRequestDto.getPassword());

            when(authenticationManager.authenticate(authRequest)).thenReturn(authRequest);
            when(request.getSession(false)).thenReturn(null);
            when(request.getSession(true)).thenReturn(newSession);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                         mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                ResponseDto response = authService.login(userAuthRequestDto, request);

                verify(oldSession, never()).invalidate();
                verify(newSession).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        securityContext);
                verify(sessionService).saveSessionMetadata(newSession, request);

                assertTrue(response.isSuccess());
                assertEquals("Вход выполнен успешно!", response.getMessage());
            }
        }
    }

    @Nested
    class RegisterTest {
        @Test
        void register_whenUserWithSameUsernameExists_thenThrowResourceAlreadyExistsException() {
            UserAuthRequestDto userAuthRequestDto = UserAuthRequestDto.builder()
                    .username(USERNAME)
                    .build();

            when(userRepository.existsByUsername(userAuthRequestDto.getUsername())).thenReturn(true);

            ResourceAlreadyExistsException e = assertThrows(ResourceAlreadyExistsException.class,
                    () -> authService.register(userAuthRequestDto));

            assertEquals("Это имя уже занято", e.getMessage());
        }

        @Test
        void register_thenSaveUserAndReturnSuccessfulResponseDto() {
            UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(USERNAME, PASSWORD);

            User user = User.builder()
                    .username(userAuthRequestDto.getUsername())
                    .password(userAuthRequestDto.getPassword())
                    .role(Role.USER.getRoleName())
                    .build();

            when(userRepository.existsByUsername(userAuthRequestDto.getUsername())).thenReturn(false);
            when(passwordEncoder.encode(userAuthRequestDto.getPassword())).thenReturn(userAuthRequestDto.getPassword());

            ResponseDto response = authService.register(userAuthRequestDto);

            verify(userRepository).save(user);

            assertTrue(response.isSuccess());
            assertEquals("Регистрация прошла успешно!", response.getMessage());
        }
    }
}
