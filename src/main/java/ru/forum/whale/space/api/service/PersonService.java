package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.PersonDto;
import ru.forum.whale.space.api.model.Person;
import ru.forum.whale.space.api.repository.PersonRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;

    public Optional<PersonDto> findById(int id) {
        return personRepository.findById(id).map(this::convertToPersonDto);
    }

    public Optional<PersonDto> findByUsername(String username) {
        return personRepository.findByUsername(username).map(this::convertToPersonDto);
    }

    public List<PersonDto> findAll() {
        return personRepository.findAll().stream()
                .map(this::convertToPersonDto)
                .collect(Collectors.toList());
    }

    public List<PersonDto> findAllByCreatedAtDesc() {
        return personRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToPersonDto)
                .collect(Collectors.toList());
    }

    public PersonDto convertToPersonDto(Person person) {
        return modelMapper.map(person, PersonDto.class);
    }
}
