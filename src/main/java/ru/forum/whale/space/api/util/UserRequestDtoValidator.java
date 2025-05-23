package ru.forum.whale.space.api.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.forum.whale.space.api.dto.PersonDto;
import ru.forum.whale.space.api.dto.request.UserRequestDto;
import ru.forum.whale.space.api.services.PeopleService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRequestDtoValidator implements Validator {
    private final PeopleService registrationService;

    @Override
    public void validate(Object target, Errors errors) {
        Optional<PersonDto> personDto = registrationService.findByUsername(((UserRequestDto) target).getUsername());
        if (personDto.isPresent()) {
            errors.rejectValue("username", "", "Это имя уже занято");
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRequestDto.class.equals(clazz);
    }
}
