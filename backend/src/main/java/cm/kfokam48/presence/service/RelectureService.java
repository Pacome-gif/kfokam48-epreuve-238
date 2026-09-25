package cm.kfokam48.presence.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presence.domain.Relecture;
import cm.kfokam48.presence.dto.RelectureAssigneeDto;
import cm.kfokam48.presence.dto.RelectureRenduDto;
import cm.kfokam48.presence.exception.CodeErreur;
import cm.kfokam48.presence.exception.MetierException;
import cm.kfokam48.presence.repository.RelectureRepository;

@Service
@Transactional
public class RelectureService {

    static final BigDecimal NOTE_MIN = BigDecimal.ZERO;
    static final BigDecimal NOTE_MAX = BigDecimal.valueOf(20);

    private final RelectureRepository relectures;
    private final ReferentielService referentiel;
    private final Clock horloge;

    public RelectureService(RelectureRepository relectures, ReferentielService referentiel, Clock horloge) {
        this.relectures = relectures;
        this.referentiel = referentiel;
        this.horloge = horloge;
    }

    /** EF5 : le relecteur (identité déclarée, H2) rend sa note et son commentaire. */
    public RelectureAssigneeDto rendre(Long relectureId, Long etudiantId, RelectureRenduDto rendu) {
        int note = noteEntiere(rendu.note()); // RG8
        Relecture relecture = relectures.findById(relectureId)
                .orElseThrow(() -> new MetierException(CodeErreur.RELECTURE_INCONNUE));
        if (relecture.getExercice().getEtudiant().getId().equals(etudiantId)) {
            throw new MetierException(CodeErreur.AUTO_RELECTURE); // RG5
        }
        if (!relecture.getRelecteur().getId().equals(etudiantId)) {
            throw new MetierException(CodeErreur.NON_RELECTEUR); // RG18
        }
        if (relecture.estRendue()) {
            throw new MetierException(CodeErreur.RELECTURE_DEJA_RENDUE); // RG9 (Q15 retenue contre Q10)
        }
        relecture.rendre(note, rendu.commentaire().trim(), horloge.instant());
        return RelectureAssigneeDto.de(relecture);
    }

    /** EF8 : relectures assignées à un étudiant. */
    @Transactional(readOnly = true)
    public List<RelectureAssigneeDto> assigneesA(Long etudiantId) {
        referentiel.etudiant(etudiantId);
        return relectures.findByRelecteurIdOrderByAssigneeAtDesc(etudiantId).stream()
                .map(RelectureAssigneeDto::de)
                .toList();
    }

    /** RG8 : entier de 0 à 20 inclus (12.0 est accepté, 12.5 est refusé). */
    static int noteEntiere(BigDecimal note) {
        boolean entiere = note.signum() == 0 || note.stripTrailingZeros().scale() <= 0;
        if (!entiere || note.compareTo(NOTE_MIN) < 0 || note.compareTo(NOTE_MAX) > 0) {
            throw new MetierException(CodeErreur.NOTE_INVALIDE);
        }
        return note.intValueExact();
    }
}
