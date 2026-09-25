package cm.kfokam48.presence.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presence.dto.RelectureAssigneeDto;
import cm.kfokam48.presence.service.RelectureService;

@RestController
@RequestMapping("/api/etudiants/{id}")
public class EtudiantController {

    private final RelectureService relectures;

    public EtudiantController(RelectureService relectures) {
        this.relectures = relectures;
    }

    @GetMapping("/relectures")
    public List<RelectureAssigneeDto> relectures(@PathVariable Long id) {
        return relectures.assigneesA(id);
    }
}
