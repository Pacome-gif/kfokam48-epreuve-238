package cm.kfokam48.presence.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presence.dto.ExerciceCreeDto;
import cm.kfokam48.presence.dto.ExerciceDepotDto;
import cm.kfokam48.presence.service.ExerciceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exercices")
public class ExerciceController {

    private final ExerciceService exercices;

    public ExerciceController(ExerciceService exercices) {
        this.exercices = exercices;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciceCreeDto deposer(@Valid @RequestBody ExerciceDepotDto demande) {
        return exercices.deposer(demande);
    }
}
