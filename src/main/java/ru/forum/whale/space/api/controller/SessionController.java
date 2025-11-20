package ru.forum.whale.space.api.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.SessionInfoDto;
import ru.forum.whale.space.api.service.SessionService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/auth/session")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<List<SessionInfoDto>> getAll(HttpSession currentSession) {
        return ResponseEntity.ok(sessionService.findUserSessions(currentSession.getId()));
    }

    @DeleteMapping
    public ResponseEntity<Void> logoutFromAllDevicesExceptCurrent(
            @RequestParam(defaultValue = "false") boolean includeCurrent,
            HttpSession currentSession) {
        if (includeCurrent) {
            sessionService.logoutFromAllDevicesIncludeCurrent(currentSession);
        } else {
            sessionService.logoutFromAllDevicesExceptCurrent(currentSession.getId());
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> logoutFromDevice(@PathVariable String sessionId, HttpSession currentSession) {
        sessionService.logoutFromUserDevice(sessionId, currentSession);
        return ResponseEntity.noContent().build();
    }
}
