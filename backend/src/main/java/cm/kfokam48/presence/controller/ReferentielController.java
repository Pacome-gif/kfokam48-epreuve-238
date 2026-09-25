package cm.kfokam48.presence.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presence.dto.EtudiantDto;
import cm.kfokam48.presence.dto.PromotionDto;
import cm.kfokam48.presence.service.ReferentielService;

@RestController
@RequestMapping("/api/promotions")
public class ReferentielController {

    private final ReferentielService referentiel;

    public ReferentielController(ReferentielService referentiel) {
        this.referentiel = referentiel;
    }

    @GetMapping
    public List<PromotionDto> promotions() {
        return referentiel.listerPromotions();
    }

    @GetMapping("/{id}/etudiants")
    public List<EtudiantDto> etudiants(@PathVariable Long id) {
        return referentiel.listerEtudiants(id);
    }
}
