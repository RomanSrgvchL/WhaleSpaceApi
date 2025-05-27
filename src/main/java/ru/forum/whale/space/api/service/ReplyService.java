package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.ReplyDto;
import ru.forum.whale.space.api.dto.request.ReplyRequestDto;
import ru.forum.whale.space.api.model.*;
import ru.forum.whale.space.api.repository.DiscussionRepository;
import ru.forum.whale.space.api.repository.PersonRepository;
import ru.forum.whale.space.api.repository.ReplyRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final PersonRepository personRepository;
    private final DiscussionRepository discussionRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Optional<ReplyDto> saveAndReturn(ReplyRequestDto replyRequestDto) {
        Reply reply = convertToReply(replyRequestDto);
        Optional<Person> person = personRepository.findById(replyRequestDto.getSenderId());
        Optional<Discussion> discussion = discussionRepository.findById(replyRequestDto.getDiscussionId());
        if (person.isPresent() && discussion.isPresent()) {
            reply.setSender(person.get());
            reply.setDiscussion(discussion.get());
            reply.setCreatedAt(LocalDateTime.now());
            replyRepository.save(reply);
            return Optional.of(convertToReplyDto(reply));
        }
        return Optional.empty();
    }

    public Reply convertToReply(ReplyRequestDto replyRequestDto) {
        Reply reply = new Reply();
        reply.setContent(replyRequestDto.getContent());
        return reply;
    }

    public ReplyDto convertToReplyDto(Reply reply) {
        return modelMapper.map(reply, ReplyDto.class);
    }
}
