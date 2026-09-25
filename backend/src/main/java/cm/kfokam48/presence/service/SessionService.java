package cm.kfokam48.presence.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presence.domain.Promotion;
import cm.kfokam48.presence.domain.SessionCours;
import cm.kfokam48.presence.dto.SessionCreationDto;
import cm.kfokam48.presence.dto.SessionDto;
import cm.kfokam48.presence.dto.SessionOuverteDto;
import cm.kfokam48.presence.exception.CodeErreur;
import cm.kfokam48.presence.exception.MetierException;
import cm.kfokam48.presence.repository.SessionRepository;

@Service
@Transactional
public class SessionService {

    /** RG1 : durée de validité du code de présence (Q2). */
    public static final Duration VALIDITE_CODE = Duration.ofMinutes(15);

    private final SessionRepository sessions;
    private final ReferentielService referentiel;
    private final GenerateurCode generateur;
    private final Clock horloge;

    public SessionService(SessionRepository sessions, ReferentielService referentiel, GenerateurCode generateur,
            Clock horloge) {
        this.sessions = sessions;
        this.referentiel = referentiel;
        this.generateur = generateur;
        this.horloge = horloge;
    }

    /** EF1 : ouvre une séance et génère un code unique qui expire 15 minutes plus tard. */
    public SessionOuverteDto ouvrir(SessionCreationDto demande) {
        Promotion promotion = referentiel.promotion(demande.promotionId());
        Instant ouverture = horloge.instant();
        String code;
        do {
            code = generateur.generer();
        } while (sessions.existsByCode(code));
        SessionCours session = sessions.save(
                new SessionCours(demande.titre().trim(), promotion, code, ouverture, ouverture.plus(VALIDITE_CODE)));
        return new SessionOuverteDto(session.getId(), session.getCode(), session.getOuvertureAt(),
                session.getExpirationAt());
    }

    @Transactional(readOnly = true)
    public List<SessionDto> lister(Long promotionId) {
        referentiel.promotion(promotionId);
        return sessions.findByPromotionIdOrderByOuvertureAtDesc(promotionId).stream().map(SessionDto::de).toList();
    }

    public SessionCours session(Long id) {
        return sessions.findById(id).orElseThrow(() -> new MetierException(CodeErreur.SESSION_INCONNUE));
    }
}
