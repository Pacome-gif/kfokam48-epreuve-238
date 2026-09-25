package cm.kfokam48.presence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import cm.kfokam48.presence.service.NoteRetenue;

/** Test unitaire de RG21 (note retenue, provisoire) et RG14 v2 (moyenne de l'étudiant). */
class NoteRetenueTest {

    @Test
    void deuxRelecturesRenduesDonnentLaMoyenneDefinitive() {
        assertThat(NoteRetenue.calculer(List.of(13, 14), 2)).isEqualTo(new NoteRetenue(13.5, false));
    }

    @Test
    void uneSeuleRelectureSurDeuxDonneUneNoteProvisoire() {
        assertThat(NoteRetenue.calculer(List.of(14), 2)).isEqualTo(new NoteRetenue(14.0, true));
    }

    @Test
    void aucuneRelectureRendueDonneAucuneNote() {
        assertThat(NoteRetenue.calculer(List.of(), 2)).isEqualTo(NoteRetenue.AUCUNE);
    }

    @Test
    void exerciceAnterieurAUnRelecteurEstDefinitifAvecUneNote() {
        assertThat(NoteRetenue.calculer(List.of(15), 1)).isEqualTo(new NoteRetenue(15.0, false)); // H11
    }

    @Test
    void moyenneEtudiantProvisoireSiUneNoteLEst() {
        var moyenne = NoteRetenue.moyenne(List.of(new NoteRetenue(15.0, false), new NoteRetenue(12.0, true),
                NoteRetenue.AUCUNE));

        assertThat(moyenne).isEqualTo(new NoteRetenue(13.5, true));
    }

    @Test
    void moyenneArrondieADeuxDecimales() {
        assertThat(NoteRetenue.calculer(List.of(13, 14, 14), 3).note()).isEqualTo(13.67);
    }
}
