package ru.forum.whale.space.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.forum.whale.space.api.dto.PersonDTO;
import ru.forum.whale.space.api.services.PeopleService;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
public class PeopleController {
    private final PeopleService peopleService;

    @GetMapping
    public ResponseEntity<List<PersonDTO>> getAllPeople() {
        return getAllPeople(peopleService.findAll());
    }

    @GetMapping("/createdAtDesc")
    public ResponseEntity<List<PersonDTO>> getPeopleByCreatedAtDesc() {
        return getAllPeople(peopleService.findAllByCreatedAtDesc());
    }

    private ResponseEntity<List<PersonDTO>> getAllPeople(List<PersonDTO> people) {
        Iterator<PersonDTO> iterator = people.iterator();
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
    public ResponseEntity<Map<String, String>> getYourself() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(Map.of("username", username), HttpStatus.OK);
    }
}
