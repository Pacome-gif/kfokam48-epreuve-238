package cm.kfokam48.presence.exception;

import org.springframework.http.HttpStatus;

/**
 * Codes d'erreur du contrat d'API (components.schemas.Erreur.code).
 */
public enum CodeErreur {
    VALIDATION(HttpStatus.BAD_REQUEST, "Requête invalide."),
    PROMOTION_INCONNUE(HttpStatus.NOT_FOUND, "Promotion inconnue."),
    SESSION_INCONNUE(HttpStatus.NOT_FOUND, "Séance inconnue."),
    ETUDIANT_INCONNU(HttpStatus.NOT_FOUND, "Étudiant inconnu."),
    EXERCICE_INCONNU(HttpStatus.NOT_FOUND, "Exercice inconnu."),
    RELECTURE_INCONNUE(HttpStatus.NOT_FOUND, "Relecture inconnue."),
    CODE_INCONNU(HttpStatus.BAD_REQUEST, "Ce code de présence n'existe pas."),
    CODE_EXPIRE(HttpStatus.GONE, "Le code de présence a expiré."),
    SESSION_CLOTUREE(HttpStatus.GONE, "La séance est clôturée."),
    SESSION_DEJA_CLOTUREE(HttpStatus.CONFLICT, "La séance est déjà clôturée."),
    DEJA_PRESENT(HttpStatus.CONFLICT, "Cet étudiant est déjà marqué présent à cette séance."),
    HORS_PROMOTION(HttpStatus.FORBIDDEN, "Cet étudiant n'appartient pas à la promotion de la séance."),
    TROP_DE_TENTATIVES(HttpStatus.TOO_MANY_REQUESTS, "Trop de codes erronés : réessaie dans 2 minutes."),
    LIEN_INVALIDE(HttpStatus.BAD_REQUEST, "Le lien doit être une URL http ou https."),
    EXERCICE_DEJA_DEPOSE(HttpStatus.CONFLICT, "Un exercice est déjà déposé pour cette séance."),
    EXERCICE_DEJA_RELU(HttpStatus.CONFLICT, "L'exercice a déjà été relu : le lien ne peut plus changer."),
    NON_AUTEUR(HttpStatus.FORBIDDEN, "Seul l'auteur peut modifier cet exercice."),
    NOTE_INVALIDE(HttpStatus.BAD_REQUEST, "La note doit être un entier entre 0 et 20."),
    AUTO_RELECTURE(HttpStatus.FORBIDDEN, "Un étudiant ne peut pas relire son propre exercice."),
    NON_RELECTEUR(HttpStatus.FORBIDDEN, "Cette relecture est assignée à un autre étudiant."),
    RELECTURE_DEJA_RENDUE(HttpStatus.CONFLICT, "Cette relecture a déjà été rendue."),
    RESSOURCE_INTROUVABLE(HttpStatus.NOT_FOUND, "Ressource introuvable."),
    ERREUR_INTERNE(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne, réessaie plus tard.");

    private final HttpStatus statut;
    private final String messageParDefaut;

    CodeErreur(HttpStatus statut, String messageParDefaut) {
        this.statut = statut;
        this.messageParDefaut = messageParDefaut;
    }

    public HttpStatus statut() {
        return statut;
    }

    public String messageParDefaut() {
        return messageParDefaut;
    }
}
