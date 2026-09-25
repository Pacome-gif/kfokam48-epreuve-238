package cm.kfokam48.presence.exception;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import cm.kfokam48.presence.dto.ErreurDto;

/**
 * Gestion centralisée des erreurs (B4) : toujours {code, message}, jamais de stack trace.
 */
@RestControllerAdvice
public class GestionErreurs {

    private static final Logger LOG = LoggerFactory.getLogger(GestionErreurs.class);

    @ExceptionHandler(MetierException.class)
    public ResponseEntity<ErreurDto> metier(MetierException e) {
        return reponse(e.getCode().statut(), e.getCode().name(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurDto> validation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> "'" + f.getField() + "' " + f.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));
        return reponse(HttpStatus.BAD_REQUEST, CodeErreur.VALIDATION.name(), "Champ invalide : " + message + ".");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErreurDto> corpsIllisible(HttpMessageNotReadableException e) {
        return reponse(HttpStatus.BAD_REQUEST, CodeErreur.VALIDATION.name(), "Corps JSON absent ou mal formé.");
    }

    @ExceptionHandler({ MissingServletRequestParameterException.class, MissingRequestHeaderException.class,
            MethodArgumentTypeMismatchException.class })
    public ResponseEntity<ErreurDto> parametre(Exception e) {
        return reponse(HttpStatus.BAD_REQUEST, CodeErreur.VALIDATION.name(), "Paramètre manquant ou invalide.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErreurDto> introuvable(NoResourceFoundException e) {
        return reponse(HttpStatus.NOT_FOUND, CodeErreur.RESSOURCE_INTROUVABLE.name(), "Ressource introuvable.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErreurDto> methode(HttpRequestMethodNotSupportedException e) {
        return reponse(HttpStatus.METHOD_NOT_ALLOWED, CodeErreur.RESSOURCE_INTROUVABLE.name(),
                "Méthode " + e.getMethod() + " non disponible sur cette ressource.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurDto> inattendue(Exception e) {
        LOG.error("Erreur inattendue", e);
        return reponse(HttpStatus.INTERNAL_SERVER_ERROR, CodeErreur.ERREUR_INTERNE.name(),
                CodeErreur.ERREUR_INTERNE.messageParDefaut());
    }

    private ResponseEntity<ErreurDto> reponse(HttpStatus statut, String code, String message) {
        return ResponseEntity.status(statut).body(new ErreurDto(code, message));
    }
}
