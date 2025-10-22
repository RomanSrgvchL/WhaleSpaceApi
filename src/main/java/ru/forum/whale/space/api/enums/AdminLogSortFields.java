package ru.forum.whale.space.api.enums;

import lombok.Getter;

@Getter
public enum AdminLogSortFields {
    CREATED_AT("createdAt"),;

    private final String fieldName;

    AdminLogSortFields(String fieldName) {
        this.fieldName = fieldName;
    }
}
