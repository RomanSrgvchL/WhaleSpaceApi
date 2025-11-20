package ru.forum.whale.space.api.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class SessionInfoDto {
    private String sessionId;
    private Instant creationTime;
    private Instant lastAccessTime;
    private String userAgent;
    private String ipAddress;
    private boolean currentSession;
}