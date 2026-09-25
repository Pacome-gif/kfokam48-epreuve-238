package cm.kfokam48.presence.service;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presence.domain.Etudiant;
import cm.kfokam48.presence.domain.Exercice;
import cm.kfokam48.presence.domain.Relecture;
import cm.kfokam48.presence.domain.SessionCours;
import cm.kfokam48.presence.dto.ExerciceCreeDto;
import cm.kfokam48.presence.dto.ExerciceDepotDto;
import cm.kfokam48.presence.dto.ExerciceEtudiantDto;
import cm.kfokam48.presence.exception.CodeErreur;
import cm.kfokam48.presence.exception.MetierException;
import cm.kfokam48.presence.repository.EtudiantRepository;
import cm.kfokam48.presence.repository.ExerciceRepository;
import cm.kfokam48.presence.repository.RelectureRepository;

@Service
@Transactional
public class ExerciceService {

    private final ExerciceRepository exercices;
    private final RelectureRepository relectures;
    private final EtudiantRepository etudiants;
    private final ReferentielService referentiel;
    private final SessionService sessions;
    private final ValidateurLien validateurLien;
    private final AssignationService assignation;
    private final Clock horloge;

    public ExerciceService(ExerciceRepository exercices, RelectureRepository relectures, EtudiantRepository etudiants,
            ReferentielService referentiel, SessionService sessions, ValidateurLien validateurLien,
            AssignationService assignation, Clock horloge) {
        this.exercices = exercices;
        this.relectures = relectures;
        this.etudiants = etudiants;
        this.referentiel = referentiel;
        this.sessions = sessions;
        this.validateurLien = validateurLien;
        this.assignation = assignation;
        this.horloge = horloge;
    }

    /** EF3 : dépôt du lien d'exercice pour une séance. */
    public ExerciceCreeDto deposer(ExerciceDepotDto demande) {
        Etudiant etudiant = etudiants.findById(demande.etudiantId())
                .orElseThrow(() -> new MetierException(CodeErreur.ETUDIANT_INCONNU, HttpStatus.BAD_REQUEST));
        SessionCours session = sessions.session(demande.sessionId());
        String lien = validateurLien.valider(demande.lien()); // RG13
        if (session.estCloturee()) {
            throw new MetierException(CodeErreur.SESSION_CLOTUREE); // RG10 : dépôt possible jusqu'à la clôture
        }
        if (exercices.existsBySessionIdAndEtudiantId(session.getId(), etudiant.getId())) {
            throw new MetierException(CodeErreur.EXERCICE_DEJA_DEPOSE); // RG12
        }
        Exercice exercice;
        try {
            exercice = exercices.saveAndFlush(new Exercice(session, etudiant, lien, horloge.instant()));
        } catch (DataIntegrityViolationException doubleEnvoi) {
            throw new MetierException(CodeErreur.EXERCICE_DEJA_DEPOSE);
        }
        assignation.assigner(exercice); // EF4
        return ExerciceCreeDto.de(exercice);
    }

    /** EF12 : exercices d'un étudiant avec la note retenue, provisoire ou non (RG21), sans relecteur (RG16). */
    @Transactional(readOnly = true)
    public List<ExerciceEtudiantDto> exercicesDe(Long etudiantId) {
        referentiel.etudiant(etudiantId); // 404 ETUDIANT_INCONNU
        List<Exercice> siens = exercices.findByEtudiantIdOrderByDeposeAtDesc(etudiantId);
        Map<Long, List<Relecture>> rendues = relectures
                .findByExerciceIdInAndRendueAtIsNotNullOrderByRendueAtAsc(siens.stream().map(Exercice::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(r -> r.getExercice().getId()));
        return siens.stream().map(e -> {
            List<Relecture> sesRelectures = rendues.getOrDefault(e.getId(), List.of());
            NoteRetenue note = NoteRetenue.calculer(sesRelectures.stream().map(Relecture::getNote).toList(),
                    e.getRelecteursRequis());
            return new ExerciceEtudiantDto(e.getId(), e.getSession().getId(), e.getSession().getTitre(), e.getLien(),
                    e.getStatut(), note.note(), note.provisoire(), sesRelectures.size(), e.getRelecteursRequis(),
                    sesRelectures.stream().map(Relecture::getCommentaire).toList());
        }).toList();
    }
}
