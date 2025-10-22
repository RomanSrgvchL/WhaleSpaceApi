package ru.forum.whale.space.api.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.aspect.EnableActionLogging;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.mapper.DiscussionMsgMapper;
import ru.forum.whale.space.api.model.*;
import ru.forum.whale.space.api.repository.DiscussionRepository;
import ru.forum.whale.space.api.repository.DiscussionMsgRepository;
import ru.forum.whale.space.api.enums.StorageBucket;
import ru.forum.whale.space.api.util.FileUtil;

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

    private static final String FOLDER_PATTERN = "discussion-%d";
    public static final String DISCUSSION_MESSAGES_BUCKET = StorageBucket.DISCUSSION_MESSAGES_BUCKET.getBucketName();

    @PostConstruct
    private void initDiscussionMessagesBucket() {
        minioService.initBucket(DISCUSSION_MESSAGES_BUCKET);
    }

    @EnableActionLogging(logType = LogType.DISCUSSION_MESSAGE)
    @Transactional
    public DiscussionMsgDto save(long discussionId, MessageRequestDto messageRequestDto, List<MultipartFile> files) {
        FileUtil.validateFiles(files);

        User currentUser = sessionUtilService.findCurrentUser();

        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Обсуждение с указанным ID не найдено"));

        List<String> fileNames = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            String folder = FOLDER_PATTERN.formatted(discussion.getId());
            fileNames = minioService.uploadImages(DISCUSSION_MESSAGES_BUCKET, files, folder);
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
