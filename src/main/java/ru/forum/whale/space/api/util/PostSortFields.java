package ru.forum.whale.space.api.util;

import lombok.Getter;

@Getter
public enum PostSortFields {
    CREATED_AT("createdAt"),;

    private final String fieldName;

    PostSortFields(String fieldName) {
        this.fieldName = fieldName;
    }
}
