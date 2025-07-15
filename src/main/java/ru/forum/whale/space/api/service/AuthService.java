package ru.forum.whale.space.api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.request.UserAuthRequestDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.model.Role;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public ResponseDto login(UserAuthRequestDto userAuthRequestDto, HttpServletRequest request) {
        var authRequest = new UsernamePasswordAuthenticationToken(userAuthRequestDto.getUsername(),
                userAuthRequestDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authRequest);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession newSession = request.getSession(true);
        newSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        return ResponseDto.buildSuccess("Вход выполнен успешно!");
    }

    @Transactional
    public ResponseDto register(UserAuthRequestDto userAuthRequestDto) {
        if (userRepository.existsByUsername(userAuthRequestDto.getUsername())) {
            throw new ResourceAlreadyExistsException("Это имя уже занято");
        }

        User user = User.builder()
                .username(userAuthRequestDto.getUsername())
                .password(passwordEncoder.encode(userAuthRequestDto.getPassword()))
                .role(Role.USER.getRoleName())
                .build();

        userRepository.save(user);

        return ResponseDto.buildSuccess("Регистрация прошла успешно!");
    }
}
