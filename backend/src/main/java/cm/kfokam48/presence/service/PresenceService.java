package cm.kfokam48.presence.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presence.domain.Etudiant;
import cm.kfokam48.presence.domain.Presence;
import cm.kfokam48.presence.domain.SessionCours;
import cm.kfokam48.presence.domain.SourcePresence;
import cm.kfokam48.presence.dto.PresenceDto;
import cm.kfokam48.presence.exception.CodeErreur;
import cm.kfokam48.presence.exception.MetierException;
import cm.kfokam48.presence.repository.EtudiantRepository;
import cm.kfokam48.presence.repository.PresenceRepository;
import cm.kfokam48.presence.repository.SessionRepository;

/**
 * Marquage de présence — l'ordre des contrôles suit le diagramme D3.
 */
@Service
@Transactional
public class PresenceService {

    private final SessionRepository sessions;
    private final EtudiantRepository etudiants;
    private final PresenceRepository presences;
    private final Clock horloge;

    public PresenceService(SessionRepository sessions, EtudiantRepository etudiants, PresenceRepository presences,
            Clock horloge) {
        this.sessions = sessions;
        this.etudiants = etudiants;
        this.presences = presences;
        this.horloge = horloge;
    }

    /** EF2 : l'étudiant saisit le code de la séance. */
    public PresenceDto marquer(String code, Long etudiantId) {
        Etudiant etudiant = etudiants.findById(etudiantId)
                .orElseThrow(() -> new MetierException(CodeErreur.ETUDIANT_INCONNU, HttpStatus.BAD_REQUEST));
        SessionCours session = sessions.findByCode(code.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new MetierException(CodeErreur.CODE_INCONNU));
        Instant maintenant = horloge.instant();
        if (session.estCloturee()) {
            throw new MetierException(CodeErreur.SESSION_CLOTUREE); // RG3
        }
        if (session.codeExpire(maintenant)) {
            throw new MetierException(CodeErreur.CODE_EXPIRE); // RG1
        }
        return enregistrer(session, etudiant, SourcePresence.ETUDIANT, maintenant);
    }

    private PresenceDto enregistrer(SessionCours session, Etudiant etudiant, SourcePresence source,
            Instant maintenant) {
        if (!etudiant.getPromotion().getId().equals(session.getPromotion().getId())) {
            throw new MetierException(CodeErreur.HORS_PROMOTION); // H10
        }
        if (presences.existsBySessionIdAndEtudiantId(session.getId(), etudiant.getId())) {
            throw new MetierException(CodeErreur.DEJA_PRESENT); // RG2
        }
        try {
            Presence presence = presences.saveAndFlush(new Presence(session, etudiant, source, maintenant));
            return PresenceDto.de(presence);
        } catch (DataIntegrityViolationException doubleClic) {
            // RG2 garantie aussi par la contrainte unique, en cas de requêtes simultanées
            throw new MetierException(CodeErreur.DEJA_PRESENT);
        }
    }
}
