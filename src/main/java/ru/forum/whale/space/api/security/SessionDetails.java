package ru.forum.whale.space.api.security;

import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Getter
public class SessionDetails implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public String userAgent;
    public String ipAddress;
}
