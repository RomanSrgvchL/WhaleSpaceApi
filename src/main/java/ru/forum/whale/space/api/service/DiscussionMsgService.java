package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.dto.request.DiscussionMsgRequestDto;
import ru.forum.whale.space.api.model.*;
import ru.forum.whale.space.api.repository.DiscussionRepository;
import ru.forum.whale.space.api.repository.UserRepository;
import ru.forum.whale.space.api.repository.DiscussionMsgRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiscussionMsgService {
    private final DiscussionMsgRepository discussionMsgRepository;
    private final UserRepository userRepository;
    private final DiscussionRepository discussionRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Optional<DiscussionMsgDto> save(DiscussionMsgRequestDto discussionMsgRequestDto) {
        Optional<User> user = userRepository.findById(discussionMsgRequestDto.getSenderId());
        Optional<Discussion> discussion = discussionRepository.findById(discussionMsgRequestDto.getDiscussionId());

        if (user.isPresent() && discussion.isPresent()) {
            DiscussionMsg discussionMsg = DiscussionMsg.builder()
                    .content(discussionMsgRequestDto.getContent())
                    .sender(user.get())
                    .discussion(discussion.get())
                    .createdAt(LocalDateTime.now())
                    .build();

            discussionMsgRepository.save(discussionMsg);

            return Optional.of(convertToDiscussionMsgDto(discussionMsg));
        }

        return Optional.empty();
    }

    private DiscussionMsgDto convertToDiscussionMsgDto(DiscussionMsg discussionMsg) {
        return modelMapper.map(discussionMsg, DiscussionMsgDto.class);
    }
}
