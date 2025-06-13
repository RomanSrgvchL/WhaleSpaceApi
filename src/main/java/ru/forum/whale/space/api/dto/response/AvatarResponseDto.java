package ru.forum.whale.space.api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AvatarResponseDto extends UserResponseDto {
    private String avatarUrl;

    public AvatarResponseDto(boolean success, String message) {
        super(success, message);
    }

    public AvatarResponseDto(boolean success, String message, String avatarUrl) {
        super(success, message);
        this.avatarUrl = avatarUrl;
    }
}
