package ru.forum.whale.space.api.util;

import lombok.Getter;

@Getter
public enum UserSortFields {
    USERNAME("username"),
    CREATED_AT("createdAt"),;

    private final String fieldName;

    UserSortFields(String fieldName) {
        this.fieldName = fieldName;
    }
}
