package cm.kfokam48.presence.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presence.dto.ExerciceEtudiantDto;
import cm.kfokam48.presence.dto.RelectureAssigneeDto;
import cm.kfokam48.presence.service.ExerciceService;
import cm.kfokam48.presence.service.RelectureService;

@RestController
@RequestMapping("/api/etudiants/{id}")
public class EtudiantController {

    private final RelectureService relectures;
    private final ExerciceService exercices;

    public EtudiantController(RelectureService relectures, ExerciceService exercices) {
        this.relectures = relectures;
        this.exercices = exercices;
    }

    @GetMapping("/exercices")
    public List<ExerciceEtudiantDto> exercices(@PathVariable Long id) {
        return exercices.exercicesDe(id);
    }

    @GetMapping("/relectures")
    public List<RelectureAssigneeDto> relectures(@PathVariable Long id) {
        return relectures.assigneesA(id);
    }
}
