package cm.kfokam48.presence.service;

import java.time.Clock;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presence.domain.Etudiant;
import cm.kfokam48.presence.domain.Exercice;
import cm.kfokam48.presence.domain.SessionCours;
import cm.kfokam48.presence.dto.ExerciceCreeDto;
import cm.kfokam48.presence.dto.ExerciceDepotDto;
import cm.kfokam48.presence.exception.CodeErreur;
import cm.kfokam48.presence.exception.MetierException;
import cm.kfokam48.presence.repository.EtudiantRepository;
import cm.kfokam48.presence.repository.ExerciceRepository;

@Service
@Transactional
public class ExerciceService {

    private final ExerciceRepository exercices;
    private final EtudiantRepository etudiants;
    private final SessionService sessions;
    private final ValidateurLien validateurLien;
    private final Clock horloge;

    public ExerciceService(ExerciceRepository exercices, EtudiantRepository etudiants, SessionService sessions,
            ValidateurLien validateurLien, Clock horloge) {
        this.exercices = exercices;
        this.etudiants = etudiants;
        this.sessions = sessions;
        this.validateurLien = validateurLien;
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
        try {
            Exercice exercice = exercices.saveAndFlush(new Exercice(session, etudiant, lien, horloge.instant()));
            return ExerciceCreeDto.de(exercice);
        } catch (DataIntegrityViolationException doubleEnvoi) {
            throw new MetierException(CodeErreur.EXERCICE_DEJA_DEPOSE);
        }
    }
}
