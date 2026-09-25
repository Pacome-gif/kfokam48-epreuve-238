package cm.kfokam48.presence.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presence.dto.LigneTableauDto;
import cm.kfokam48.presence.service.TableauService;

@RestController
@RequestMapping("/api/tableau")
public class TableauController {

    private final TableauService tableau;

    public TableauController(TableauService tableau) {
        this.tableau = tableau;
    }

    @GetMapping
    public List<LigneTableauDto> tableau(@RequestParam Long promotionId) {
        return tableau.tableau(promotionId);
    }
}
