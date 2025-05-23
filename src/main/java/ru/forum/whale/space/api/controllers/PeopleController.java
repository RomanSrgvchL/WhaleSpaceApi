package ru.forum.whale.space.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.PersonDto;
import ru.forum.whale.space.api.dto.request.UsernameRequestDto;
import ru.forum.whale.space.api.security.PersonDetails;
import ru.forum.whale.space.api.services.PeopleService;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
public class PeopleController {
    private final PeopleService peopleService;

    @GetMapping
    public ResponseEntity<List<PersonDto>> getAllPeople() {
        return getAllPeople(peopleService.findAll());
    }

    @GetMapping("/createdAtDesc")
    public ResponseEntity<List<PersonDto>> getPeopleByCreatedAtDesc() {
        return getAllPeople(peopleService.findAllByCreatedAtDesc());
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
        return ResponseEntity.ok(people);
    }

    @GetMapping("/yourself")
    public ResponseEntity<PersonDto> getYourself() {
        PersonDetails personDetails = (PersonDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return new ResponseEntity<>(peopleService.convertToPersonDto(personDetails.getPerson()), HttpStatus.OK);
    }

    @PostMapping("/byName")
    public ResponseEntity<PersonDto> getByName(@RequestBody UsernameRequestDto usernameRequestDto) {
        Optional<PersonDto> personDto = peopleService.findByUsername(usernameRequestDto.getUsername());
        return personDto.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
