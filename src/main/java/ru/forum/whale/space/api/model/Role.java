package ru.forum.whale.space.api.model;

import lombok.Getter;

@Getter
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String prefixRole;

    Role(String role) {
        prefixRole = role;
    }
}
