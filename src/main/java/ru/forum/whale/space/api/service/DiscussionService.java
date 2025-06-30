package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.DiscussionDto;
import ru.forum.whale.space.api.dto.DiscussionWithoutRepliesDto;
import ru.forum.whale.space.api.dto.request.DiscussionRequestDto;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Discussion;
import ru.forum.whale.space.api.model.Reply;
import ru.forum.whale.space.api.repository.DiscussionRepository;
import ru.forum.whale.space.api.util.SessionUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiscussionService {
    private final DiscussionRepository discussionRepository;
    private final ModelMapper modelMapper;

    public List<DiscussionWithoutRepliesDto> findAll(Sort sort) {
        return discussionRepository.findAll(sort).stream()
                .map(this::convertToDiscussionWithoutRepliesDto)
                .collect(Collectors.toList());
    }

    public DiscussionDto findById(int id) {
        Discussion discussion = discussionRepository.findByIdWithReplies(id)
                .orElseThrow(() -> new ResourceNotFoundException("Обсуждение с указанным ID не найдено"));

        discussion.getReplies().sort(Comparator.comparing(Reply::getCreatedAt));
        return convertToDiscussionDto(discussion);
    }

    @Transactional
    public DiscussionDto save(DiscussionRequestDto discussionRequestDto) {
        if (discussionRepository.findByTitle(discussionRequestDto.getTitle()).isPresent()) {
            throw new ResourceAlreadyExistsException("Обсуждение с таким названием уже сущесвтует");
        }

        Discussion discussion = Discussion.builder()
                .title(discussionRequestDto.getTitle())
                .createdAt(LocalDateTime.now())
                .creator(SessionUtil.getCurrentUser())
                .build();

        return convertToDiscussionDto(discussionRepository.save(discussion));
    }

    @Transactional
    public void deleteById(Integer id) {
        if (discussionRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Обсуждение с указанным ID не найдено");
        }

        discussionRepository.deleteById(id);
    }

    private DiscussionDto convertToDiscussionDto(Discussion discussion) {
        return modelMapper.map(discussion, DiscussionDto.class);
    }

    private DiscussionWithoutRepliesDto convertToDiscussionWithoutRepliesDto(Discussion discussion) {
        return modelMapper.map(discussion, DiscussionWithoutRepliesDto.class);
    }
}
