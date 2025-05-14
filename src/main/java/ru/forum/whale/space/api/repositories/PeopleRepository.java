package ru.forum.whale.space.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.models.Person;

import java.util.List;
import java.util.Optional;

public interface PeopleRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByUsername(String username);
    List<Person> findAllByOrderByCreatedAtDesc();
}
