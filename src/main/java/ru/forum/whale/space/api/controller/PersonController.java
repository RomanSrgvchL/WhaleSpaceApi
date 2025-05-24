package ru.forum.whale.space.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.PersonDto;
import ru.forum.whale.space.api.dto.request.PersonRequestDto;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.security.PersonDetails;
import ru.forum.whale.space.api.service.PersonService;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonDto>> getAllPeople() {
        return getAllPeople(personService.findAll());
    }

    @GetMapping("/createdAtDesc")
    public ResponseEntity<List<PersonDto>> getPeopleByCreatedAtDesc() {
        return getAllPeople(personService.findAllByCreatedAtDesc());
    }

    private ResponseEntity<List<PersonDto>> getAllPeople(List<PersonDto> people) {
        Iterator<PersonDto> iterator = people.iterator();
        while (iterator.hasNext()) {
            if (Objects.equals(iterator.next().getUsername(),
                    SecurityContextHolder.getContext().getAuthentication().getName())) {
                iterator.remove();
                break;
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(people);
    }

    @GetMapping("/yourself")
    public ResponseEntity<PersonDto> getYourself() {
        PersonDetails personDetails = (PersonDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(personService.convertToPersonDto(personDetails.getPerson()));
    }

    @PostMapping("/byName")
    public ResponseEntity<PersonDto> getByName(@RequestBody PersonRequestDto personRequestDto) {
        Optional<PersonDto> personDto = personService.findByUsername(personRequestDto.getUsername());
        if (personDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(personDto.get());
        }
        throw new ResourceNotFoundException("Пользователь не найден");
    }
}
