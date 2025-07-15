package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.DiscussionDto;
import ru.forum.whale.space.api.dto.DiscussionMetaDto;
import ru.forum.whale.space.api.dto.request.DiscussionRequestDto;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.mapper.DiscussionMapper;
import ru.forum.whale.space.api.model.Discussion;
import ru.forum.whale.space.api.model.DiscussionMsg;
import ru.forum.whale.space.api.repository.DiscussionRepository;
import ru.forum.whale.space.api.util.StorageBucket;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiscussionService {
    private final DiscussionRepository discussionRepository;
    private final SessionUtilService sessionUtilService;
    private final MinioService minioService;
    private final DiscussionMapper discussionMapper;

    public List<DiscussionMetaDto> findAll(Sort sort) {
        return discussionRepository.findAll(sort).stream()
                .map(this::convertToDiscussionMetaDto)
                .toList();
    }

    public DiscussionDto findById(long id) {
        Discussion discussion = discussionRepository.findByIdWithMessages(id)
                .orElseThrow(() -> new ResourceNotFoundException("Обсуждение с указанным ID не найдено"));

        discussion.getMessages().sort(Comparator.comparing(DiscussionMsg::getCreatedAt));
        return convertToDiscussionDto(discussion);
    }

    @Transactional
    public DiscussionDto save(DiscussionRequestDto discussionRequestDto) {
        if (discussionRepository.existsByTitle(discussionRequestDto.getTitle())) {
            throw new ResourceAlreadyExistsException("Обсуждение с таким названием уже сущесвтует");
        }

        Discussion discussion = Discussion.builder()
                .title(discussionRequestDto.getTitle())
                .creator(sessionUtilService.findCurrentUser())
                .build();

        return convertToDiscussionDto(discussionRepository.save(discussion));
    }

    @Transactional
    public void deleteById(long id) {
        Discussion discussion = discussionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Обсуждение с указанным ID не найдено"));

        for (var message : discussion.getMessages()) {
            minioService.deleteFiles(StorageBucket.DISCUSSION_MESSAGES_BUCKET.getBucketName(),
                    message.getImageFileNames());
        }

        discussionRepository.deleteById(id);
    }

    private DiscussionDto convertToDiscussionDto(Discussion discussion) {
        return discussionMapper.discussionToDiscussionDto(discussion);
    }

    private DiscussionMetaDto convertToDiscussionMetaDto(Discussion discussion) {
        return discussionMapper.discussionToDiscussionMetaDto(discussion);
    }
}
