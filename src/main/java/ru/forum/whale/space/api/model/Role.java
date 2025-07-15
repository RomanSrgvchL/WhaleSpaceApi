package ru.forum.whale.space.api.model;

import lombok.Getter;

@Getter
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    ANONYMOUS("ROLE_ANONYMOUS");

    private final String roleName;

    Role(String role) {
        roleName = role;
    }
}
