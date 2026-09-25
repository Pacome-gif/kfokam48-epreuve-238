package cm.kfokam48.presence.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.springframework.stereotype.Component;

import cm.kfokam48.presence.exception.CodeErreur;
import cm.kfokam48.presence.exception.MetierException;

/** RG13 : le lien d'un exercice est une URL absolue http ou https. */
@Component
public class ValidateurLien {

    static final int LONGUEUR_MAX = 500;

    public String valider(String lien) {
        String nettoye = lien == null ? "" : lien.trim();
        if (nettoye.length() > LONGUEUR_MAX) {
            throw new MetierException(CodeErreur.LIEN_INVALIDE);
        }
        try {
            URI uri = new URI(nettoye);
            String schema = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            if (!(schema.equals("http") || schema.equals("https")) || uri.getHost() == null) {
                throw new MetierException(CodeErreur.LIEN_INVALIDE);
            }
            return nettoye;
        } catch (URISyntaxException e) {
            throw new MetierException(CodeErreur.LIEN_INVALIDE);
        }
    }
}
