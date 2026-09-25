package cm.kfokam48.presence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import cm.kfokam48.presence.service.ChoixRelecteur;

/**
 * Test unitaire des règles de tirage : RG5 (jamais l'auteur), RG6 v2 (deux relecteurs distincts,
 * jamais un relecteur déjà assigné), RG7 (parmi les présents), H3 (le moins chargé d'abord), H1/H12.
 */
class ChoixRelecteurTest {

    static final Long AUTEUR = 1L;
    static final List<Long> AUCUN = List.of();

    @RepeatedTest(200)
    void deuxRelecteursDistinctsJamaisLAuteur() {
        var choix = ChoixRelecteur.choisir(AUTEUR, List.of(1L, 2L, 3L, 4L), AUCUN, Map.of(), 2, new Random());

        assertThat(choix).hasSize(2).doesNotHaveDuplicates().doesNotContain(AUTEUR);
        assertThat(choix).allMatch(id -> List.of(2L, 3L, 4L).contains(id));
    }

    @Test
    void unSeulCandidatDonneUnSeulRelecteur() {
        assertThat(ChoixRelecteur.choisir(AUTEUR, List.of(1L, 2L), AUCUN, Map.of(), 2, new Random()))
                .containsExactly(2L);
    }

    @Test
    void auteurSeulPresentDonneAucunRelecteur() {
        assertThat(ChoixRelecteur.choisir(AUTEUR, List.of(AUTEUR), AUCUN, Map.of(), 2, new Random())).isEmpty();
        assertThat(ChoixRelecteur.choisir(AUTEUR, AUCUN, AUCUN, Map.of(), 2, new Random())).isEmpty();
    }

    @RepeatedTest(50)
    void unRelecteurDejaAssigneNEstJamaisRetire() {
        var choix = ChoixRelecteur.choisir(AUTEUR, List.of(1L, 2L, 3L), List.of(2L), Map.of(), 1, new Random());

        assertThat(choix).containsExactly(3L);
    }

    @RepeatedTest(50)
    void lesMoinsChargesSontPrioritaires() {
        var charges = Map.of(2L, 2L, 3L, 0L, 4L, 1L, 5L, 3L);

        assertThat(ChoixRelecteur.choisir(AUTEUR, List.of(1L, 2L, 3L, 4L, 5L), AUCUN, charges, 2, new Random()))
                .containsExactlyInAnyOrder(3L, 4L);
    }
}
