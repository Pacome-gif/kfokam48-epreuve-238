package cm.kfokam48.presence.service;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * EF4 : attribue deux relecteurs distincts à chaque exercice déposé (RG5, RG6 v2, RG7, H1, H3, H12).
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

    /**
     * Complète les relecteurs manquants d'un exercice avec les présents éligibles.
     * L'exercice est verrouillé : deux attributions simultanées ne dépassent jamais le nombre requis (H14).
     */
    public void assigner(Exercice depose) {
        Exercice exercice = exercices.verrouiller(depose.getId()).orElseThrow();
        if (exercice.getStatut() == StatutExercice.RELU) {
            return;
        }
        List<Long> dejaAssignes = relectures.findByExerciceId(exercice.getId()).stream()
                .map(r -> r.getRelecteur().getId())
                .toList();
        int manquants = exercice.getRelecteursRequis() - dejaAssignes.size();
        if (manquants <= 0) {
            return;
        }
        Long sessionId = exercice.getSession().getId();
        List<Long> nouveaux = ChoixRelecteur.choisir(exercice.getEtudiant().getId(),
                presences.idsPresents(sessionId), dejaAssignes, charges(sessionId), manquants, hasard);
        for (Long id : nouveaux) {
            relectures.save(new Relecture(exercice, etudiants.getReferenceById(id), horloge.instant()));
        }
        if (!nouveaux.isEmpty() && exercice.getStatut() == StatutExercice.DEPOSE) {
            exercice.mettreEnAttenteDeRelecture();
        }
    }

    /**
     * H1, H12 : à chaque nouvelle présence, les exercices à qui il manque des relecteurs retentent leur chance.
     * Transaction propre (bug #21) : si deux présences simultanées visent le même exercice, seule
     * cette attribution échoue, jamais la présence déjà validée.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void assignerEnAttente(Long sessionId) {
        exercices.manquantDeRelecteurs(sessionId).forEach(this::assigner);
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
