package ru.forum.whale.space.api.enums;

import lombok.Getter;

@Getter
public enum DiscussionSortFields {
    TITLE("title"),
    CREATED_AT("createdAt"),;

    private final String fieldName;

    DiscussionSortFields(String fieldName) {
        this.fieldName = fieldName;
    }
}
