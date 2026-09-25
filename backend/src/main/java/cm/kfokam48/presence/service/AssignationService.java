package cm.kfokam48.presence.service;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.random.RandomGenerator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presence.domain.Exercice;
import cm.kfokam48.presence.domain.Relecture;
import cm.kfokam48.presence.domain.StatutExercice;
import cm.kfokam48.presence.repository.EtudiantRepository;
import cm.kfokam48.presence.repository.ExerciceRepository;
import cm.kfokam48.presence.repository.PresenceRepository;
import cm.kfokam48.presence.repository.RelectureRepository;

/**
 * EF4 : attribue un relecteur à chaque exercice déposé (RG5, RG6, RG7, H1, H3).
 */
@Service
@Transactional
public class AssignationService {

    private final PresenceRepository presences;
    private final ExerciceRepository exercices;
    private final RelectureRepository relectures;
    private final EtudiantRepository etudiants;
    private final Clock horloge;
    private final RandomGenerator hasard = new SecureRandom();

    public AssignationService(PresenceRepository presences, ExerciceRepository exercices,
            RelectureRepository relectures, EtudiantRepository etudiants, Clock horloge) {
        this.presences = presences;
        this.exercices = exercices;
        this.relectures = relectures;
        this.etudiants = etudiants;
        this.horloge = horloge;
    }

    /** Au dépôt : tire un relecteur si un présent éligible existe, sinon l'exercice reste DEPOSE. */
    public void assigner(Exercice exercice) {
        if (exercice.getStatut() != StatutExercice.DEPOSE) {
            return;
        }
        Long sessionId = exercice.getSession().getId();
        Optional<Long> relecteurId = ChoixRelecteur.choisir(exercice.getEtudiant().getId(),
                presences.idsPresents(sessionId), charges(sessionId), hasard);
        relecteurId.ifPresent(id -> {
            relectures.save(new Relecture(exercice, etudiants.getReferenceById(id), horloge.instant()));
            exercice.mettreEnAttenteDeRelecture();
        });
    }

    /**
     * H1 : à chaque nouvelle présence, les exercices restés sans relecteur retentent leur chance.
     * Transaction propre (bug #21) : si deux présences simultanées visent le même exercice, seule
     * cette attribution échoue, jamais la présence déjà validée.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void assignerEnAttente(Long sessionId) {
        List<Exercice> enAttente = exercices.findBySessionIdAndStatutOrderByDeposeAtAsc(sessionId,
                StatutExercice.DEPOSE);
        enAttente.forEach(this::assigner);
    }

    private Map<Long, Long> charges(Long sessionId) {
        Map<Long, Long> charges = new HashMap<>();
        relectures.flush();
        for (Object[] ligne : relectures.compterParRelecteur(sessionId)) {
            charges.put((Long) ligne[0], (Long) ligne[1]);
        }
        return charges;
    }
}
