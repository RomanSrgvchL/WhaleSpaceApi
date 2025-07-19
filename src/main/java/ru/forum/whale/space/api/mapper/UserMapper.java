package ru.forum.whale.space.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.forum.whale.space.api.dto.UserDto;
import ru.forum.whale.space.api.dto.UserProfileDto;
import ru.forum.whale.space.api.model.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto userToUserDto(User user);

    UserProfileDto userToUserProfileDto(User user);

    void updateUserFromUserProfileDto(UserProfileDto userProfileDto, @MappingTarget User user);
}
