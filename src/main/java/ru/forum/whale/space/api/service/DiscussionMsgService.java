package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.dto.request.DiscussionMsgRequestDto;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.*;
import ru.forum.whale.space.api.repository.DiscussionRepository;
import ru.forum.whale.space.api.repository.DiscussionMsgRepository;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiscussionMsgService {
    private final DiscussionMsgRepository discussionMsgRepository;
    private final DiscussionRepository discussionRepository;
    private final ModelMapper modelMapper;
    private final SessionUtilService sessionUtilService;

    @Transactional
    public DiscussionMsgDto save(DiscussionMsgRequestDto discussionMsgRequestDto) {
        User currentUser = sessionUtilService.findCurrentUser();

        Discussion discussion = discussionRepository.findById(discussionMsgRequestDto.getDiscussionId())
                .orElseThrow(() -> new ResourceNotFoundException("Обсуждение не найдено"));

        DiscussionMsg discussionMsg = DiscussionMsg.builder()
                .content(discussionMsgRequestDto.getContent())
                .sender(currentUser)
                .discussion(discussion)
                .createdAt(LocalDateTime.now())
                .build();

        return convertToDiscussionMsgDto(discussionMsgRepository.save(discussionMsg));
    }

    private DiscussionMsgDto convertToDiscussionMsgDto(DiscussionMsg discussionMsg) {
        return modelMapper.map(discussionMsg, DiscussionMsgDto.class);
    }
}
