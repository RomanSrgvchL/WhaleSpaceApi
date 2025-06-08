package ru.forum.whale.space.api.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.DiscussionDto;
import ru.forum.whale.space.api.dto.DiscussionWithoutRepliesDto;
import ru.forum.whale.space.api.dto.request.DiscussionRequestDto;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
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
    private final EntityManager entityManager;

    public List<DiscussionWithoutRepliesDto> findAll() {
        return convertToDiscussionDtoList(discussionRepository.findAll());
    }

    public List<DiscussionWithoutRepliesDto> findAllByCreatedAtDesc() {
        return convertToDiscussionDtoList(discussionRepository.findAllByOrderByCreatedAtDesc());
    }

    public DiscussionDto findById(int id) {
        Discussion discussion = discussionRepository.findByIdWithReplies(id).orElse(null);

        if (discussion != null) {
            discussion.getReplies().sort(Comparator.comparing(Reply::getCreatedAt));
            return convertToDiscussionDto(discussion);
        }

        throw new ResourceNotFoundException("Обсуждение с указанным ID не найдено");
    }

    @Transactional
    public void save(DiscussionRequestDto discussionRequestDto) {
        if (discussionRepository.findByTitle(discussionRequestDto.getTitle()).isPresent()) {
            throw new ResourceAlreadyExistsException("Обсуждение с таким названием уже сущесвтует");
        }

        Optional<Person> person = personRepository.findByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName());

        if (person.isEmpty()) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }

        Discussion discussion = Discussion.builder()
                .title(discussionRequestDto.getTitle())
                .createdAt(LocalDateTime.now())
                .creator(person.get())
                .build();

        discussionRepository.save(discussion);
    }

    @Transactional
    public void deleteById(Integer id) {
        if (discussionRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Обсуждение с указанным ID не найдено");
        }

        discussionRepository.deleteById(id);
    }

    private List<DiscussionWithoutRepliesDto> convertToDiscussionDtoList(List<Discussion> discussions) {
        return discussions.stream()
                .map(this::convertToDiscussionWithoutRepliesDto)
                .collect(Collectors.toList());
    }

    private DiscussionWithoutRepliesDto convertToDiscussionWithoutRepliesDto(Discussion discussion) {
        Session session = entityManager.unwrap(Session.class);
        session.detach(discussion);
        return modelMapper.map(discussion, DiscussionWithoutRepliesDto.class);
    }

    private DiscussionDto convertToDiscussionDto(Discussion discussion) {
        return modelMapper.map(discussion, DiscussionDto.class);
    }
}
