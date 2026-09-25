package cm.kfokam48.presence.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * Règle de tirage du relecteur, sans dépendance à la base (testable unitairement).
 * <ul>
 * <li>RG7 : candidats = étudiants présents à la séance ;</li>
 * <li>RG5 : l'auteur n'est jamais candidat ;</li>
 * <li>H3 : priorité aux candidats qui ont le moins de relectures dans la séance, puis hasard.</li>
 * </ul>
 */
public final class ChoixRelecteur {

    private ChoixRelecteur() {
    }

    public static Optional<Long> choisir(Long auteurId, Collection<Long> presents, Map<Long, Long> charges,
            RandomGenerator hasard) {
        List<Long> candidats = presents.stream().distinct().filter(id -> !id.equals(auteurId)).toList();
        if (candidats.isEmpty()) {
            return Optional.empty(); // H1 : l'exercice attend un prochain présent
        }
        long chargeMin = candidats.stream().mapToLong(id -> charges.getOrDefault(id, 0L)).min().orElse(0);
        List<Long> moinsCharges = candidats.stream()
                .filter(id -> charges.getOrDefault(id, 0L) == chargeMin)
                .toList();
        return Optional.of(moinsCharges.get(hasard.nextInt(moinsCharges.size())));
    }
}
