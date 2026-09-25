package cm.kfokam48.presence.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presence.dto.SessionCreationDto;
import cm.kfokam48.presence.dto.SessionDto;
import cm.kfokam48.presence.dto.SessionOuverteDto;
import cm.kfokam48.presence.service.SessionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessions;

    public SessionController(SessionService sessions) {
        this.sessions = sessions;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionOuverteDto ouvrir(@Valid @RequestBody SessionCreationDto demande) {
        return sessions.ouvrir(demande);
    }

    @GetMapping
    public List<SessionDto> lister(@RequestParam Long promotionId) {
        return sessions.lister(promotionId);
    }
}
