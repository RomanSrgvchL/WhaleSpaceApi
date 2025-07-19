package ru.forum.whale.space.api.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
import ru.forum.whale.space.api.exception.IllegalOperationException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.mapper.DiscussionMsgMapper;
import ru.forum.whale.space.api.model.*;
import ru.forum.whale.space.api.repository.DiscussionRepository;
import ru.forum.whale.space.api.repository.DiscussionMsgRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiscussionMsgService {
    private final DiscussionMsgRepository discussionMsgRepository;
    private final DiscussionRepository discussionRepository;
    private final SessionUtilService sessionUtilService;
    private final MinioService minioService;
    private final DiscussionMsgMapper discussionMsgMapper;

    @Value("${minio.discussion-messages-bucket}")
    private String discussionMessagesBucket;

    @PostConstruct
    private void initChatMessagesBucket() {
        minioService.initBucket(discussionMessagesBucket);
    }

    @Transactional
    public DiscussionMsgDto save(long discussionId, MessageRequestDto messageRequestDto, List<MultipartFile> files) {
        if (files != null && !files.isEmpty()) {
            if (files.size() > 3) {
                throw new IllegalOperationException("Можно прикрепить не более 3 файлов");
            } else {
                for (var file : files) {
                    String contentType = file.getContentType();
                    if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
                        throw new IllegalOperationException("Файлы должен быть формата PNG или JPG/JPEG");
                    }
                }
            }
        }

        User currentUser = sessionUtilService.findCurrentUser();

        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Обсуждение с указанным ID не найдено"));

        String folder = "discussion-" + discussion.getId();

        List<String> fileNames = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            fileNames = minioService.uploadImages(discussionMessagesBucket, files, folder);
        }

        DiscussionMsg discussionMsg = DiscussionMsg.builder()
                .content(messageRequestDto.getContent())
                .sender(currentUser)
                .discussion(discussion)
                .imageFileNames(List.copyOf(fileNames))
                .build();

        return convertToDiscussionMsgDto(discussionMsgRepository.save(discussionMsg));
    }

    private DiscussionMsgDto convertToDiscussionMsgDto(DiscussionMsg discussionMsg) {
        return discussionMsgMapper.discussionMsgToDiscussionMsgDto(discussionMsg);
    }
}
