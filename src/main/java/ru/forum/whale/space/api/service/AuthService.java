package ru.forum.whale.space.api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.request.UserRequestDto;
import ru.forum.whale.space.api.dto.response.UserResponseDto;
import ru.forum.whale.space.api.model.Person;
import ru.forum.whale.space.api.repository.PersonRepository;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public UserResponseDto login(UserRequestDto userRequestDto, HttpServletRequest request) {
        var authRequest = new UsernamePasswordAuthenticationToken(userRequestDto.getUsername(),
                userRequestDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authRequest);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        return new UserResponseDto(true, "Вход выполнен успешно!");
    }

    @Transactional
    public UserResponseDto register(UserRequestDto userRequestDto) {
        Person person = convertToPerson(userRequestDto);

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setCreatedAt(LocalDateTime.now());
        person.setRole("ROLE_USER");

        personRepository.save(person);

        return new UserResponseDto(true, "Регистрация прошла успешно!");
    }

    public Person convertToPerson(UserRequestDto userRequestDto) {
        return modelMapper.map(userRequestDto, Person.class);
    }
}
