package ru.forum.whale.space.api.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.PersonDTO;
import ru.forum.whale.space.api.models.Person;
import ru.forum.whale.space.api.repositories.PeopleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PeopleService {
    private final PeopleRepository peopleRepository;
    private final ModelMapper modelMapper;

    public Optional<Person> findByUsername(String username) {
        return peopleRepository.findByUsername(username);
    }

    public List<PersonDTO> findAll() {
        return peopleRepository.findAll().stream()
                .map(this::convertToPersonDTO)
                .collect(Collectors.toList());
    }

    public List<PersonDTO> findAllByCreatedAtDesc() {
        return peopleRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToPersonDTO)
                .collect(Collectors.toList());
    }

    private PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }
}
