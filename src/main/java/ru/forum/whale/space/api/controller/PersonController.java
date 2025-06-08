package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.PersonDto;
import ru.forum.whale.space.api.service.PersonService;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Операции с пользователями")
public class PersonController {
    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonDto>> getAll(@RequestParam(value = "sortBy", defaultValue = "")
                                                  String sortBy) {
        List<PersonDto> people;

        if (Objects.equals(sortBy, "createdAtDesc")) {
            people = personService.findAllByCreatedAtDesc();
        } else {
            people = personService.findAll();
        }

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

    @GetMapping("/me")
    public ResponseEntity<PersonDto> getMe() {
        return ResponseEntity.status(HttpStatus.OK).body(personService.findYourself());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<PersonDto> getByName(@PathVariable("username") String name) {
        PersonDto personDto = personService.findByUsername(name);
        return ResponseEntity.status(HttpStatus.OK).body(personDto);
    }
}
