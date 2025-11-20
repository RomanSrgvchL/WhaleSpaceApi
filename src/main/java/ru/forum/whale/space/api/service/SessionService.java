package ru.forum.whale.space.api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Service;
import ru.forum.whale.space.api.dto.SessionInfoDto;
import ru.forum.whale.space.api.exception.IllegalOperationException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.security.SessionDetails;
import ru.forum.whale.space.api.util.SessionUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final RedisIndexedSessionRepository sessionRepository;

    public void saveSessionMetadata(HttpSession newSession, HttpServletRequest request) {
        if (newSession != null) {
            SessionDetails sessionDetails = SessionDetails.builder()
                    .userAgent(request.getHeader("User-Agent"))
                    .ipAddress(getClientIpAddress(request))
                    .build();

            newSession.setAttribute("SESSION_DETAILS", sessionDetails);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getRemoteAddr();

        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }

        return request.getRemoteAddr();
    }

    public List<SessionInfoDto> findUserSessions(String currentSessionId) {
        List<SessionInfoDto> sessions = new ArrayList<>();

        Map<String, ? extends Session> userSessions = sessionRepository.findByPrincipalName(
                SessionUtil.getCurrentUsername()
        );

        List<? extends Session> sortedSessions = userSessions.values().stream()
                .sorted(Comparator.comparing(Session::getCreationTime).reversed())
                .toList();

        for (Session session : sortedSessions) {
            SessionDetails sessionDetails = session.getAttribute("SESSION_DETAILS");

            SessionInfoDto sessionInfo = SessionInfoDto.builder()
                    .sessionId(session.getId())
                    .creationTime(session.getCreationTime())
                    .lastAccessTime(session.getLastAccessedTime())
                    .userAgent(sessionDetails.getUserAgent())
                    .ipAddress(sessionDetails.getIpAddress())
                    .currentSession(session.getId().equals(currentSessionId))
                    .build();

            sessions.add(sessionInfo);
        }

        return sessions;
    }

    public void logoutFromAllDevicesIncludeCurrent(HttpSession currentSession) {
        String username = SessionUtil.getCurrentUsername();

        Map<String, ? extends Session> userSessions = sessionRepository.findByPrincipalName(username);

        currentSession.invalidate();

        for (Session session : userSessions.values()) {
            if (!session.getId().equals(currentSession.getId())) {
                sessionRepository.deleteById(session.getId());
            }
        }
    }

    public void logoutFromAllDevicesExceptCurrent(String currentSessionId) {
        String username = SessionUtil.getCurrentUsername();

        Map<String, ? extends Session> userSessions = sessionRepository.findByPrincipalName(username);

        for (Session session : userSessions.values()) {
            if (!session.getId().equals(currentSessionId)) {
                sessionRepository.deleteById(session.getId());
            }
        }
    }

    public void logoutFromUserDevice(String sessionId, HttpSession currentSession) {
        String username = SessionUtil.getCurrentUsername();

        Map<String, ? extends Session> userSessions = sessionRepository.findByPrincipalName(username);

        Session targetSession = sessionRepository.findById(sessionId);
        if (targetSession == null) {
            throw new ResourceNotFoundException("Сессия с указанным ID не найдена");
        }

        if (!userSessions.containsKey(sessionId)) {
            throw new IllegalOperationException("Удаление чужой сессии запрещено");
        }

        if (sessionId.equals(currentSession.getId())) {
            currentSession.invalidate();
        } else {
            sessionRepository.deleteById(sessionId);
        }
    }
}
