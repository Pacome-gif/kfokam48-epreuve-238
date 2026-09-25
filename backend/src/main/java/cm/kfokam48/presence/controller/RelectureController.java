package cm.kfokam48.presence.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presence.dto.RelectureAssigneeDto;
import cm.kfokam48.presence.dto.RelectureRenduDto;
import cm.kfokam48.presence.service.RelectureService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/relectures")
public class RelectureController {

    private final RelectureService relectures;

    public RelectureController(RelectureService relectures) {
        this.relectures = relectures;
    }

    /** Identité déclarée dans X-Etudiant-Id (hypothèse H2 du cahier des charges). */
    @PostMapping("/{id}")
    public RelectureAssigneeDto rendre(@PathVariable Long id, @RequestHeader("X-Etudiant-Id") Long etudiantId,
            @Valid @RequestBody RelectureRenduDto rendu) {
        return relectures.rendre(id, etudiantId, rendu);
    }
}
