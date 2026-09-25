package cm.kfokam48.presence.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presence.dto.PresenceDetailDto;
import cm.kfokam48.presence.dto.PresenceDto;
import cm.kfokam48.presence.dto.PresenceManuelleDto;
import cm.kfokam48.presence.service.PresenceService;
import jakarta.validation.Valid;

/** Présences d'une séance, côté formateur (EF9, Q14). */
@RestController
@RequestMapping("/api/sessions/{id}/presences")
public class PresenceSeanceController {

    private final PresenceService presences;

    public PresenceSeanceController(PresenceService presences) {
        this.presences = presences;
    }

    @GetMapping
    public List<PresenceDetailDto> lister(@PathVariable Long id) {
        return presences.presencesDe(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PresenceDto ajouter(@PathVariable Long id, @Valid @RequestBody PresenceManuelleDto demande) {
        return presences.ajouterManuellement(id, demande.etudiantId());
    }
}
