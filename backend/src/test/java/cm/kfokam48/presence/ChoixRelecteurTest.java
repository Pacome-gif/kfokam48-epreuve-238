package cm.kfokam48.presence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import cm.kfokam48.presence.service.ChoixRelecteur;

/**
 * Test unitaire des règles de tirage du relecteur : RG5 (jamais l'auteur), RG7 (parmi les présents),
 * H3 (le moins chargé d'abord), H1 (aucun candidat).
 */
class ChoixRelecteurTest {

    static final Long AUTEUR = 1L;

    @RepeatedTest(200)
    void lAuteurNestJamaisTire() {
        var choix = ChoixRelecteur.choisir(AUTEUR, List.of(1L, 2L, 3L), Map.of(), new Random());

        assertThat(choix).isPresent();
        assertThat(choix.get()).isNotEqualTo(AUTEUR).isIn(2L, 3L);
    }

    @Test
    void auteurSeulPresentDonneAucunRelecteur() {
        assertThat(ChoixRelecteur.choisir(AUTEUR, List.of(AUTEUR), Map.of(), new Random())).isEmpty();
        assertThat(ChoixRelecteur.choisir(AUTEUR, List.of(), Map.of(), new Random())).isEmpty();
    }

    @RepeatedTest(50)
    void leMoinsChargeEstPrioritaire() {
        var charges = Map.of(2L, 2L, 3L, 0L, 4L, 1L);

        assertThat(ChoixRelecteur.choisir(AUTEUR, List.of(1L, 2L, 3L, 4L), charges, new Random())).contains(3L);
    }
}
