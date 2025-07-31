package ru.forum.whale.space.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.forum.whale.space.api.dto.DiscussionDto;
import ru.forum.whale.space.api.dto.DiscussionMetaDto;
import ru.forum.whale.space.api.dto.request.DiscussionRequestDto;
import ru.forum.whale.space.api.enums.StorageBucket;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.mapper.DiscussionMapper;
import ru.forum.whale.space.api.model.Discussion;
import ru.forum.whale.space.api.model.DiscussionMsg;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.DiscussionRepository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.forum.whale.space.api.util.TestUtil.DISCUSSION_ID;

@ExtendWith(MockitoExtension.class)
class DiscussionServiceTest {
    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private SessionUtilService sessionUtilService;

    @Mock
    private MinioService minioService;

    @Mock
    private DiscussionMapper discussionMapper;

    @InjectMocks
    private DiscussionService discussionService;

    @Test
    void findAll_thenReturnDiscussionMetaDtoList() {
        Discussion discussion = new Discussion();
        DiscussionMetaDto discussionMetaDto = new DiscussionMetaDto();

        Sort sort = Sort.unsorted();

        when(discussionRepository.findAll(sort)).thenReturn(List.of(discussion));
        when(discussionMapper.discussionToDiscussionMetaDto(discussion)).thenReturn(discussionMetaDto);

        List<DiscussionMetaDto> result = discussionService.findAll(sort);

        assertEquals(1, result.size());
        assertEquals(discussionMetaDto, result.getFirst());
    }

    @Test
    void findById_whenDiscussionNotFound_thenThrowResourceNotFoundException() {
        when(discussionRepository.findByIdWithMessages(DISCUSSION_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> discussionService.findById(DISCUSSION_ID));

        assertEquals("Обсуждение с указанным ID не найдено", e.getMessage());
    }

    @Test
    void findById_thenReturnDiscussionDtoWithSortedMessages() {
        long discussionId = 1L;

        DiscussionMsg msg1 = createDiscussionMsg("2025-01-02T10:00:00Z");
        DiscussionMsg msg2 = createDiscussionMsg("2025-01-01T10:00:00Z");

        Discussion discussion = createDiscussion(discussionId, new ArrayList<>(List.of(msg1, msg2)));

        DiscussionDto expected = new DiscussionDto();

        when(discussionRepository.findByIdWithMessages(discussionId)).thenReturn(Optional.of((discussion)));
        when(discussionMapper.discussionToDiscussionDto(discussion)).thenReturn(expected);

        DiscussionDto result = discussionService.findById(discussionId);

        ArgumentCaptor<Discussion> discussionCaptor = ArgumentCaptor.forClass(Discussion.class);

        verify(discussionMapper).discussionToDiscussionDto(discussionCaptor.capture());

        Discussion foundDiscussionWithSortedMessages  = discussionCaptor.getValue();

        List<DiscussionMsg> messages = foundDiscussionWithSortedMessages.getMessages();

        assertEquals(expected, result);
        assertEquals(msg2, messages.get(0));
        assertEquals(msg1, messages.get(1));
    }

    @Test
    void save_whenDiscussionWithSameTitleExists_thenThrowResourceAlreadyExistsException() {
        DiscussionRequestDto discussionRequestDto = new DiscussionRequestDto("new discussion");

        when(discussionRepository.existsByTitle(discussionRequestDto.getTitle())).thenReturn(true);

        ResourceAlreadyExistsException e = assertThrows(ResourceAlreadyExistsException.class,
                () -> discussionService.save(discussionRequestDto));

        assertEquals("Обсуждение с таким названием уже сущесвтует", e.getMessage());
    }

    @Test
    void save_thenReturnCreatedDiscussionDto() {
        DiscussionRequestDto discussionRequestDto = new DiscussionRequestDto("new discussion");

        User currentUser = new User();

        Discussion discussion = Discussion.builder()
                .title(discussionRequestDto.getTitle())
                .creator(currentUser)
                .build();

        DiscussionDto expected = new DiscussionDto();

        when(discussionRepository.existsByTitle(discussionRequestDto.getTitle())).thenReturn(false);
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(discussionRepository.save(discussion)).thenReturn(discussion);
        when(discussionMapper.discussionToDiscussionDto(discussion)).thenReturn(expected);

        DiscussionDto result = discussionService.save(discussionRequestDto);

        ArgumentCaptor<Discussion> discussionCaptor = ArgumentCaptor.forClass(Discussion.class);

        verify(discussionRepository).save(discussionCaptor.capture());

        Discussion savedDiscussion = discussionCaptor.getValue();

        assertEquals(expected, result);
        assertEquals(discussionRequestDto.getTitle(), savedDiscussion.getTitle());
        assertEquals(currentUser, savedDiscussion.getCreator());
    }

    @Test
    void deleteById_whenDiscussionNotFound_thenThrowResourceNotFoundException() {
        when(discussionRepository.findById(DISCUSSION_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> discussionService.deleteById(DISCUSSION_ID));

        assertEquals("Обсуждение с указанным ID не найдено", e.getMessage());
    }

    @Test
    void deleteById_thenDeleteDiscussionAndFiles() {
        DiscussionMsg msg1 = createDiscussionMsg(List.of("img1.png", "img2.png"));
        DiscussionMsg msg2 = createDiscussionMsg(List.of("img3.png"));

        Discussion discussion = createDiscussion(DISCUSSION_ID, new ArrayList<>(List.of(msg1, msg2)));

        when(discussionRepository.findById(DISCUSSION_ID)).thenReturn(Optional.of(discussion));

        discussionService.deleteById(DISCUSSION_ID);

        verify(minioService).deleteFiles(StorageBucket.DISCUSSION_MESSAGES_BUCKET.getBucketName(),
                msg1.getImageFileNames());
        verify(minioService).deleteFiles(StorageBucket.DISCUSSION_MESSAGES_BUCKET.getBucketName(),
                msg2.getImageFileNames());
        verify(discussionRepository).deleteById(DISCUSSION_ID);
    }

    private Discussion createDiscussion(long discussionId, List<DiscussionMsg> messages) {
        return Discussion.builder()
                .id(discussionId)
                .messages(messages)
                .build();
    }

    private DiscussionMsg createDiscussionMsg(String createdAt) {
        return DiscussionMsg.builder()
                .createdAt(ZonedDateTime.parse(createdAt))
                .build();
    }

    private DiscussionMsg createDiscussionMsg(List<String> imageFileNames) {
        return DiscussionMsg.builder()
                .imageFileNames(imageFileNames)
                .build();
    }
}