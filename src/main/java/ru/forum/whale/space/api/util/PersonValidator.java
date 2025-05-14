package ru.forum.whale.space.api.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.forum.whale.space.api.dto.request.UserRequestDto;
import ru.forum.whale.space.api.models.Person;
import ru.forum.whale.space.api.services.PeopleService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonValidator implements Validator {
    private final PeopleService registrationService;

    @Override
    public void validate(Object target, Errors errors) {
        Optional<Person> person = registrationService.findByUsername(((UserRequestDto) target).getUsername());
        if (person.isPresent()) {
            errors.rejectValue("username", "", "This username is already taken");
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRequestDto.class.equals(clazz);
    }
}
