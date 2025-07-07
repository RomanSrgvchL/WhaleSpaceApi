package ru.forum.whale.space.api.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.request.ChatMsgRequestDto;
import ru.forum.whale.space.api.exception.IllegalOperationException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.ChatMsg;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.repository.ChatMsgRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMsgService {
    private final ChatMsgRepository chatMsgRepository;
    private final ChatRepository chatRepository;
    private final ModelMapper modelMapper;
    private final MinioService minioService;
    private final SessionUtilService sessionUtilService;

    @Value("${minio.chat-messages-bucket}")
    private String chatMessagesBucket;

    @PostConstruct
    private void initChatMessagesBucket() {
        minioService.initBucket(chatMessagesBucket);
    }

    @Transactional
    public ChatMsgDto save(ChatMsgRequestDto chatMsgRequestDto, List<MultipartFile> files) {
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
        long currentUserId = currentUser.getId();

        Chat chat = chatRepository.findById(chatMsgRequestDto.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Чат не найден"));

        if (currentUserId != chat.getUser1().getId() && currentUserId != chat.getUser2().getId()) {
            throw new IllegalOperationException("Доступ к чужому чату запрещён");
        }

        String folder = "chat-" + chat.getId();

        List<String> fileNames = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            fileNames = minioService.uploadMessageFiles(chatMessagesBucket, files, folder);
        }

        ChatMsg chatMsg = ChatMsg.builder()
                .content(chatMsgRequestDto.getContent())
                .sender(currentUser)
                .chat(chat)
                .imageFileNames(List.copyOf(fileNames))
                .createdAt(LocalDateTime.now())
                .build();

        return convertToChatMsgDto(chatMsgRepository.save(chatMsg));
    }

    private ChatMsgDto convertToChatMsgDto(ChatMsg chatMsg) {
        return modelMapper.map(chatMsg, ChatMsgDto.class);
    }
}
