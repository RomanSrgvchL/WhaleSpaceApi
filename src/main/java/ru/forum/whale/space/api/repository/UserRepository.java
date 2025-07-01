package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
