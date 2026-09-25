package cm.kfokam48.presence.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presence.dto.PresenceDemandeDto;
import cm.kfokam48.presence.dto.PresenceDto;
import cm.kfokam48.presence.service.PresenceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/presences")
public class PresenceController {

    private final PresenceService presences;

    public PresenceController(PresenceService presences) {
        this.presences = presences;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PresenceDto marquer(@Valid @RequestBody PresenceDemandeDto demande) {
        return presences.marquer(demande.code(), demande.etudiantId());
    }
}
