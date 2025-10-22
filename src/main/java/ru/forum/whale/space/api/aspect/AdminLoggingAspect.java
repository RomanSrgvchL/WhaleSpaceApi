package ru.forum.whale.space.api.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.Discussion;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.repository.DiscussionRepository;
import ru.forum.whale.space.api.service.AdminLogService;
import ru.forum.whale.space.api.service.SessionUtilService;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminLoggingAspect {
    private final AdminLogService adminLogService;
    private final SessionUtilService sessionUtilService;
    private final ChatRepository chatRepository;
    private final DiscussionRepository discussionRepository;

    private static final String CHAT_MESSAGE_FORMAT = "Пользователь %s отправил сообщение пользователю %s";
    private static final String DISCUSSION_MESSAGE_FORMAT = "Пользователь %s отправил сообщение в обсуждение %s";

    @AfterReturning(pointcut = "@annotation(actionLog))")
    public void logSuccess(JoinPoint joinPoint, EnableActionLogging actionLog) {
        User currentUser = sessionUtilService.findCurrentUser();

        String log_content = switch (actionLog.logType()) {
            case CHAT_MESSAGE -> {
                long chatId = (long) joinPoint.getArgs()[0];
                Chat chat = chatRepository.findById(chatId).orElse(null);

                if (chat == null) {
                    yield CHAT_MESSAGE_FORMAT.formatted(currentUser.getUsername(), "Unknown");
                }

                User recipient = currentUser.getId().equals(chat.getUser1().getId())
                        ? chat.getUser2()
                        : chat.getUser1();

                yield CHAT_MESSAGE_FORMAT.formatted(currentUser.getUsername(), recipient.getUsername());
            }
            case DISCUSSION_MESSAGE -> {
                long discussionId = (long) joinPoint.getArgs()[0];
                Discussion discussion = discussionRepository.findById(discussionId).orElse(null);

                if (discussion == null) {
                    yield DISCUSSION_MESSAGE_FORMAT.formatted(currentUser.getUsername(), "Unknown");
                }

                yield DISCUSSION_MESSAGE_FORMAT.formatted(currentUser.getUsername(), discussion.getTitle());
            }
            case POST -> "Пользователь %s создал пост".formatted(currentUser.getUsername());
            case COMMENT -> "Пользователь %s написал комментарий".formatted(currentUser.getUsername());
        };

        adminLogService.save(
                currentUser,
                log_content,
                actionLog.logType()
        );
    }
}
