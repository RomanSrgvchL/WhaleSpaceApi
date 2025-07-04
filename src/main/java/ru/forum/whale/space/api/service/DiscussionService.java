package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.DiscussionDto;
import ru.forum.whale.space.api.dto.DiscussionMetaDto;
import ru.forum.whale.space.api.dto.request.DiscussionRequestDto;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Discussion;
import ru.forum.whale.space.api.model.DiscussionMsg;
import ru.forum.whale.space.api.repository.DiscussionRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiscussionService {
    private final DiscussionRepository discussionRepository;
    private final SessionUtilService sessionUtilService;
    private final ModelMapper modelMapper;

    public List<DiscussionMetaDto> findAll(Sort sort) {
        return discussionRepository.findAll(sort).stream()
                .map(this::convertToDiscussionMetaDto)
                .collect(Collectors.toList());
    }

    public DiscussionDto findById(long id) {
        Discussion discussion = discussionRepository.findByIdWithMessages(id)
                .orElseThrow(() -> new ResourceNotFoundException("Обсуждение с указанным ID не найдено"));

        discussion.getMessages().sort(Comparator.comparing(DiscussionMsg::getCreatedAt));
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
                .creator(sessionUtilService.findCurrentUser())
                .build();

        return convertToDiscussionDto(discussionRepository.save(discussion));
    }

    @Transactional
    public void deleteById(Long id) {
        if (discussionRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Обсуждение с указанным ID не найдено");
        }

        discussionRepository.deleteById(id);
    }

    private DiscussionDto convertToDiscussionDto(Discussion discussion) {
        return modelMapper.map(discussion, DiscussionDto.class);
    }

    private DiscussionMetaDto convertToDiscussionMetaDto(Discussion discussion) {
        return modelMapper.map(discussion, DiscussionMetaDto.class);
    }
}
