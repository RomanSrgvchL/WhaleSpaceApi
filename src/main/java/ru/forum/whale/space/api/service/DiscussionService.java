package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.DiscussionDto;
import ru.forum.whale.space.api.dto.request.DiscussionRequestDto;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Discussion;
import ru.forum.whale.space.api.model.Person;
import ru.forum.whale.space.api.model.Reply;
import ru.forum.whale.space.api.repository.DiscussionRepository;
import ru.forum.whale.space.api.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiscussionService {
    private final DiscussionRepository discussionRepository;
    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;

    public Optional<DiscussionDto> findById(int id) {
        Discussion discussion = discussionRepository.findById(id).orElse(null);
        if (discussion != null) {
            Hibernate.initialize(discussion.getReplies());
            discussion.getReplies().sort(Comparator.comparing(Reply::getCreatedAt));
            return Optional.ofNullable(convertToDiscussionDto(discussion));
        }
        return Optional.empty();
    }

    public Optional<DiscussionDto> findByTitle(String title) {
        return discussionRepository.findByTitle(title).map(this::convertToDiscussionDto);
    }

    public List<DiscussionDto> findAll() {
        return discussionRepository.findAll().stream()
                .map(this::convertToDiscussionDto)
                .collect(Collectors.toList());
    }

    public List<DiscussionDto> findAllByCreatedAtDesc() {
        return discussionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToDiscussionDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void save(DiscussionRequestDto discussionRequestDto) {
        Discussion discussion = convertToDiscussion(discussionRequestDto);
        discussion.setCreatedAt(LocalDateTime.now());
        Optional<Person> person = personRepository.findByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName());
        if (person.isPresent()) {
            discussion.setCreator(person.get());
            discussionRepository.save(discussion);
        } else {
            throw new ResourceNotFoundException("Пользоваетель не найден");
        }
    }

    public Discussion convertToDiscussion(DiscussionRequestDto discussionRequestDto) {
        return modelMapper.map(discussionRequestDto, Discussion.class);
    }

    public DiscussionDto convertToDiscussionDto(Discussion discussion) {
        return modelMapper.map(discussion, DiscussionDto.class);
    }
}
