package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.PersonDto;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Person;
import ru.forum.whale.space.api.repository.PersonRepository;
import ru.forum.whale.space.api.security.PersonDetails;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;

    public List<PersonDto> findAll() {
        return findAll(personRepository::findAllByOrderByCreatedAtDesc);
    }

    public List<PersonDto> findAllByCreatedAtDesc() {
        return findAll(personRepository::findAllByOrderByCreatedAtDesc);
    }

    private List<PersonDto> findAll(Supplier<List<Person>> fetcher) {
        return fetcher.get().stream()
                .map(this::convertToPersonDto)
                .collect(Collectors.toList());
    }

    public PersonDto findByUsername(String username) {
        Optional<Person> person = personRepository.findByUsername(username);

        if (person.isEmpty()) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }

        return convertToPersonDto(person.get());
    }

    public PersonDto findYourself() {
        PersonDetails personDetails = (PersonDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return convertToPersonDto(personDetails.getPerson());
    }

    private PersonDto convertToPersonDto(Person person) {
        return modelMapper.map(person, PersonDto.class);
    }
}
