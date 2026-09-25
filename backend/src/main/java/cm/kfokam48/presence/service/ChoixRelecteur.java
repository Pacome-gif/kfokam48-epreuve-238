package cm.kfokam48.presence.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

/**
 * Règle de tirage des relecteurs, sans dépendance à la base (testable unitairement).
 * <ul>
 * <li>RG7 : candidats = étudiants présents à la séance ;</li>
 * <li>RG5 : l'auteur n'est jamais candidat ;</li>
 * <li>RG6 v2 : relecteurs distincts, jamais un relecteur déjà assigné à cet exercice ;</li>
 * <li>H3 : priorité aux candidats qui ont le moins de relectures dans la séance, puis hasard.</li>
 * </ul>
 */
public final class ChoixRelecteur {

    private ChoixRelecteur() {
    }

    /**
     * @return au plus {@code nombre} relecteurs distincts ; moins s'il n'y a pas assez de candidats (H1, H12).
     */
    public static List<Long> choisir(Long auteurId, Collection<Long> presents, Collection<Long> dejaAssignes,
            Map<Long, Long> charges, int nombre, RandomGenerator hasard) {
        List<Long> candidats = new ArrayList<>(presents.stream()
                .distinct()
                .filter(id -> !id.equals(auteurId) && !dejaAssignes.contains(id))
                .toList());
        Map<Long, Long> chargeCourante = new HashMap<>(charges);
        List<Long> choisis = new ArrayList<>();
        while (choisis.size() < nombre && !candidats.isEmpty()) {
            long chargeMin = candidats.stream().mapToLong(id -> chargeCourante.getOrDefault(id, 0L)).min().orElse(0);
            List<Long> moinsCharges = candidats.stream()
                    .filter(id -> chargeCourante.getOrDefault(id, 0L) == chargeMin)
                    .toList();
            Long elu = moinsCharges.get(hasard.nextInt(moinsCharges.size()));
            choisis.add(elu);
            candidats.remove(elu);
            chargeCourante.merge(elu, 1L, Long::sum);
        }
        return choisis;
    }
}
