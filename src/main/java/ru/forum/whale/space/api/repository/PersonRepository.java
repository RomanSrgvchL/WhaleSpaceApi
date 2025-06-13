package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.Person;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByUsername(String username);
}
