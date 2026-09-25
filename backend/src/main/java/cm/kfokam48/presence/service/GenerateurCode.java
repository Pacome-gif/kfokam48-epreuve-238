package cm.kfokam48.presence.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

/**
 * Code de présence de 6 caractères, sans caractères ambigus (0/O, 1/I/L).
 */
@Component
public class GenerateurCode {

    static final String ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    static final int LONGUEUR = 6;

    private final SecureRandom aleatoire = new SecureRandom();

    public String generer() {
        StringBuilder code = new StringBuilder(LONGUEUR);
        for (int i = 0; i < LONGUEUR; i++) {
            code.append(ALPHABET.charAt(aleatoire.nextInt(ALPHABET.length())));
        }
        return code.toString();
    }
}
