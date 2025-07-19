package ru.forum.whale.space.api.enums;

import lombok.Getter;

@Getter
public enum StorageBucket {
    USER_AVATARS_BUCKET("user-avatars"),
    CHAT_MESSAGES_BUCKET("chat-messages"),
    DISCUSSION_MESSAGES_BUCKET("discussion-messages"),
    POST_FILES_BUCKET("posts");

    private final String bucketName;

    StorageBucket(String bucketName) {
        this.bucketName = bucketName;
    }
}
