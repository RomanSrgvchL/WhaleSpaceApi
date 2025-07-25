package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.UserDto;
import ru.forum.whale.space.api.dto.UserProfileDto;
import ru.forum.whale.space.api.dto.response.PageResponseDto;
import ru.forum.whale.space.api.exception.*;
import ru.forum.whale.space.api.mapper.UserMapper;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SessionUtilService sessionUtilService;
    private final UserMapper userMapper;

    public PageResponseDto<UserDto> findAll(Sort sort, int page, int size) {
        Page<UserDto> usersPage = userRepository.findAll(PageRequest.of(page, size, sort))
                .map(this::convertToUserDto);

        return PageResponseDto.<UserDto>builder()
                .content(usersPage.getContent())
                .page(page)
                .size(size)
                .totalPages(usersPage.getTotalPages())
                .totalElements(usersPage.getTotalElements())
                .isLast(usersPage.isLast())
                .build();
    }

    public UserDto findById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        return convertToUserDto(user);
    }

    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        return convertToUserDto(user);
    }

    public UserDto findYourself() {
        return convertToUserDto(sessionUtilService.findCurrentUser());
    }

    @Transactional
    public UserProfileDto update(UserProfileDto userProfileDto) {
        User currentUser = sessionUtilService.findCurrentUser();
        mergeIntoUser(userProfileDto, currentUser);
        return convertToUserProfileDto(currentUser);
    }

    private void mergeIntoUser(UserProfileDto userProfileDto, User user) {
        userMapper.updateUserFromUserProfileDto(userProfileDto, user);
    }

    private UserDto convertToUserDto(User user) {
        return userMapper.userToUserDto(user);
    }

    private UserProfileDto convertToUserProfileDto(User user) {
        return userMapper.userToUserProfileDto(user);
    }
}
