package ru.forum.whale.space.api.service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.request.ChatMsgRequestDto;
import ru.forum.whale.space.api.exception.IllegalOperationException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.mapper.ChatMsgMapper;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.ChatMsg;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.repository.ChatMsgRepository;

import java.util.List;
import ru.forum.whale.space.api.util.FileUtil;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMsgService {
    private final ChatMsgRepository chatMsgRepository;
    private final ChatRepository chatRepository;
    private final SessionUtilService sessionUtilService;
    private final MinioService minioService;
    private final ChatMsgMapper chatMsgMapper;
    private static final String FOLDER_PATTERN = "chat-%d";

    @Value("${minio.chat-messages-bucket}")
    private String chatMessagesBucket;

    @PostConstruct
    private void initChatMessagesBucket() {
        minioService.initBucket(chatMessagesBucket);
    }

    @Transactional
    public ChatMsgDto save(ChatMsgRequestDto chatMsgRequestDto, List<MultipartFile> files) {
        FileUtil.validateFiles(files);

        User currentUser = sessionUtilService.findCurrentUser();
        long currentUserId = currentUser.getId();

        Chat chat = chatRepository.findById(chatMsgRequestDto.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Чат не найден"));

        if (currentUserId != chat.getUser1().getId() && currentUserId != chat.getUser2().getId()) {
            throw new IllegalOperationException("Доступ к чужому чату запрещён");
        }

        List<String> fileNames = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            String folder = FOLDER_PATTERN.formatted(chat.getId());
            fileNames = minioService.uploadImages(chatMessagesBucket, files, folder);
        }

        ChatMsg chatMsg = ChatMsg.builder()
                .content(chatMsgRequestDto.getContent())
                .sender(currentUser)
                .chat(chat)
                .imageFileNames(List.copyOf(fileNames))
                .build();

        return convertToChatMsgDto(chatMsgRepository.save(chatMsg));
    }

    private ChatMsgDto convertToChatMsgDto(ChatMsg chatMsg) {
        return chatMsgMapper.chatMsgToChatMsgDto(chatMsg);
    }
}
