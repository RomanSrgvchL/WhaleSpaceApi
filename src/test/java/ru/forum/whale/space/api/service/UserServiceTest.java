package ru.forum.whale.space.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.forum.whale.space.api.dto.UserDto;
import ru.forum.whale.space.api.dto.UserProfileDto;
import ru.forum.whale.space.api.dto.response.PageResponseDto;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.mapper.UserMapper;
import ru.forum.whale.space.api.model.Gender;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.forum.whale.space.api.util.TestUtil.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionUtilService sessionUtilService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void findAll_thenReturnUserDtoPage() {
        Sort sort = Sort.unsorted();
        int page = 0;
        int size = 1;
        int totalPages = 1;
        long total = 1L;
        boolean isLast = true;

        User user = new User();
        UserDto userDto = new UserDto();

        Page<User> usersPage = new PageImpl<>(List.of(user), PageRequest.of(page, size, sort), total);

        when(userRepository.findAll(PageRequest.of(page, size, sort))).thenReturn(usersPage);
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        PageResponseDto<UserDto> result = userService.findAll(sort, page, size);

        assertEquals(userDto, result.getContent().getFirst());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals(totalPages, result.getTotalPages());
        assertEquals(total, result.getTotalElements());
        assertEquals(isLast, result.isLast());
    }

    @Test
    void findById_whenUserNotFound_thenThrowResourceNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> userService.findById(USER_ID));

        assertEquals("Пользователь не найден", e.getMessage());
    }

    @Test
    void findById_thenReturnUserDto() {
        User user = createUser(USER_ID);
        UserDto expected = new UserDto();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of((user)));
        when(userMapper.userToUserDto(user)).thenReturn(expected);

        UserDto result = userService.findById(USER_ID);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userMapper).userToUserDto(userCaptor.capture());

        assertEquals(expected, result);
        assertEquals(USER_ID, userCaptor.getValue().getId());
    }

    @Test
    void findByUsername_whenUserNotFound_thenThrowResourceNotFoundException() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userService.findByUsername(USERNAME));

        assertEquals("Пользователь не найден", e.getMessage());
    }

    @Test
    void findByUsername_thenReturnUserDto() {
        User user = User.builder()
                .username(USERNAME)
                .build();

        UserDto expected = new UserDto();

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of((user)));
        when(userMapper.userToUserDto(user)).thenReturn(expected);

        UserDto result = userService.findByUsername(USERNAME);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userMapper).userToUserDto(userCaptor.capture());

        assertEquals(expected, result);
        assertEquals(USERNAME, userCaptor.getValue().getUsername());
    }

    @Test
    void findYourself_thenReturnUserDto() {
        User user = new User();
        UserDto expected = new UserDto();

        when(sessionUtilService.findCurrentUser()).thenReturn(user);
        when(userMapper.userToUserDto(user)).thenReturn(expected);

        UserDto result = userService.findYourself();

        assertEquals(expected, result);

        verify(userMapper).userToUserDto(user);
    }

    @Test
    void update_thenReturnUserProfileDto() {
        User currentUser = User.builder()
                .birthDate(LocalDate.parse("2005-01-02"))
                .bio("что-то о себе...")
                .build();

        UserProfileDto userProfileDto = UserProfileDto.builder()
                .birthDate(LocalDate.parse("2005-01-03"))
                .gender(Gender.MALE)
                .build();

        UserProfileDto expected = UserProfileDto.builder()
                .birthDate(userProfileDto.getBirthDate())
                .gender(Gender.MALE)
                .bio(currentUser.getBio())
                .build();

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(userMapper.userToUserProfileDto(currentUser)).thenReturn(expected);

        UserProfileDto result = userService.update(userProfileDto);

        verify(userMapper).updateUserFromUserProfileDto(userProfileDto, currentUser);
        verify(userMapper).userToUserProfileDto(currentUser);

        assertEquals(expected.getGender(), userProfileDto.getGender());
        assertEquals(expected.getGender(), result.getGender());
        assertEquals(expected.getBio(), result.getBio());
    }
}