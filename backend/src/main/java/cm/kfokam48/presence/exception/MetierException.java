package cm.kfokam48.presence.exception;

import org.springframework.http.HttpStatus;

/**
 * Violation d'une règle de gestion : traduite en {code, message} par {@link GestionErreurs}.
 */
public class MetierException extends RuntimeException {

    private final CodeErreur code;
    private final HttpStatus statut;

    public MetierException(CodeErreur code) {
        this(code, code.statut());
    }

    /**
     * Le contrat fixe parfois un statut différent pour un même code selon l'opération
     * (ex. ETUDIANT_INCONNU : 404 sur GET /api/etudiants/{id}/..., 400 dans le corps de POST /api/presences).
     */
    public MetierException(CodeErreur code, HttpStatus statut) {
        super(code.messageParDefaut());
        this.code = code;
        this.statut = statut;
    }

    public CodeErreur getCode() {
        return code;
    }

    public HttpStatus getStatut() {
        return statut;
    }
}
