package cm.kfokam48.presence.exception;

/**
 * Violation d'une règle de gestion : traduite en {code, message} par {@link GestionErreurs}.
 */
public class MetierException extends RuntimeException {

    private final CodeErreur code;

    public MetierException(CodeErreur code) {
        this(code, code.messageParDefaut());
    }

    public MetierException(CodeErreur code, String message) {
        super(message);
        this.code = code;
    }

    public CodeErreur getCode() {
        return code;
    }
}
